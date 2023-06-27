package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Doctor;
import me.youmeng.domain.Patient;
import me.youmeng.mapper.DoctorMapper;
import me.youmeng.mapper.PatientMapper;
import me.youmeng.service.DoctorService;
import me.youmeng.service.PatientService;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {
}
