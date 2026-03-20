package com.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.excel.entity.UserAccount;
import com.excel.mapper.UserAccountMapper;
import com.excel.service.UserAuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAccountMapper userAccountMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserAuthServiceImpl(UserAccountMapper userAccountMapper) {
        this.userAccountMapper = userAccountMapper;
    }

    @Override
    public boolean validateUser(String username, String password) {
        Optional<UserAccount> userOptional = getByUsername(username);
        if (userOptional.isEmpty()) {
            return false;
        }
        UserAccount user = userOptional.get();
        if (!"启用".equals(user.getStatus())) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    @Override
    public boolean isActiveUser(String username) {
        return getByUsername(username)
                .filter(user -> "启用".equals(user.getStatus()))
                .isPresent();
    }

    @Override
    public Optional<UserAccount> getByUsername(String username) {
        LambdaQueryWrapper<UserAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAccount::getUsername, username);
        return Optional.ofNullable(userAccountMapper.selectOne(wrapper));
    }

    @Override
    public UserAccount createBootstrapAdmin(String username, String rawPassword, String displayName) {
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setDisplayName(StringUtils.hasText(displayName) ? displayName : username);
        user.setRoleCode("ADMIN");
        user.setStatus("启用");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userAccountMapper.insert(user);
        return user;
    }
}
