package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hospital")
@Slf4j
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private CasesService casesService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){


        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "hospital_" + (name == null?"":name) + "_" + page + "_" + pageSize;
        pageInfo = (Page) redisTemplate.opsForValue().get(redisKey);
        if(pageInfo != null){
            return R.success(pageInfo);
        }else{
            pageInfo = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Hospital> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3、模糊查询条件设置
        if(name != null){
            lambdaQueryWrapper.like(Hospital::getName, name);
        }

        //5、执行查询
        hospitalService.page(pageInfo,lambdaQueryWrapper);
        redisTemplate.opsForValue().set(redisKey, pageInfo,60, TimeUnit.MINUTES);

        return R.success(pageInfo);
    }

    @GetMapping("/list")
    public R<List<Hospital>> list(){
        List<Hospital> hospitals = new ArrayList<>();
        hospitals = hospitalService.list();
        return R.success(hospitals);
    }
    @GetMapping("/edit-get")
    public R<Hospital> edit(Long id){
        log.info("hospital:{}",id);

        Hospital hospital = hospitalService.getById(id);
        return R.success(hospital);
    }

    @PutMapping("/edit-finish")
    public R<String> editFinish(@RequestBody Hospital hospital){
        hospitalService.updateById(hospital);

        //如果数据有改动，清除所有与key以，hospital_开头的缓存
        Set redisKey = redisTemplate.keys("hospital_*");
        redisTemplate.delete(redisKey);


        return R.success("医院信息修改成功");
    }


    @PostMapping("/add")
    public R<String> add(@RequestBody Hospital hospital){
        LambdaQueryWrapper<Hospital> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Hospital::getName, hospital.getName());
        Hospital h = hospitalService.getOne(lambdaQueryWrapper);
        if(h != null){
            return R.error("该医院已经存在!");
        }
        hospitalService.save(hospital);

        clearRedisCache();
        return R.success("添加成功！");
    }

    @Transactional
    @DeleteMapping("/delete")
    public R<String> delete(Long id){

        //将该医院所有相关的药品、医生、病例都删除
        LambdaQueryWrapper<Drug> drugLambdaQueryWrapper = new LambdaQueryWrapper<>();
        drugLambdaQueryWrapper.eq(Drug::getHid, id);
        drugService.remove(drugLambdaQueryWrapper);

        //删除医生
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        doctorLambdaQueryWrapper.eq(Doctor::getHid, id);
        List<Doctor> doctors = doctorService.list(doctorLambdaQueryWrapper);
        List<Long> ids = doctors.stream().map(doctor -> {
            Long did = doctor.getId();
            return did;
        }).collect(Collectors.toList());
        doctorService.remove(doctorLambdaQueryWrapper);


        if(ids.size() != 0){
            //与医生有关的预约都删除
            LambdaQueryWrapper<Appointment> appointmentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            appointmentLambdaQueryWrapper.in(Appointment::getDid, ids);
            appointmentService.remove(appointmentLambdaQueryWrapper);

            //与医生有关的病例都删除
            LambdaQueryWrapper<Cases> casesLambdaQueryWrapper = new LambdaQueryWrapper<>();
            casesLambdaQueryWrapper.in(Cases::getDid, ids);
            casesService.remove(casesLambdaQueryWrapper);

            //医生的账户也删除
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<Long> uids = doctors.stream().map(doctor -> {
                Long uid = doctor.getUid();
                return uid;
            }).collect(Collectors.toList());
            userLambdaQueryWrapper.in(User::getId, uids);
            userService.remove(userLambdaQueryWrapper);
        }

        hospitalService.removeById(id);

        clearRedisCache();
        return R.success("医院删除成功！");
    }
    public void clearRedisCache(){
        //如果数据有改动，清除所有与key以，hospital_开头的缓存
        Set redisKey = redisTemplate.keys("hospital_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("drug_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("doctor_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
    }
}
