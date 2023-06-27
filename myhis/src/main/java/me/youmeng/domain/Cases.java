package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Cases implements Serializable {

    private static final long serialVersionUID = 1L;

    //病例id
    private Long id;

    //医生id
    private Long did;

    //患者id
    private Long pid;

    //诊疗时间，
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //结束时间，
    private LocalDateTime endTime;

    //临床诊断
    private String diagnosis;

    //治疗方式
    private String remedy;

    //治疗结果
    private String result;

}
