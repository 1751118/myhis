package me.youmeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.youmeng.domain.Need;
import me.youmeng.domain.Prescription;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NeedMapper extends BaseMapper<Need> {
}
