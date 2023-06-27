package me.youmeng.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息
 */
@Data
public class Hospital implements Serializable {

    private static final long serialVersionUID = 1L;

    //医院
    private Long id;

    //名称
    private String name;


    //地址
    private String address;


    //电话
    private String telephone;


    //等级
    private String level;

}
