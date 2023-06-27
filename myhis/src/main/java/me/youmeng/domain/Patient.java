package me.youmeng.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息
 */
@Data
public class Patient implements Serializable{

    private static final long serialVersionUID = 1L;

    //病人id
    private Long id;

    //姓名
    private String name;

    //其用户id
    private Long uid;

    //住址
    private String address;

    //注册时间，
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
