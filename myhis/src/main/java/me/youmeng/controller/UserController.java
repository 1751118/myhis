package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.User;
import me.youmeng.service.UserService;
import me.youmeng.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request){

        log.info("手机用户正在登录！map:{}",map.toString());
        //获取用户输入的手机号和验证码
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

//        //获取session中保存的验证码（系统生成）
//        Object codeInSession = request.getSession().getAttribute(phone);

        //获取redis中保存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);
        if(codeInSession != null && codeInSession.equals(code)){
            //登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>();
            queryWrapper.eq(User::getTelephone,phone);
            User user = userService.getOne(queryWrapper);
            if(user == null){
                //是新用户，为它注册
                user = new User();
                user.setTelephone(phone);
                userService.save(user);
            }
            //登录成功放ID
            request.getSession().setAttribute("user",user.getId());

            //登录成功删除redis中的验证码
            redisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("验证码错误");
    }
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user/*, HttpServletRequest request*/){
        String phone = user.getTelephone();
        if(phone!=null){
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}",code);

//            //将验证码保存到session中
//            request.getSession().setAttribute(phone,code);

            //将验证码保存到redis中缓存，1分钟有效
            redisTemplate.opsForValue().set(phone,code,1, TimeUnit.MINUTES);
            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("user");
        return R.success("退出成功！");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody User user){
        log.info("user:{}",user);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(user.getUsername() != null, User::getUsername, user.getUsername());
        User u = userService.getOne(lambdaQueryWrapper);
        if(u != null){
            return R.error("该用户已经存在");
        }

        //新增用户
        userService.save(user);
        return R.success("用户添加成功了！");
    }
    @GetMapping("/edit-get")
    public R<User> edit(Long uid){
        log.info("user:{}",uid);
        User user = userService.getById(uid);
        return R.success(user);
    }

    @PutMapping("/edit-finish")
    public R<String> editFinish(@RequestBody User user){
        log.info("User:{}",user);
        boolean b = userService.updateById(user);

        if(!b){
            return R.error("修改失败，" + user.getUsername() + "已经存在");
        }
        return R.success("用户信息修改成功");
    }

    @DeleteMapping("/delete")
    public R<String> delete(Long id){
        userService.removeById(id);
        return R.success("用户删除成功！");
    }
}
