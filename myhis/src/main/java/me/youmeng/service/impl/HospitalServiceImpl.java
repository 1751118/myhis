package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Doctor;
import me.youmeng.domain.Hospital;
import me.youmeng.mapper.DoctorMapper;
import me.youmeng.mapper.HospitalMapper;
import me.youmeng.service.DoctorService;
import me.youmeng.service.HospitalService;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements HospitalService {
}
