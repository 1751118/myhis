package me.youmeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.youmeng.domain.Operation;
import me.youmeng.domain.Prescription;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationMapper extends BaseMapper<Operation> {
}
