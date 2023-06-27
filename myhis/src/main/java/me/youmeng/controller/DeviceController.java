package me.youmeng.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import me.youmeng.common.R;
import me.youmeng.domain.Device;
import me.youmeng.domain.Doctor;
import me.youmeng.domain.Hospital;
import me.youmeng.domain.Patient;
import me.youmeng.dto.DeviceDto;
import me.youmeng.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/device")
@Slf4j
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

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
        Page deviceDtoInfo = new Page(page,pageSize);

        //如果没有按条件查询的话，封装redids的key，这里用菜单项名称 + 模糊查询的关键字作为缓存
        String redisKey = "device_" + (name == null?"":name) + "_"
                                            + page + "_" + pageSize;
        deviceDtoInfo = (Page) redisTemplate.opsForValue().get(redisKey);
        if(deviceDtoInfo != null){
            return R.success(deviceDtoInfo);
        }else{
            deviceDtoInfo = new Page(page,pageSize);
        }

        //2、构造条件构造器
        LambdaQueryWrapper<Device> deviceLambdaQueryWrapper = new LambdaQueryWrapper<Device>();

        //3、模糊查询条件设置（按照医生、患者姓名模糊查询）
        if(name != null){
            deviceLambdaQueryWrapper.like(Device::getName, name);
        }
        //4、执行查询
        deviceService.page(pageInfo,deviceLambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, deviceDtoInfo);

        //5、封装CasesDto
        List<Device> records = pageInfo.getRecords();
        List<DeviceDto> lists = records.stream().map(record ->{
            DeviceDto deviceDto = new DeviceDto();
            BeanUtils.copyProperties(record, deviceDto);
            return deviceDto;
        }).collect(Collectors.toList());

        //6、设置caseDtoInfo的记录
        deviceDtoInfo.setRecords(lists);

        //设置key为redisKey的数据deviceDtoInfo，在redis中缓存60分钟，
        // 从此刻起的60分钟，用户再查询预约表，将不会查询数据库，直接从redis中取数据（除非redis中的缓存数据被删除）
        //这样可以缓解数据库的压力
        redisTemplate.opsForValue().set(redisKey, deviceDtoInfo,60, TimeUnit.MINUTES);


        return R.success(deviceDtoInfo);
    }

    @PutMapping("/edit")
    public R<String> editFinish(@RequestBody DeviceDto deviceDto){
        deviceService.updateById(deviceDto);

        //如果数据有改动，清除所有与key以，device_开头的缓存
        Set redisKey = redisTemplate.keys("device_*");
        redisTemplate.delete(redisKey);
        return R.success("设备信息修改成功");
    }

    @PostMapping("/add")
    public R<String> add(@RequestBody DeviceDto deviceDto){

        Device device = new Device();
        BeanUtils.copyProperties(deviceDto,device);
        deviceService.save(device);

        //如果数据有改动，清除所有与key以，device_开头的缓存
        Set redisKey = redisTemplate.keys("device_*");
        redisTemplate.delete(redisKey);
        return R.success("设备添加成功！");
    }

    @DeleteMapping("/delete")
    public R<String> delete(Long[] ids){
        deviceService.removeByIds(Arrays.asList(ids));
        //如果数据有改动，清除所有与key以，device_开头的缓存
        Set redisKey = redisTemplate.keys("device_*");
        redisTemplate.delete(redisKey);
        return R.success("预约删除成功！");
    }

    @GetMapping("/get")
    public R<DeviceDto> get(Long id){

        //编辑预约信息时的回显
        Device device = deviceService.getById(id);
        DeviceDto deviceDto = new DeviceDto();

        BeanUtils.copyProperties(device, deviceDto);

        return R.success(deviceDto);
    }

}
