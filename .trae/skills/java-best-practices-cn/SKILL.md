---
name: "java-best-practices-cn"
description: "全面的Java编码最佳实践,涵盖线程使用、接口规范、设计模式、缓存、监控、高可用、安全性和性能优化。"
---

# Java编码最佳实践

本指南涵盖构建健壮、可维护和高性能Java应用程序的基本编码最佳实践。

---

## 1. 项目概览

- **语言**: Java 17+
- **框架**: Spring Boot 3.x,Mybatis Plus 3.5.x
- **构建工具**: Maven/Gradle
- **架构**: 微服务与插件系统

---

## 2. 线程使用指南

### 2.1 线程池最佳实践

```java
// 推荐: 使用ThreadPoolExecutor进行正确配置
public class ThreadPoolExample {
    
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CORE_POOL_SIZE * 2;
    private static final int KEEP_ALIVE_TIME = 60;
    private static final TimeUnit KEEP_ALIVE_UNIT = TimeUnit.SECONDS;
    
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_UNIT,
        new LinkedBlockingQueue<>(1000),
        new CustomThreadFactory("worker-%d"),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
    
    public void executeTask(Runnable task) {
        executor.execute(task);
    }
    
    public <T> Future<T> submitTask(Callable<T> task) {
        return executor.submit(task);
    }
    
    // 始终优雅关闭线程池
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

**关键要点:**
- 定义核心线程数、最大线程数、队列容量和存活时间
- 使用自定义ThreadFactory创建有意义的线程名
- 实现拒绝策略(CallerRunsPolicy、AbortPolicy等)
- 始终优雅地关闭线程池

### 2.2 CompletableFuture异步操作

```java
public class AsyncExample {
    
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    // 带回调的异步执行
    public CompletableFuture<Result> processAsync(Data data) {
        return CompletableFuture.supplyAsync(() -> processData(data), executor)
            .thenApply(this::transformResult)
            .thenCompose(this::saveResultAsync)
            .exceptionally(ex -> {
                log.error("处理数据错误", ex);
                return Result.error(ex.getMessage());
            });
    }
    
    // 并行执行多个任务
    public CompletableFuture<List<Result>> processMultipleAsync(List<Data> dataList) {
        List<CompletableFuture<Result>> futures = dataList.stream()
            .map(data -> CompletableFuture.supplyAsync(() -> processData(data), executor))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
```

### 2.3 线程安全最佳实践

```java
public class ThreadSafeExample {
    
    // 使用ConcurrentHashMap实现线程安全的Map操作
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    
    // 使用computeIfAbsent实现线程安全的延迟初始化
    public Object getOrCreate(String key, Supplier<Object> factory) {
        return cache.computeIfAbsent(key, k -> factory.get());
    }
    
    // 使用原子类进行计数
    private final AtomicLong counter = new AtomicLong(0);
    private final AtomicReference<Data> currentData = new AtomicReference<>();
    
    // 读多写少场景使用读写锁
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Map<String, Data> dataMap = new HashMap<>();
    
    public Data read(String key) {
        rwLock.readLock().lock();
        try {
            return dataMap.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }
    
    public void write(String key, Data value) {
        rwLock.writeLock().lock();
        try {
            dataMap.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
    
    // 使用StampedLock实现乐观读
    private final StampedLock stampedLock = new StampedLock();
    
    public Data readOptimistic(String key) {
        long stamp = stampedLock.tryOptimisticRead();
        Data data = dataMap.get(key);
        if (!stampedLock.validate(stamp)) {
            stamp = stampedLock.readLock();
            try {
                data = dataMap.get(key);
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        return data;
    }
}
```

### 2.4 ThreadLocal最佳实践

```java
public class ThreadLocalExample {
    
    private static final ThreadLocal<User> currentUser = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<RequestContext> requestContext = ThreadLocal.withInitial(RequestContext::new);
    
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }
    
    public static User getCurrentUser() {
        return currentUser.get();
    }
    
    public static void clear() {
        currentUser.remove();
        requestContext.remove();
    }
    
    // 重要: 始终清理ThreadLocal以防止内存泄漏
    public class RequestHandler {
        
        public void handleRequest(HttpRequest request) {
            try {
                setCurrentUser(extractUser(request));
                processRequest(request);
            } finally {
                clear(); // 关键: 防止内存泄漏
            }
        }
    }
}
```

---

## 3. 接口规范指南

### 3.1 API接口设计

```java
@RestController
@RequestMapping("/api/v1/resources")
@Api(tags = "资源管理", description = "资源管理相关API")
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;
    
    @GetMapping
    @ApiOperation(value = "查询资源", notes = "分页查询资源")
    public ResponseEntity<PageResult<ResourceDTO>> queryResources(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
        PageResult<ResourceDTO> result = resourceService.queryResources(keyword, pageRequest);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @ApiOperation(value = "创建资源")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResourceDTO> createResource(
            @Valid @RequestBody CreateResourceRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        ResourceDTO created = resourceService.createResource(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "更新资源")
    public ResponseEntity<ResourceDTO> updateResource(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateResourceRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        ResourceDTO updated = resourceService.updateResource(id, request, currentUser);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除资源")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteResource(
            @PathVariable @Positive Long id,
            @RequestAttribute("currentUser") User currentUser) {
        
        resourceService.deleteResource(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
```

### 3.2 Service接口设计

```java
public interface ResourceService {
    PageResult<ResourceDTO> queryResources(String keyword, PageRequest pageRequest);
    ResourceDTO createResource(CreateResourceRequest request, User currentUser);
    ResourceDTO updateResource(Long id, UpdateResourceRequest request, User currentUser);
    void deleteResource(Long id, User currentUser);
}

@Service
@Transactional(readOnly = true)
public class ResourceServiceImpl implements ResourceService {
    
    @Autowired
    private ResourceMapper resourceMapper;
    
    @Override
    public PageResult<ResourceDTO> queryResources(String keyword, PageRequest pageRequest) {
        Page<Resource> page = resourceMapper.queryResources(keyword, pageRequest);
        return PageResult.of(page.map(this::toDTO));
    }
    
    @Override
    @Transactional
    public ResourceDTO createResource(CreateResourceRequest request, User currentUser) {
        if (resourceMapper.existsByName(request.getName())) {
            throw new ServiceException(Status.RESOURCE_EXIST);
        }
        
        Resource resource = toEntity(request);
        resource.setCreator(currentUser.getId());
        resource.setCreateTime(LocalDateTime.now());
        resourceMapper.insert(resource);
        
        log.info("资源创建成功, id: {}", resource.getId());
        return toDTO(resource);
    }
    
    private ResourceDTO toDTO(Resource resource) {
        ResourceDTO dto = new ResourceDTO();
        BeanUtils.copyProperties(resource, dto);
        return dto;
    }
    
    private Resource toEntity(CreateResourceRequest request) {
        Resource resource = new Resource();
        BeanUtils.copyProperties(request, resource);
        return resource;
    }
}
```

### 3.3 DTO模式

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "资源名称不能为空")
    @Size(min = 1, max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @NotNull
    private ResourceType type;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String description;
    private ResourceType type;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<T> data;
    private long total;
    private int pageNo;
    private int pageSize;
    private int totalPages;
    
    public static <T> PageResult<T> of(Page<T> page) {
        return PageResult.<T>builder()
            .data(page.getContent())
            .total(page.getTotalElements())
            .pageNo(page.getNumber() + 1)
            .pageSize(page.getSize())
            .totalPages(page.getTotalPages())
            .build();
    }
}
```

---

## 4. 设计模式

### 4.1 单例模式

```java
// 线程安全的单例模式 - Bill Pugh Singleton
public class SingletonExample {
    
    private SingletonExample() {}
    
    private static class SingletonHolder {
        private static final SingletonExample INSTANCE = new SingletonExample();
    }
    
    public static SingletonExample getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    // 枚举单例
    public enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {}
    }
}
```

### 4.2 工厂模式

```java
public interface MessageHandler {
    void handle(Message message);
}

@Service
public class MessageHandlerFactory {
    
    private final Map<MessageType, MessageHandler> handlers = new ConcurrentHashMap<>();
    
    @Autowired
    public MessageHandlerFactory(List<MessageHandler> handlerList) {
        for (MessageHandler handler : handlerList) {
            handlers.put(determineType(handler), handler);
        }
    }
    
    public MessageHandler getHandler(MessageType type) {
        MessageHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("No handler for type: " + type);
        }
        return handler;
    }
}
```

### 4.3 策略模式

```java
public interface PaymentStrategy {
    PaymentResult pay(PaymentRequest request);
    boolean supports(PaymentType type);
}

@Service
public class PaymentService {
    
    private final List<PaymentStrategy> strategies;
    
    @Autowired
    public PaymentService(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList;
    }
    
    public PaymentResult processPayment(PaymentRequest request) {
        PaymentStrategy strategy = strategies.stream()
            .filter(s -> s.supports(request.getType()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("不支持的支付方式"));
        
        return strategy.pay(request);
    }
}
```

### 4.4 观察者模式(事件驱动)

```java
public class ResourceCreatedEvent extends ApplicationEvent {
    private final Long resourceId;
    private final String resourceName;
    
    public ResourceCreatedEvent(Object source, Long resourceId, String resourceName) {
        super(source);
        this.resourceId = resourceId;
        this.resourceName = resourceName;
    }
}

@Component
public class ResourceEventListener {
    
    @EventListener
    @Async
    public void handleResourceCreated(ResourceCreatedEvent event) {
        log.info("资源已创建: id={}, name={}", event.getResourceId(), event.getResourceName());
    }
}

@Service
public class ResourceEventPublisher {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void publishResourceCreated(Long resourceId, String resourceName) {
        eventPublisher.publishEvent(new ResourceCreatedEvent(this, resourceId, resourceName));
    }
}
```

### 4.5 模板方法模式

```java
public abstract class AbstractDataProcessor {
    
    public final void process() {
        validate();
        preProcess();
        doProcess();
        postProcess();
        cleanup();
    }
    
    protected void validate() {}
    protected void preProcess() {}
    protected abstract void doProcess();
    protected void postProcess() {}
    protected void cleanup() {}
}
```

### 4.6 构建器模式

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private UserStatus status;
}

// 使用
User user = User.builder()
    .id(1L)
    .username("john")
    .email("john@example.com")
    .status(UserStatus.ACTIVE)
    .build();
```

### 4.7 装饰器模式

```java
@Component
@Primary
public class CachedDataService implements DataService {
    
    @Autowired
    private BasicDataService basicService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Override
    public Data getData(String id) {
        Cache.ValueWrapper wrapper = cache.get("data:" + id);
        if (wrapper != null) {
            return (Data) wrapper.get();
        }
        
        Data data = basicService.getData(id);
        if (data != null) {
            cache.put("data:" + id, data);
        }
        return data;
    }
}
```

---

## 5. 缓存最佳实践

### 5.1 Spring缓存注解

```java
@Service
public class CacheExample {
    
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }
    
    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearUserCache() {}
}
```

### 5.2 多级缓存

```java
@Service
public class MultiLevelCacheService {
    
    // L1: 本地缓存 (Caffeine)
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .recordStats()
        .build();
    
    // L2: 分布式缓存 (Redis)
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final Duration REDIS_TTL = Duration.ofMinutes(30);
    
    public <T> T get(String key, Class<T> type, Supplier<T> loader) {
        T value = (T) localCache.getIfPresent(key);
        if (value != null) return value;
        
        value = (T) redisTemplate.opsForValue().get(key);
        if (value != null) {
            localCache.put(key, value);
            return value;
        }
        
        value = loader.get();
        if (value != null) {
            localCache.put(key, value);
            redisTemplate.opsForValue().set(key, value, REDIS_TTL);
        }
        return value;
    }
}
```

---

## 6. 监控最佳实践

### 6.1 使用Micrometer收集指标

```java
@Component
public class MetricsExample {
    
    private final MeterRegistry meterRegistry;
    
    @Autowired
    public MetricsExample(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    // Counter - 用于计数事件
    private Counter requestCounter = Counter.builder("http.requests")
        .tag("endpoint", "/api/users")
        .description("HTTP请求总数")
        .register(meterRegistry);
    
    // Timer - 用于测量执行时间
    private Timer requestTimer = Timer.builder("http.request.duration")
        .tag("endpoint", "/api/users")
        .register(meterRegistry);
    
    // Histogram - 用于百分位分布
    private DistributionSummary requestSize = DistributionSummary.builder("http.request.size")
        .baseUnit("bytes")
        .publishPercentiles(0.5, 0.75, 0.95, 0.99)
        .register(meterRegistry);
    
    public void recordRequest(String endpoint, long durationMs, long size) {
        requestCounter.increment();
        requestTimer.record(durationMs, TimeUnit.MILLISECONDS);
        requestSize.record(size);
    }
}
```

### 6.2 健康检查

```java
@Component
public class HealthCheckExample implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try {
            dataSource.getConnection().close();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "不可用")
                .withDetail("error", e.getMessage())
                .build();
        }
        
        return Health.up()
            .withDetail("database", "可用")
            .build();
    }
}
```

### 6.3 分布式追踪

```java
@Service
public class TracingExample {
    
    @Autowired
    private Tracer tracer;
    
    public void processWithTrace(String operationName) {
        Span span = tracer.spanBuilder(operationName).startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            processBusiness();
            span.addEvent("业务处理完成");
        } catch (Exception e) {
            span.setStatus(Status.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

---

## 7. 高可用性

### 7.1 断路器模式

```java
@Service
public class CircuitBreakerExample {
    
    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;
    
    private CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("remoteService");
    
    public String callRemoteService() {
        Supplier<String> supplier = CircuitBreaker.decorateSupplier(
            circuitBreaker, 
            () -> remoteService.call()
        );
        
        return Try.ofSupplier(supplier)
            .recover(throwable -> "降级响应")
            .get();
    }
    
    @CircuitBreaker(name = "remoteService", fallbackMethod = "fallback")
    public String callWithAnnotation() {
        return remoteService.call();
    }
    
    private String fallback(Exception e) {
        return "降级响应";
    }
}
```

### 7.2 重试模式

```java
@Service
public class RetryExample {
    
    @Retryable(
        maxAttempts = 3,
        delay = 1000,
        multiplier = 2.0,
        maxDelay = 5000,
        retryFor = {Exception.class}
    )
    public String callWithRetry() {
        return remoteService.call();
    }
}
```

### 7.3 限流

```java
@Component
public class RateLimiterExample {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    public boolean tryAcquire(String key, int rate, int capacity) {
        return buckets.computeIfAbsent(key, k -> 
            Bucket.builder()
                .addLimit(Bandwidth.classic(rate, Refill.intervally(rate, Duration.ofMinutes(1))))
                .build()
        ).tryConsume(1);
    }
}
```

---

## 8. 安全最佳实践

### 8.1 认证与授权

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    public String createToken(String username, List<String> roles) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000);
        
        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### 8.2 输入验证

```java
@Component
public class InputValidationExample {
    
    // 使用注解验证
    @Validated
    public void createUser(@Valid CreateUserRequest request) {}
    
    // 防止SQL注入 - 使用参数化查询
    public User findUserByName(String username) {
        return entityManager.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", username)
            .getSingleResult();
    }
}
```

### 8.3 加密

```java
@Service
public class EncryptionExample {
    
    // AES加密
    public String encryptAES(String plaintext) throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    // 使用BCrypt哈希密码
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    public boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
```

---

## 9. 性能优化

### 9.1 数据库优化

```java
@Repository
public class UserRepository {
    
    // 使用参数绑定
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String username);
    
    // 使用fetch join避免N+1问题
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    User findByIdWithRoles(@Param("id") Long id);
    
    // 批量操作
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") UserStatus status);
}
```

### 9.2 连接池配置

```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(20);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(60000);
        
        return config;
    }
}
```

### 9.3 对象池

```java
@Service
public class ObjectPoolExample {
    
    private final GenericObjectPool<ExpensiveObject> objectPool;
    
    public ObjectPoolExample() {
        GenericObjectPoolConfig<ExpensiveObject> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        config.setMaxWaitMillis(30000);
        
        objectPool = new GenericObjectPool<>(new ExpensiveObjectFactory(), config);
    }
    
    public ExpensiveObject borrowObject() {
        try {
            return objectPool.borrowObject();
        } catch (Exception e) {
            throw new RuntimeException("获取对象失败", e);
        }
    }
    
    public void returnObject(ExpensiveObject obj) {
        objectPool.returnObject(obj);
    }
}
```

### 9.4 异步处理

```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

---

## 10. 日志最佳实践

```java
@Service
public class LoggingExample {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingExample.class);
    
    // 使用参数化日志(不要字符串拼接)
    public void logInfo() {
        log.info("处理请求: userId={}, action={}", userId, action);
    }
    
    // 使用适当的日志级别
    public void logWithLevel() {
        log.debug("调试详细信息");
        log.info("重要业务事件");
        log.warn("潜在问题");
        log.error("发生错误", ex);
    }
    
    // 使用MDC进行请求追踪
    public void processWithMDC(String requestId) {
        MDC.put("requestId", requestId);
        try {
            // 处理请求
        } finally {
            MDC.clear();
        }
    }
}
```

---

## 11. 异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        log.warn("服务异常: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
            .code(ex.getStatus().getCode())
            .message(ex.getStatus().getMsg())
            .build();
        return ResponseEntity.status(ex.getStatus().getCode()).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("意外错误", ex);
        ErrorResponse error = ErrorResponse.builder()
            .code(500)
            .message("服务器内部错误")
            .build();
        return ResponseEntity.status(500).body(error);
    }
}

@Data
@Builder
public class ErrorResponse implements Serializable {
    private int code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
```

---

## 12. 代码审查检查清单

### 12.1 代码质量
- [ ] 代码遵循项目编码规范
- [ ] 无硬编码值(使用常量)
- [ ] 无魔法数字
- [ ] 代码可测试
- [ ] 无重复代码

### 12.2 安全性
- [ ] 所有用户输入都有验证
- [ ] 使用参数化查询(无SQL注入)
- [ ] 日志中无敏感数据
- [ ] 正确的认证和授权

### 12.3 性能
- [ ] 数据库查询已优化
- [ ] 避免N+1查询问题
- [ ] 合理使用缓存
- [ ] 已配置连接池
- [ ] 考虑了线程安全

### 12.4 异常处理
- [ ] 所有异常都被正确处理
- [ ] 无空catch块
- [ ] 正确记录错误日志

---

## 13. Maven构建命令

```bash
# 构建整个项目
mvn clean package -DskipTests

# 构建特定模块
mvn clean package -pl module-name -am

# 运行测试
mvn test

# 代码分析
mvn checkstyle:check
mvn pmd:pmd
mvn spotbugs:spotbugs
```

---

## 14. 关键依赖

- **Spring Boot 3.x**: 核心框架
- **Spring Security**: 认证与授权
- **Mybatis Plus**: 数据库访问
- **HikariCP**: 连接池
- **Caffeine**: 本地缓存
- **Redis**: 分布式缓存
- **Micrometer**: 指标收集
- **Prometheus**: 监控
- **Jaeger/Zipkin**: 分布式追踪
- **Lombok**: 代码生成
- **MapStruct**: Bean映射
