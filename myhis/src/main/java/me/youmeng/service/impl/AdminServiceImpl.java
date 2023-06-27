package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Admin;
import me.youmeng.domain.User;
import me.youmeng.mapper.AdminMapper;
import me.youmeng.mapper.UserMapper;
import me.youmeng.service.AdminService;
import me.youmeng.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
}
