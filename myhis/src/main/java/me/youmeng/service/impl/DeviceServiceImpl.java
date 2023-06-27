package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Device;
import me.youmeng.domain.Hospital;
import me.youmeng.mapper.DeviceMapper;
import me.youmeng.mapper.HospitalMapper;
import me.youmeng.service.DeviceService;
import me.youmeng.service.HospitalService;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {
}
