package me.youmeng.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.Admin;
import me.youmeng.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    private R<Admin> login(HttpServletRequest httpServletRequest, @RequestBody Admin admin){

        String username = admin.getUsername();
        LambdaQueryWrapper<Admin> lambdaQueryWrapper = new LambdaQueryWrapper<Admin>();
        lambdaQueryWrapper.eq(Admin::getUsername,username);
        Admin adm = adminService.getOne(lambdaQueryWrapper);

        //2、如果不存在用户，返回用户不存在
        if(adm == null){
            return  R.error("登录失败，用户不存在！");
        }
        //3、对密码进行md5加密处理后匹配数据库的密码，如果密码不正确，返回密码错误
        String password = admin.getPassword();
       // password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if(!adm.getPassword().equals(password)){
            return R.error("登录失败，密码错误！");
        }

        //4、存入session，返回登录成功！
        httpServletRequest.getSession().setAttribute("admin",adm.getId());
        return R.success(adm);

    }
}
