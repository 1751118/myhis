package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.dto.PrescriptionDto;
import me.youmeng.dto.PrescriptionDto;
import me.youmeng.service.DrugService;
import me.youmeng.service.IncludeService;
import me.youmeng.service.PrescriptionService;
import me.youmeng.service.HospitalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/prescription")
@Slf4j
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private IncludeService includeService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param prescriptionId
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String prescriptionId){

        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);
        Page<PrescriptionDto> prescriptionDtoPage = new Page<>();

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "prescription_" + (prescriptionId == null?"":prescriptionId)
                + "_" + page + "_" + pageSize;

        prescriptionDtoPage = (Page) redisTemplate.opsForValue().get(redisKey);
        if(prescriptionDtoPage != null){
            return R.success(prescriptionDtoPage);
        }else{
            prescriptionDtoPage = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Prescription> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3、模糊查询条件设置
        if(prescriptionId != null){
            lambdaQueryWrapper.like(Prescription::getId, prescriptionId);
        }
        //4、执行查询
        prescriptionService.page(pageInfo,lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, prescriptionDtoPage);

        List<Prescription> records = pageInfo.getRecords();

        List<PrescriptionDto> lists =records.stream().map((record)->{
            //查询include表，找出属于该处方的所有处方
            PrescriptionDto prescriptionDto = new PrescriptionDto();
            BeanUtils.copyProperties(record, prescriptionDto);

            LambdaQueryWrapper<Include> includeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            includeLambdaQueryWrapper.eq(Include::getPreid, prescriptionDto.getId());
            List<Include> includes = includeService.list(includeLambdaQueryWrapper);

            List<Long> drugsId = new ArrayList<>();
            for(Include include : includes){
                drugsId.add(include.getDrugid());
            }
            List<Drug> drugs = drugService.listByIds(drugsId);
            for(Drug drug : drugs){
                prescriptionDto.getDrugs().add(drug.getName());
            }

            return prescriptionDto;
        }).collect(Collectors.toList());

        prescriptionDtoPage.setRecords(lists);
        redisTemplate.opsForValue().set(redisKey, prescriptionDtoPage,60, TimeUnit.MINUTES);
        return R.success(prescriptionDtoPage);
    }

    @PutMapping("/edit")
    public R<String> editFinish(@RequestBody Prescription prescription){
        prescriptionService.updateById(prescription);

        //如果数据有改动，清除所有与key以，prescription_开头的缓存
        Set redisKey = redisTemplate.keys("prescription_*");
        redisTemplate.delete(redisKey);
        return R.success("处方信息修改成功");
    }
    @DeleteMapping("/delete")
    @Transactional
    public R<String> delete(Long[] ids){
        LambdaQueryWrapper<Prescription> prescriptionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        prescriptionLambdaQueryWrapper.in(Prescription::getId, ids);
        prescriptionService.remove(prescriptionLambdaQueryWrapper);

        LambdaQueryWrapper<Include> includeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        includeLambdaQueryWrapper.in(Include::getPreid, ids);
        includeService.remove(includeLambdaQueryWrapper);

        //如果数据有改动，清除所有与key以，prescription_开头的缓存
        Set redisKey = redisTemplate.keys("prescription_*");
        redisTemplate.delete(redisKey);
        return R.success("处方删除成功！");
    }

    @PostMapping("/add")
    @Transactional
    public R<String> add(@RequestBody PrescriptionDto prescriptionDto){
        //做一个简单的判别，判别库中是否存在输入的药品，不存在，处方保存失败
        List<String> drugs = prescriptionDto.getDrugs();

        List<Long> drugsIds = new ArrayList<>();
        for(String deviceName : drugs){
            LambdaQueryWrapper<Drug> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Drug::getName,deviceName);
            Drug device = drugService.getOne(lambdaQueryWrapper);
            if(device == null){
                return R.error(deviceName + "不存在，请确定药品名称，并确保输入方式正确！");
            }
            drugsIds.add(device.getId());
        }
        prescriptionService.save(prescriptionDto);

        //同时要把处方的的include表更新
        for (Long drugId : drugsIds) {
            Include include = new Include();
            include.setPreid(prescriptionDto.getId());
            include.setDrugid(drugId);
            includeService.save(include);
        }

        //如果数据有改动，清除所有与key以，prescription_开头的缓存
        Set redisKey = redisTemplate.keys("prescription_*");
        redisTemplate.delete(redisKey);
        return R.success("处方添加成功！");
    }

    @GetMapping("/get")
    @Transactional
    public R<PrescriptionDto> get(Long id){
        Prescription prescription = prescriptionService.getById(id);
        PrescriptionDto prescriptionDto = new PrescriptionDto();
        BeanUtils.copyProperties(prescription,prescriptionDto);

        //到include表中去拿属于该id的药品的名称
        LambdaQueryWrapper<Include> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Include::getPreid, prescription.getId());
        List<Include> includes = includeService.list(lambdaQueryWrapper);

        List<Long> drugIds = new ArrayList<>();
        for(Include include : includes){
            drugIds.add(include.getDrugid());
        }
        if(drugIds.size() != 0){
            for(Long drugId : drugIds){
                Drug drug = drugService.getById(drugId);
                prescriptionDto.getDrugs().add(drug.getName());
            }
        }
        return R.success(prescriptionDto);
    }
}
