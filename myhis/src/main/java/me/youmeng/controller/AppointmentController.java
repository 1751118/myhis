package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.dto.AppointmentDto;
import me.youmeng.dto.CasesDto;
import me.youmeng.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointment")
@Slf4j
public class AppointmentController {

    @Autowired
    private CasesService casesService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentService appointmentService;

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
    public R<Page> page(Integer page, Integer pageSize, String name, Long beginTime, Long endTime){

        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);
        Page appoinmentDtoInfo = new Page(page,pageSize);

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "appointment_" + (name == null?"":name) + "_"
                                         + (beginTime == null?"":beginTime) + "_"
                                         + (endTime == null?"":endTime) + "_"
                                            + page + "_" + pageSize;
        appoinmentDtoInfo = (Page) redisTemplate.opsForValue().get(redisKey);
        if(appoinmentDtoInfo != null){
            return R.success(appoinmentDtoInfo);
        }else{
            appoinmentDtoInfo = new Page(page,pageSize);
        }



        //2、构造条件构造器
        LambdaQueryWrapper<Appointment> appointmentLambdaQueryWrapper = new LambdaQueryWrapper<Appointment>();
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<Doctor>();
        LambdaQueryWrapper<Patient> patientLambdaQueryWrapper = new LambdaQueryWrapper<Patient>();

        //按照创建时间降序、医生、病人姓名分类显示
        appointmentLambdaQueryWrapper.orderByDesc(Appointment::getCreateTime);
        appointmentLambdaQueryWrapper.orderByDesc(Appointment::getEndTime);
        appointmentLambdaQueryWrapper.orderByAsc(Appointment::getDid);
        appointmentLambdaQueryWrapper.orderByAsc(Appointment::getPid);

        //筛选符合某一时间段的预约
        if(beginTime !=null && endTime != null){
            Date time1 = new Date(beginTime);
            Date time2 = new Date(endTime);
            appointmentLambdaQueryWrapper.between(Appointment::getCreateTime, time1,time2);
        }

        //3、模糊查询条件设置（按照医生、患者姓名模糊查询）
        if(name != null){
            doctorLambdaQueryWrapper.like(Doctor::getName, name);
            patientLambdaQueryWrapper.like(Patient::getName, name);
            List<Doctor> doctors = doctorService.list(doctorLambdaQueryWrapper);
            List<Patient> patients = patientService.list(patientLambdaQueryWrapper);
            List<Long> ids = new ArrayList<>();
            for(Doctor doctor : doctors){
                ids.add(doctor.getId());
            }
            for(Patient patient : patients){
                ids.add(patient.getId());
            }

            //如果找到相关记录就显示，否则不显示任何数据
            if(ids.size() > 0)
                appointmentLambdaQueryWrapper.in(Appointment::getDid, ids).or().in(Appointment::getPid,ids);
            else{
                appointmentLambdaQueryWrapper.eq(Appointment::getId, -1);
            }
        }

        //4、执行查询
        appointmentService.page(pageInfo,appointmentLambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, appoinmentDtoInfo);


        //5、封装CasesDto
        List<Appointment> records = pageInfo.getRecords();
        List<AppointmentDto> lists = records.stream().map(record ->{
            AppointmentDto appointmentDto = new AppointmentDto();
            Doctor doctor = doctorService.getById(record.getDid());
            Hospital hospital = hospitalService.getById(doctor.getHid());
            Patient patient = patientService.getById(record.getPid());
            BeanUtils.copyProperties(record, appointmentDto);
            if(doctor != null)
                appointmentDto.setDoctorName(doctor.getName());
            if(patient != null)
                appointmentDto.setPatientName(patient.getName());
            if(hospital != null)
                appointmentDto.setHospitalName(hospital.getName());
            return appointmentDto;
        }).collect(Collectors.toList());

        //6、设置caseDtoInfo的记录
        appoinmentDtoInfo.setRecords(lists);

        //设置key为redisKey的数据appointmentDtoInfo，在redis中缓存60分钟，
        // 从此刻起的60分钟，用户再查询预约表，将不会查询数据库，直接从redis中取数据（除非redis中的缓存数据被删除）
        //这样可以缓解数据库的压力
        redisTemplate.opsForValue().set(redisKey, appoinmentDtoInfo,60, TimeUnit.MINUTES);

        return R.success(appoinmentDtoInfo);
    }

    @PutMapping("/edit")
    public R<String> editFinish(@RequestBody AppointmentDto appointmentDto){
        //检查医生信息
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        doctorLambdaQueryWrapper.eq(Doctor::getName, appointmentDto.getDoctorName());
        Doctor doctor = doctorService.getOne(doctorLambdaQueryWrapper);
        if(doctor == null){
            return R.error("该医生不存在，请检查医生的姓名");
        }

        //检查患者信息
        LambdaQueryWrapper<Patient> patientLambdaQueryWrapper = new LambdaQueryWrapper<>();
        patientLambdaQueryWrapper.eq(Patient::getName, appointmentDto.getPatientName());
        Patient patient = patientService.getOne((patientLambdaQueryWrapper));
        if(patient == null){
            return R.error("该患者不存在，请检查患者的姓名");
        }

        appointmentDto.setDid(doctor.getId());
        appointmentDto.setPid(patient.getId());
        appointmentService.updateById(appointmentDto);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        return R.success("预约信息修改成功");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody AppointmentDto appointmentDto){
        //这里简单判断医生、病人是否有这两个人，否则添加不成功
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Patient> patientLambdaQueryWrapper = new LambdaQueryWrapper<>();
        doctorLambdaQueryWrapper.eq(Doctor::getName, appointmentDto.getDoctorName());
        patientLambdaQueryWrapper.eq(Patient::getName, appointmentDto.getPatientName());

        Doctor doctor = doctorService.getOne(doctorLambdaQueryWrapper);
        Patient patient = patientService.getOne(patientLambdaQueryWrapper);

        if(doctor == null){
            return R.error("不存在该医生！");
        }
        if(patient == null){
            return R.error("不存在该患者！");
        }

        appointmentDto.setDid(doctor.getId());
        appointmentDto.setPid(patient.getId());
        Hospital hospital = hospitalService.getById(doctor.getHid());
        appointmentDto.setHospitalName(hospital.getName());

        Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentDto,appointment);
        appointmentService.save(appointment);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        return R.success("预约成功！");
    }

    @DeleteMapping("/delete")
    public R<String> delete(Long[] ids){
        appointmentService.removeByIds(Arrays.asList(ids));
        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("appointment_*");
        redisTemplate.delete(redisKey);
        return R.success("预约删除成功！");
    }

    @GetMapping("/get")
    public R<AppointmentDto> get(Long id){

        //编辑预约信息时的回显
        Appointment appointment = appointmentService.getById(id);
        AppointmentDto appointmentDto = new AppointmentDto();
        Doctor doctor = doctorService.getById(appointment.getDid());
        Patient patient = patientService.getById(appointment.getPid());
        Hospital hospital = hospitalService.getById(doctor.getHid());
        BeanUtils.copyProperties(appointment, appointmentDto);
        appointmentDto.setDoctorName(doctor.getName());
        appointmentDto.setPatientName(patient.getName());
        appointmentDto.setHospitalName(hospital.getName());
        return R.success(appointmentDto);
    }

}
