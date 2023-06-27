package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Prescription implements Serializable {

    private static final long serialVersionUID = 1L;

    //药品id
    private Long id;

    //价格
    private Double price;

}
