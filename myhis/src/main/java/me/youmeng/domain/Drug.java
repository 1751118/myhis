package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Drug implements Serializable {

    private static final long serialVersionUID = 1L;

    //药品id
    private Long id;

    //名称
    private String name;

    //价格
    private Double price;

    //生产日期
//    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate produceTime;

    //保质期
    private String expiration;

    //售卖状态
    private Boolean status;

    //所属医院
    private Long hid;

    //数量
    private Integer count;

    //更新时间，
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
