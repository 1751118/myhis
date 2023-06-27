package me.youmeng.domain;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import me.youmeng.dto.DoctorDto;

/**
 * 用户信息
 */
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    //用户id
    private Long id;

    //用户名
    private String username;


    //密码
    private String password;


    //年龄
    private Integer age;


    //性别
    private String gender;


    //电话
    private String telephone;

//    public void getAttribute(DoctorDto doctorDto){
//        this.username = doctorDto.getUsername();
//        this.password = "123456";
//        this.age = doctorDto.ge
//    }
}
