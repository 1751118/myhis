package me.youmeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.youmeng.domain.Drug;
import me.youmeng.domain.Include;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IncludeMapper extends BaseMapper<Include> {
}
