package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.dto.DoctorDto;
import me.youmeng.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
@Slf4j
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private UserService userService;

    @Autowired
    private HospitalService hospitalService;

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
        Page<DoctorDto> doctorDtoPage = new Page<>();

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "doctor_" + (name == null?"":name) + "_" + page + "_" + pageSize;

        doctorDtoPage = (Page) redisTemplate.opsForValue().get(redisKey);
        if(doctorDtoPage != null){
            return R.success(doctorDtoPage);
        }else{
            doctorDtoPage = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<Doctor>();

        //3、模糊查询条件设置
        if(name != null){
            queryWrapper.like(Doctor::getName,name);
        }

        //4、排序条件
        queryWrapper.orderByAsc(Doctor::getCreateTime);

        //5、执行查询
        doctorService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo, doctorDtoPage);

        //6、根据doc的id和h_id查询其用户信息和所属医院信息，封装成DoctorDto


        //7、遍历每一条Dto记录，依次查询user表和hospital表拿到账号或医院名
        List<Doctor> records = pageInfo.getRecords();

        List<DoctorDto> list = records.stream().map((record)->{
            DoctorDto doctorDto = new DoctorDto();
            BeanUtils.copyProperties(record, doctorDto);
            User user = userService.getById(doctorDto.getUid());
            Hospital hospital = hospitalService.getById(doctorDto.getHid());
            if(user != null && hospital != null){
                doctorDto.setUsername(user.getUsername());
                doctorDto.setTelephone(user.getTelephone());
                doctorDto.setHospital(hospital.getName());
                doctorDto.setAge(user.getAge());
                doctorDto.setGender(user.getGender());
            }
            return doctorDto;
        }).collect(Collectors.toList());

        doctorDtoPage.setRecords(list);
        redisTemplate.opsForValue().set(redisKey, doctorDtoPage,60, TimeUnit.MINUTES);
        return R.success(doctorDtoPage);
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody DoctorDto doctorDto){
        log.info("DoctorDto:{}",doctorDto);
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(doctorDto.getUsername() != null, User::getUsername,doctorDto.getUsername());
        User user = userService.getOne(lambdaQueryWrapper);

        doctorDto.setUid(user.getId());
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(doctorDto, doctor);
        doctorService.save(doctor);

        //如果数据有改动，清除所有与key以，doctor_开头的缓存
        Set redisKey = redisTemplate.keys("doctor_*");
        redisTemplate.delete(redisKey);
        return R.success("医生添加成功!");
    }

    /**
     * 编辑信息时的信息回显，现在拿不到前台传递的参数
     * @param did
     * @return
     */
    @GetMapping("/edit-get")
    public R<Doctor> edit(Long did){
        log.info("Doctor:{}",did);

        Doctor doctor = doctorService.getById(did);
        return R.success(doctor);
    }

    @PutMapping("/edit-finish")
    public R<String> editFinish(@RequestBody Doctor doctor){
        log.info("doctor:{}",doctor);
        doctorService.updateById(doctor);

        //如果数据有改动，清除所有与key以，doctor_开头的缓存
        Set redisKey = redisTemplate.keys("doctor_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("医生信息修改成功");
    }

    @Transactional
    @DeleteMapping("/delete")
    public R<String> delete(@RequestBody DoctorDto doctorDto){
        doctorService.removeById(doctorDto.getId());
        userService.removeById(doctorDto.getUid());

        //将该医生所有相关的预约都删除
        LambdaQueryWrapper<Appointment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Appointment::getDid, doctorDto.getId());
        appointmentService.remove(lambdaQueryWrapper);

        //将该医生所有相关的病例都删除
        LambdaQueryWrapper<Cases> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
        lambdaQueryWrapper1.eq(Cases::getDid, doctorDto.getId());
        casesService.remove(lambdaQueryWrapper1);

        //如果数据有改动，清除所有与key以，doctor_开头的缓存
        Set redisKey = redisTemplate.keys("doctor_*");
        redisTemplate.delete(redisKey);
        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("医生删除成功！");
    }
}
