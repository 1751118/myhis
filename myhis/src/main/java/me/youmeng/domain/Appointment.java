package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    //病例id
    private Long id;

    //医生id
    private Long did;

    //患者id
    private Long pid;

    //创建预约的时间，
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //预约的时间，
    private LocalDateTime appointTime;

    //结束时间，
    private LocalDateTime endTime;

    //预约状态
    private int status;

}
