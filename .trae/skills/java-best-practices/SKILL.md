---
name: "java-best-practices"
description: "Comprehensive Java coding best practices including thread usage, interface specs, design patterns, caching, monitoring, high availability, security, and performance."
---

# Java Best Practices

This comprehensive guide covers essential Java coding best practices for building robust, maintainable, and high-performance applications.

---

## 1. Project Overview

- **Language**: Java 17+
- **Framework**: Spring Boot 3.x, Mybatis Plus 3.5.x,Spring Cloud Alibab 2023.x
- **Build Tool**: Maven/Gradle
- **Architecture**: Microservices with plugin system

---

## 2. Thread Usage Guidelines

### 2.1 Thread Pool Best Practices

```java
// Recommended: Use ThreadPoolExecutor with proper configuration
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
    
    // Always shutdown gracefully
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

**Key Points:**
- Define core pool size, max pool size, queue capacity, and keep-alive time
- Use custom ThreadFactory for meaningful thread names
- Implement rejection policy (CallerRunsPolicy, AbortPolicy, etc.)
- Always shutdown thread pool gracefully

### 2.2 CompletableFuture for Async Operations

```java
public class AsyncExample {
    
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    // Async execution with callback
    public CompletableFuture<Result> processAsync(Data data) {
        return CompletableFuture.supplyAsync(() -> processData(data), executor)
            .thenApply(this::transformResult)
            .thenCompose(this::saveResultAsync)
            .exceptionally(ex -> {
                log.error("Error processing data", ex);
                return Result.error(ex.getMessage());
            });
    }
    
    // Parallel execution of multiple tasks
    public CompletableFuture<List<Result>> processMultipleAsync(List<Data> dataList) {
        List<CompletableFuture<Result>> futures = dataList.stream()
            .map(data -> CompletableFuture.supplyAsync(() -> processData(data), executor))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }
}
```

### 2.3 Thread Safety Best Practices

```java
public class ThreadSafeExample {
    
    // Use ConcurrentHashMap for thread-safe map operations
    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();
    
    // ComputeIfAbsent for thread-safe lazy initialization
    public Object getOrCreate(String key, Supplier<Object> factory) {
        return cache.computeIfAbsent(key, k -> factory.get());
    }
    
    // Use Atomic classes for counters
    private final AtomicLong counter = new AtomicLong(0);
    private final AtomicReference<Data> currentData = new AtomicReference<>();
    
    // Use ReadWriteLock for read-heavy workloads
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
    
    // Use StampedLock for optimistic read
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

### 2.4 ThreadLocal Best Practices

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
    
    // IMPORTANT: Always clean up ThreadLocal to prevent memory leaks
    public class RequestHandler {
        
        public void handleRequest(HttpRequest request) {
            try {
                setCurrentUser(extractUser(request));
                processRequest(request);
            } finally {
                clear(); // Critical: prevent memory leaks
            }
        }
    }
}
```

---

## 3. Interface Specification Guidelines

### 3.1 API Interface Design

```java
@RestController
@RequestMapping("/api/v1/resources")
@Api(tags = "Resource Management", description = "APIs for resource management")
public class ResourceController {
    
    @Autowired
    private ResourceService resourceService;
    
    @GetMapping
    @ApiOperation(value = "Query resources", notes = "Query resources with pagination")
    public ResponseEntity<PageResult<ResourceDTO>> queryResources(
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNo,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) String keyword) {
        
        PageRequest pageRequest = PageRequest.of(pageNo - 1, pageSize);
        PageResult<ResourceDTO> result = resourceService.queryResources(keyword, pageRequest);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping
    @ApiOperation(value = "Create resource")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResourceDTO> createResource(
            @Valid @RequestBody CreateResourceRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        ResourceDTO created = resourceService.createResource(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PutMapping("/{id}")
    @ApiOperation(value = "Update resource")
    public ResponseEntity<ResourceDTO> updateResource(
            @PathVariable @Positive Long id,
            @Valid @RequestBody UpdateResourceRequest request,
            @RequestAttribute("currentUser") User currentUser) {
        
        ResourceDTO updated = resourceService.updateResource(id, request, currentUser);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete resource")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteResource(
            @PathVariable @Positive Long id,
            @RequestAttribute("currentUser") User currentUser) {
        
        resourceService.deleteResource(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
```

### 3.2 Service Interface Design

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
        
        log.info("Resource created successfully, id: {}", resource.getId());
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

### 3.3 DTO Patterns

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateResourceRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @NotBlank(message = "Resource name cannot be blank")
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

## 4. Design Patterns

### 4.1 Singleton Pattern

```java
// Thread-safe singleton using Bill Pugh Singleton
public class SingletonExample {
    
    private SingletonExample() {}
    
    private static class SingletonHolder {
        private static final SingletonExample INSTANCE = new SingletonExample();
    }
    
    public static SingletonExample getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    // Enum singleton
    public enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {}
    }
}
```

### 4.2 Factory Pattern

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

### 4.3 Strategy Pattern

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
            .orElseThrow(() -> new IllegalArgumentException("Unsupported payment type"));
        
        return strategy.pay(request);
    }
}
```

### 4.4 Observer Pattern (Event-Driven)

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
        log.info("Resource created: id={}, name={}", event.getResourceId(), event.getResourceName());
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

### 4.5 Template Method Pattern

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

### 4.6 Builder Pattern

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

// Usage
User user = User.builder()
    .id(1L)
    .username("john")
    .email("john@example.com")
    .status(UserStatus.ACTIVE)
    .build();
```

### 4.7 Decorator Pattern

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

## 5. Caching Best Practices

### 5.1 Spring Cache Annotations

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

### 5.2 Multi-Level Cache

```java
@Service
public class MultiLevelCacheService {
    
    // L1: Local cache (Caffeine)
    private final Cache<String, Object> localCache = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .recordStats()
        .build();
    
    // L2: Distributed cache (Redis)
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

## 6. Monitoring Best Practices

### 6.1 Metrics Collection with Micrometer

```java
@Component
public class MetricsExample {
    
    private final MeterRegistry meterRegistry;
    
    @Autowired
    public MetricsExample(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    // Counter - for counting events
    private Counter requestCounter = Counter.builder("http.requests")
        .tag("endpoint", "/api/users")
        .description("Total HTTP requests")
        .register(meterRegistry);
    
    // Timer - for measuring execution time
    private Timer requestTimer = Timer.builder("http.request.duration")
        .tag("endpoint", "/api/users")
        .register(meterRegistry);
    
    // Histogram - for percentile distribution
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

### 6.2 Health Check

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
                .withDetail("database", "unavailable")
                .withDetail("error", e.getMessage())
                .build();
        }
        
        return Health.up()
            .withDetail("database", "available")
            .build();
    }
}
```

### 6.3 Distributed Tracing

```java
@Service
public class TracingExample {
    
    @Autowired
    private Tracer tracer;
    
    public void processWithTrace(String operationName) {
        Span span = tracer.spanBuilder(operationName).startSpan();
        
        try (Scope scope = span.makeCurrent()) {
            processBusiness();
            span.addEvent("business.processed");
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

## 7. High Availability

### 7.1 Circuit Breaker Pattern

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
            .recover(throwable -> "Fallback response")
            .get();
    }
    
    @CircuitBreaker(name = "remoteService", fallbackMethod = "fallback")
    public String callWithAnnotation() {
        return remoteService.call();
    }
    
    private String fallback(Exception e) {
        return "Fallback response";
    }
}
```

### 7.2 Retry Pattern

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

### 7.3 Rate Limiting

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

## 8. Security Best Practices

### 8.1 Authentication and Authorization

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

### 8.2 Input Validation

```java
@Component
public class InputValidationExample {
    
    // Use annotation-based validation
    @Validated
    public void createUser(@Valid CreateUserRequest request) {}
    
    // SQL injection prevention - use parameterized queries
    public User findUserByName(String username) {
        return entityManager.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", User.class)
            .setParameter("username", username)
            .getSingleResult();
    }
}
```

### 8.3 Encryption

```java
@Service
public class EncryptionExample {
    
    // AES Encryption
    public String encryptAES(String plaintext) throws GeneralSecurityException {
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    // Hash passwords with BCrypt
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    public boolean verifyPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
```

---

## 9. Performance Optimization

### 9.1 Database Optimization

```java
@Repository
public class UserRepository {
    
    // Use JPQL with parameter binding
    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String username);
    
    // Use fetch join to avoid N+1 problem
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    User findByIdWithRoles(@Param("id") Long id);
    
    // Batch operations
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :ids")
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") UserStatus status);
}
```

### 9.2 Connection Pool Configuration

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

### 9.3 Object Pooling

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
            throw new RuntimeException("Failed to borrow object", e);
        }
    }
    
    public void returnObject(ExpensiveObject obj) {
        objectPool.returnObject(obj);
    }
}
```

### 9.4 Asynchronous Processing

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

## 10. Logging Best Practices

```java
@Service
public class LoggingExample {
    
    private static final Logger log = LoggerFactory.getLogger(LoggingExample.class);
    
    // Use parameterized logging (no string concatenation)
    public void logInfo() {
        log.info("Processing request: userId={}, action={}", userId, action);
    }
    
    // Use appropriate log level
    public void logWithLevel() {
        log.debug("Detailed info for debugging");
        log.info("Important business event");
        log.warn("Potential issue");
        log.error("Error occurred", ex);
    }
    
    // Use MDC for request tracking
    public void processWithMDC(String requestId) {
        MDC.put("requestId", requestId);
        try {
            // Process request
        } finally {
            MDC.clear();
        }
    }
}
```

---

## 11. Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        log.warn("Service exception: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
            .code(ex.getStatus().getCode())
            .message(ex.getStatus().getMsg())
            .build();
        return ResponseEntity.status(ex.getStatus().getCode()).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        ErrorResponse error = ErrorResponse.builder()
            .code(500)
            .message("Internal server error")
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

## 12. Code Review Checklist

### 12.1 Code Quality
- [ ] Code follows project coding conventions
- [ ] No hardcoded values (use constants)
- [ ] No magic numbers
- [ ] Code is testable
- [ ] No code duplication

### 12.2 Security
- [ ] Input validation on all user inputs
- [ ] Parameterized queries (no SQL injection)
- [ ] No sensitive data in logs
- [ ] Proper authentication and authorization

### 12.3 Performance
- [ ] Database queries are optimized
- [ ] N+1 query problem avoided
- [ ] Appropriate use of caching
- [ ] Connection pool configured
- [ ] Thread safety considered

### 12.4 Error Handling
- [ ] All exceptions are handled properly
- [ ] No empty catch blocks
- [ ] Proper logging of errors

---

## 13. Maven Build Commands

```bash
# Build entire project
mvn clean package -DskipTests

# Build specific module
mvn clean package -pl module-name -am

# Run tests
mvn test

# Code analysis
mvn checkstyle:check
mvn pmd:pmd
mvn spotbugs:spotbugs
```

---

## 14. Key Dependencies

- **Spring Boot 3.x**: Core framework
- **Spring Security**: Authentication and authorization
- **Mybatis Plus**: Database access
- **HikariCP**: Connection pooling
- **Caffeine**: Local caching
- **Redis**: Distributed caching
- **Micrometer**: Metrics collection
- **Prometheus**: Monitoring
- **Jaeger/Zipkin**: Distributed tracing
- **Lombok**: Code generation
- **MapStruct**: Bean mapping
