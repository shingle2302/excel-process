package com.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_account")
public class UserAccount {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String passwordHash;

    private String displayName;

    private String roleCode;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
