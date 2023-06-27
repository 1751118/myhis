package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    //手术id
    private Long id;

    //手术名称
    private String name;

    //手术日期
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //手术结果
    private String result;


}
