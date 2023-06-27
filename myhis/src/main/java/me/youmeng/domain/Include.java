package me.youmeng.domain;

import lombok.Data;

import java.io.Serializable;


@Data
public class Include implements Serializable {

    private static final long serialVersionUID = 1L;

    //药品
    private Long drugid;

    //处方
    private Long preid;

}
