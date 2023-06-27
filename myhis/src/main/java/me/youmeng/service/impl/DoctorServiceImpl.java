package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Doctor;
import me.youmeng.domain.User;
import me.youmeng.mapper.DoctorMapper;
import me.youmeng.mapper.UserMapper;
import me.youmeng.service.DoctorService;
import me.youmeng.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
}
