package me.youmeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.youmeng.domain.Appointment;
import me.youmeng.domain.Cases;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
