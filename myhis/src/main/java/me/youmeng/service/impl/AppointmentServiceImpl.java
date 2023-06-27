package me.youmeng.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Appointment;
import me.youmeng.domain.Cases;
import me.youmeng.mapper.AppointmentMapper;
import me.youmeng.mapper.CasesMapper;
import me.youmeng.service.AppointmentService;
import me.youmeng.service.CasesService;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
}
