package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.Drug;
import me.youmeng.domain.Hospital;
import me.youmeng.dto.DrugDto;
import me.youmeng.service.DrugService;
import me.youmeng.service.HospitalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drug")
@Slf4j
public class DrugController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DrugService drugService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param drugName
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String drugName,String hospitalName){

        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);
        Page<DrugDto> drugDtoPage = new Page<>();

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "drug_" + (drugName == null?"":drugName) + "_"
                + (hospitalName == null?"":hospitalName) + "_" + page + "_" + pageSize;
        drugDtoPage = (Page) redisTemplate.opsForValue().get(redisKey);
        if(drugDtoPage != null){
            return R.success(drugDtoPage);
        }else{
            drugDtoPage = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Drug> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.orderByAsc(Drug::getHid);
        lambdaQueryWrapper.orderByDesc(Drug::getProduceTime);

        //3、模糊查询条件设置
        if(drugName != null){
            lambdaQueryWrapper.like(Drug::getName, drugName);
        }
        if(hospitalName != null){
            //现按医院的输入名模糊查询满足条件的id，再按id查询显示
            LambdaQueryWrapper<Hospital> lambdaQueryWrapper1 = new LambdaQueryWrapper<>();
            lambdaQueryWrapper1.like(Hospital::getName, hospitalName);
            List<Hospital> hospitals = hospitalService.list(lambdaQueryWrapper1);
            List<Long> ids = new ArrayList<>();
            for(Hospital hospital : hospitals){
                ids.add(hospital.getId());
            }
            //按医院id查询
            lambdaQueryWrapper.in(Drug::getHid, ids);
        }

        //5、执行查询
        drugService.page(pageInfo,lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, drugDtoPage);

        List<Drug> records = pageInfo.getRecords();

        List<DrugDto> lists =records.stream().map((record)->{
            DrugDto drugDto = new DrugDto();
            BeanUtils.copyProperties(record, drugDto);
            Hospital hospital = hospitalService.getById(record.getHid());
            drugDto.setHospital(hospital.getName());
            return drugDto;
        }).collect(Collectors.toList());

        drugDtoPage.setRecords(lists);
        redisTemplate.opsForValue().set(redisKey, drugDtoPage,60, TimeUnit.MINUTES);
        return R.success(drugDtoPage);
    }

    @PutMapping("/edit")
    public R<String> editFinish(@RequestBody Drug drug){
        drugService.updateById(drug);

        //如果数据有改动，清除所有与key以，drug_开头的缓存
        Set redisKey = redisTemplate.keys("drug_*");
        redisTemplate.delete(redisKey);
        return R.success("药品信息修改成功");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody Drug drug){
        LambdaQueryWrapper<Drug> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Drug::getName, drug.getName());
        lambdaQueryWrapper.eq(Drug::getHid, drug.getHid());
        Drug h = drugService.getOne(lambdaQueryWrapper);
        if(h != null){
            return R.error("该医院已经存在该药品!");
        }
        drug.setUpdateTime(LocalDateTime.now());
        drugService.save(drug);

        //如果数据有改动，清除所有与key以，drug_开头的缓存
        Set redisKey = redisTemplate.keys("drug_*");
        redisTemplate.delete(redisKey);
        return R.success("药品添加成功！");
    }

    @DeleteMapping("/delete")
    public R<String> delete(Long[] ids){
        drugService.removeByIds(Arrays.asList(ids));

        //如果数据有改动，清除所有与key以，drug_开头的缓存
        Set redisKey = redisTemplate.keys("drug_*");
        redisTemplate.delete(redisKey);
        return R.success("药品删除成功！");
    }

    @GetMapping("/get")
    public R<DrugDto> get(Long id){
        log.info("get到的id是：{}",id);
        Drug drug = drugService.getById(id);
        Hospital hospital = hospitalService.getById(drug.getHid());
        DrugDto drugDto = new DrugDto();
        BeanUtils.copyProperties(drug, drugDto);
        drugDto.setHospital(hospital.getName());
        return R.success(drugDto);
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

        //如果数据有改动，清除所有与key以，drug_开头的缓存
        Set redisKey = redisTemplate.keys("drug_*");
        redisTemplate.delete(redisKey);
        return R.success("状态更改成功！");
    }
}
