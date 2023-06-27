package me.youmeng.dto;

import lombok.Data;
import me.youmeng.domain.Doctor;
import me.youmeng.domain.Patient;

@Data
public class PatientDto extends Patient {

    //账号
    private String username;

    //密码
    private String password;

    //手机号
    private String telephone;

    //性别
    private String gender;

    //年龄
    private Integer age;

}
