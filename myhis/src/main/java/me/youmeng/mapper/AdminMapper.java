package me.youmeng.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.youmeng.domain.Admin;
import me.youmeng.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {
}
