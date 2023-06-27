package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.*;
import me.youmeng.dto.OperationDto;
import me.youmeng.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operation")
@Slf4j
public class OperationController {

    @Autowired
    private OperationService operationService;

    @Autowired
    private NeedService needService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param operationId
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String operationId, String operationName){

        //1、分页查询对象构造
        Page pageInfo = new Page(page,pageSize);
        Page<OperationDto> operationDtoPage = new Page<>();

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "operation_" + (operationId == null?"":operationId)
                + "_" + (operationName == null?"":operationName)
                + "_" + page + "_" + pageSize;

        operationDtoPage = (Page) redisTemplate.opsForValue().get(redisKey);
        if(operationDtoPage != null){
            return R.success(operationDtoPage);
        }else{
            operationDtoPage = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Operation> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3、模糊查询条件设置
        if(operationId != null){
            lambdaQueryWrapper.like(Operation::getId, operationId);
        }
        if(operationName != null){
            lambdaQueryWrapper.like(Operation::getName, operationName);
        }
        //4、执行查询
        operationService.page(pageInfo,lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, operationDtoPage);

        List<Operation> records = pageInfo.getRecords();

        List<OperationDto> lists =records.stream().map((record)->{
            //查询need表，找出属于该处方的所有药品
            OperationDto operationDto = new OperationDto();
            BeanUtils.copyProperties(record, operationDto);

            LambdaQueryWrapper<Need> needLambdaQueryWrapper = new LambdaQueryWrapper<>();
            needLambdaQueryWrapper.eq(Need::getOpid, operationDto.getId());
            List<Need> needs = needService.list(needLambdaQueryWrapper);

            List<Long> devicesId = new ArrayList<>();
            for(Need need : needs){
                devicesId.add(need.getDevid());
            }
            if(devicesId.size() != 0){
                List<Device> devices = deviceService.listByIds(devicesId);
                for(Device device : devices){
                    operationDto.getDevices().add(device.getName());
                }
            }
            return operationDto;
        }).collect(Collectors.toList());

        operationDtoPage.setRecords(lists);
        redisTemplate.opsForValue().set(redisKey, operationDtoPage,60, TimeUnit.MINUTES);
        return R.success(operationDtoPage);
    }

    @PutMapping("/edit")
    public R<String> editFinish(@RequestBody Operation operation){
        operationService.updateById(operation);

        //如果数据有改动，清除所有与key以，operation_开头的缓存
        Set redisKey = redisTemplate.keys("operation_*");
        redisTemplate.delete(redisKey);
        return R.success("手术信息修改成功");
    }
    @DeleteMapping("/delete")
    @Transactional
    public R<String> delete(Long[] ids){
        LambdaQueryWrapper<Operation> operationLambdaQueryWrapper = new LambdaQueryWrapper<>();
        operationLambdaQueryWrapper.in(Operation::getId, ids);
        operationService.remove(operationLambdaQueryWrapper);

        //更新need表，将id等于该手术id的need记录删除
        for(Long id : ids){
            LambdaQueryWrapper<Need> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Need::getOpid, id);
            needService.remove(lambdaQueryWrapper);
        }

        //如果数据有改动，清除所有与key以，operation_开头的缓存
        Set redisKey = redisTemplate.keys("operation_*");
        redisTemplate.delete(redisKey);
        return R.success("手术删除成功！");
    }

    @PostMapping("/add")
    @Transactional
    public R<String> add(@RequestBody OperationDto operationDto){
        //做一个简单的判别，判别库中是否存在输入的设备，不存在，手术保存失败
        List<String> devices = operationDto.getDevices();

        List<Long> deviceIds = new ArrayList<>();
        for(String deviceName : devices){
            LambdaQueryWrapper<Device> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Device::getName,deviceName);
            Device device = deviceService.getOne(lambdaQueryWrapper);
            if(device == null){
                return R.error(deviceName + "不存在，请确定设备名称，并确保输入方式正确！");
            }
            deviceIds.add(device.getId());
        }
        operationService.save(operationDto);

        //同时要把手术的need表更新
        for (Long deviceId : deviceIds) {
            Need need = new Need();
            need.setOpid(operationDto.getId());
            need.setDevid(deviceId);
            needService.save(need);
        }

        //如果数据有改动，清除所有与key以，appointment_开头的缓存
        Set redisKey = redisTemplate.keys("operation_*");
        redisTemplate.delete(redisKey);
        return R.success("手术添加成功！");
    }

    @GetMapping("/get")
    public R<OperationDto> get(Long id){
        Operation operation = operationService.getById(id);
        OperationDto operationDto = new OperationDto();
        BeanUtils.copyProperties(operation,operationDto);

        //到need表中去拿属于该id的设备的名称
        LambdaQueryWrapper<Need> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Need::getOpid, operation.getId());
        List<Need> needs = needService.list(lambdaQueryWrapper);

        List<Long> deviceIds = new ArrayList<>();
        for(Need need : needs){
            deviceIds.add(need.getDevid());
        }
        if(deviceIds.size() != 0){
            for(Long deviceId : deviceIds){
                Device device = deviceService.getById(deviceId);
                operationDto.getDevices().add(device.getName());
            }
        }
        return R.success(operationDto);
    }
}
