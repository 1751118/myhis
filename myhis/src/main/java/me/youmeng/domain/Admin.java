package me.youmeng.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理员
 */
@Data
public class Admin implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;


    //用户名
    private String username;

    //密码
    private String password;
}
