package me.youmeng.dto;

import lombok.Data;
import me.youmeng.domain.Doctor;

@Data
public class DoctorDto extends Doctor {

    //账号
    private String username;

    //医院名
    private String hospital;

    //手机号
    private String telephone;

    //性别
    private String gender;

    //年龄
    private Integer age;
}
