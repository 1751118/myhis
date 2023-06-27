package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.dto.CasesDto;
import me.youmeng.dto.DrugDto;
import me.youmeng.service.*;
import org.apache.ibatis.annotations.Case;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cases")
@Slf4j
public class CasesController {

    @Autowired
    private CasesService casesService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

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
        log.info("beginTime:{}",beginTime);

        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);
        Page casesDtoInfo = new Page(page,pageSize);

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "cases_" + (name == null?"":name) + "_"
                + (beginTime == null?"":beginTime) + "_"
                + (endTime == null?"":endTime) + "_"
                + page + "_" + pageSize;
        casesDtoInfo = (Page) redisTemplate.opsForValue().get(redisKey);
        if(casesDtoInfo != null){
            return R.success(casesDtoInfo);
        }else{
            casesDtoInfo = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Cases> caseLambdaQueryWrapper = new LambdaQueryWrapper<Cases>();
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<Doctor>();
        LambdaQueryWrapper<Patient> patientLambdaQueryWrapper = new LambdaQueryWrapper<Patient>();

        caseLambdaQueryWrapper.orderByDesc(Cases::getCreateTime);
        caseLambdaQueryWrapper.orderByDesc(Cases::getEndTime);
        caseLambdaQueryWrapper.orderByDesc(Cases::getDid);
        caseLambdaQueryWrapper.orderByDesc(Cases::getPid);

        if(beginTime !=null && endTime != null){
            Date time1 = new Date(beginTime);
            Date time2 = new Date(endTime);
            caseLambdaQueryWrapper.between(Cases::getCreateTime, time1,time2);
        }

        //3、模糊查询条件设置
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
            if(ids.size() > 0)
                caseLambdaQueryWrapper.in(Cases::getDid, ids).or().in(Cases::getPid,ids);
            else{
                caseLambdaQueryWrapper.eq(Cases::getId, -1);
            }
        }

        //4、执行查询
        casesService.page(pageInfo,caseLambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, casesDtoInfo);

        //5、封装CasesDto
        List<Cases> records = pageInfo.getRecords();
        List<CasesDto> lists = records.stream().map(record ->{
            CasesDto casesDto = new CasesDto();
            Doctor doctor = doctorService.getById(record.getDid());
            Hospital hospital = hospitalService.getById(doctor.getHid());
            Patient patient = patientService.getById(record.getPid());
            BeanUtils.copyProperties(record, casesDto);
            if(doctor != null)
                casesDto.setDoctorName(doctor.getName());
            if(patient != null)
                casesDto.setPatientName(patient.getName());
            if(hospital != null)
                casesDto.setHospitalName(hospital.getName());
            return casesDto;
        }).collect(Collectors.toList());

        //6、设置caseDtoInfo的记录
        casesDtoInfo.setRecords(lists);
        redisTemplate.opsForValue().set(redisKey, casesDtoInfo,60, TimeUnit.MINUTES);

        return R.success(casesDtoInfo);
    }

    @PutMapping("/edit")
    public R<String> editFinish(@RequestBody CasesDto casesDto){
        //检查医生信息
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        doctorLambdaQueryWrapper.eq(Doctor::getName, casesDto.getDoctorName());
        Doctor doctor = doctorService.getOne(doctorLambdaQueryWrapper);
        if(doctor == null){
            return R.error("该医生不存在，请检查医生的姓名");
        }

        //检查患者信息
        LambdaQueryWrapper<Patient> patientLambdaQueryWrapper = new LambdaQueryWrapper<>();
        patientLambdaQueryWrapper.eq(Patient::getName, casesDto.getPatientName());
        Patient patient = patientService.getOne((patientLambdaQueryWrapper));
        if(patient == null){
            return R.error("该患者不存在，请检查患者的姓名");
        }

        casesDto.setDid(doctor.getId());
        casesDto.setPid(patient.getId());
        casesService.updateById(casesDto);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("病例信息修改成功");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody CasesDto casesDto){
        //这里简单判断医生、病人是否有这两个人，否则添加不成功
        LambdaQueryWrapper<Doctor> doctorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Patient> patientLambdaQueryWrapper = new LambdaQueryWrapper<>();
        doctorLambdaQueryWrapper.eq(Doctor::getName, casesDto.getDoctorName());
        patientLambdaQueryWrapper.eq(Patient::getName, casesDto.getPatientName());

        Doctor doctor = doctorService.getOne(doctorLambdaQueryWrapper);
        Patient patient = patientService.getOne(patientLambdaQueryWrapper);

        if(doctor == null){
            return R.error("不存在该医生！");
        }
        if(patient == null){
            return R.error("不存在该患者！");
        }

        casesDto.setDid(doctor.getId());
        casesDto.setPid(patient.getId());
        Hospital hospital = hospitalService.getById(doctor.getHid());
        casesDto.setHospitalName(hospital.getName());

        Cases cases = new Cases();
        BeanUtils.copyProperties(casesDto,cases);
        casesService.save(cases);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("病例添加成功！");
    }

    @DeleteMapping("/delete")
    public R<String> delete(Long[] ids){
        casesService.removeByIds(Arrays.asList(ids));

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("病例删除成功！");
    }

    @GetMapping("/get")
    public R<CasesDto> get(Long id){

        //这里怎么又null了？
        Cases cases = casesService.getById(id);
        CasesDto casesDto = new CasesDto();
        Doctor doctor = doctorService.getById(cases.getDid());
        Patient patient = patientService.getById(cases.getPid());
        Hospital hospital = hospitalService.getById(doctor.getHid());
        BeanUtils.copyProperties(cases, casesDto);
        casesDto.setDoctorName(doctor.getName());
        casesDto.setPatientName(patient.getName());
        casesDto.setHospitalName(hospital.getName());

        return R.success(casesDto);
    }

    @PostMapping("/status/{newStatus}")
    public R<String> changeStatus(Long[] ids, @PathVariable Boolean newStatus){
        LambdaQueryWrapper<Drug> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Drug::getId, ids);
        List<Drug> drugs = drugService.list(lambdaQueryWrapper);
        for(Drug drug : drugs){
            drug.setStatus(newStatus);
        }
        drugService.updateBatchById(drugs);

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("cases_*");
        redisTemplate.delete(redisKey);
        return R.success("状态更改成功！");
    }
}
