package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 医生信息
 */
@Data
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    //医生id
    private Long id;

    //姓名
    private String name;


    //所属医院
    private Long hid;


    //部门
    private String department;


    //职称等级
    private String level;


    //注册时间，
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    //其用户id
    private Long uid;
}
