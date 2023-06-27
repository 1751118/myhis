package me.youmeng.domain;

import lombok.Data;

import java.io.Serializable;


@Data
public class Need implements Serializable {

    private static final long serialVersionUID = 1L;

    //手术
    private Long opid;

    //仪器名称
    private Long devid;

}
