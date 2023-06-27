package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.dto.PatientDto;
import me.youmeng.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patient")
@Slf4j
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private CasesService casesService;

    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        log.info("page = {},pageSize={},name={}",page,pageSize,name);

        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);
        Page<PatientDto> patientDtoPage = new Page<>();

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "patient_" + (name == null?"":name) + "_" + page + "_" + pageSize;
        patientDtoPage = (Page) redisTemplate.opsForValue().get(redisKey);
        if(patientDtoPage != null){
            return R.success(patientDtoPage);
        }else{
            patientDtoPage = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Patient> queryWrapper = new LambdaQueryWrapper<Patient>();

        //3、模糊查询条件设置
        if(name != null){
            queryWrapper.like(Patient::getName,name);
        }

        //5、执行查询
        patientService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo, patientDtoPage);

        //6、根据doc的id和h_id查询其用户信息和所属医院信息，封装成PatientDto
        List<Patient> records = pageInfo.getRecords();

        List<PatientDto> list = records.stream().map((record)->{
            PatientDto patientDto = new PatientDto();
            BeanUtils.copyProperties(record, patientDto);
            User user = userService.getById(patientDto.getUid());

            if(user != null){
                patientDto.setUsername(user.getUsername());
                patientDto.setTelephone(user.getTelephone());
                patientDto.setAge(user.getAge());
                patientDto.setGender(user.getGender());
            }
            return patientDto;
        }).collect(Collectors.toList());

        patientDtoPage.setRecords(list);
        redisTemplate.opsForValue().set(redisKey, patientDtoPage,60, TimeUnit.MINUTES);
        return R.success(patientDtoPage);
    }

    @Transactional
    @PostMapping("/add")
    public R<String> add(@RequestBody PatientDto patientDto){
        log.info("PatientDto:{}",patientDto);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(patientDto.getUsername() != null, User::getUsername,patientDto.getUsername());
        User user = userService.getOne(lambdaQueryWrapper);
        if(user != null){
            return R.error("该用户名已经存在！");
        }
        user = new User();
        BeanUtils.copyProperties(patientDto,user);
        userService.save(user);

        Patient patient = new Patient();
        BeanUtils.copyProperties(patientDto, patient);
        patient.setUid(user.getId());
        patientService.save(patient);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("patient_*");
        redisTemplate.delete(redisKey);
        return R.success("病人添加成功!");
    }

    /**
     * 编辑信息时的信息回显，现在拿不到前台传递的参数
     * @param id
     * @return
     */
    @GetMapping("/edit-get")
    public R<PatientDto> edit(Long id){
        log.info("Patient:{}",id);

        Patient patient = patientService.getById(id);
        User user = userService.getById(patient.getUid());
        PatientDto patientDto = new PatientDto();
        BeanUtils.copyProperties(user,patientDto);
        BeanUtils.copyProperties(patient,patientDto);
        return R.success(patientDto);
    }

    @Transactional
    @PutMapping("/edit-finish")
    public R<String> editFinish(@RequestBody PatientDto patientdto){
        log.info("patient:{}",patientdto);
        Patient patient = new Patient();
        BeanUtils.copyProperties(patientdto,patient);
        patientService.updateById(patient);

        User user = new User();
        BeanUtils.copyProperties(patientdto,user);
        user.setId(patientdto.getUid());
        try {
            userService.updateById(user);
        }
        catch (Exception e){
            return R.error("该用户名已经存在！");
        }


        //如果数据有改动，清除所有相关的缓存
        Set redisKey = redisTemplate.keys("patient_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("病人信息修改成功");
    }

    @Transactional
    @DeleteMapping("/delete")
    public R<String> delete(@RequestBody PatientDto patientdto){
        patientService.removeById(patientdto.getId());
        userService.removeById(patientdto.getUid());

        //将该病人所有相关的预约都删除
        LambdaQueryWrapper<Appointment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Appointment::getPid, patientdto.getId());
        appointmentService.remove(lambdaQueryWrapper);

        //将该病人所有相关的预约都删除
        LambdaQueryWrapper<Cases> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Cases::getPid, patientdto.getId());
        casesService.remove(lambdaQueryWrapper1);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("patient_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("病人删除成功！");
    }
}
