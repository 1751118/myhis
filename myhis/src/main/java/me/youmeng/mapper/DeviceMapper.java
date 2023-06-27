package me.youmeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.youmeng.domain.Device;
import me.youmeng.domain.Hospital;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
}
