package me.youmeng.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户信息
 */
@Data
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    //
    private Long id;

    //名称
    private String name;


    //功能
    private String function;


    //价格
    private double price;


    //出厂公司
    private String company;

    //图片
    private String image;

}
