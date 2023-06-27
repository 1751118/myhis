package me.youmeng.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Cases;

import me.youmeng.mapper.CasesMapper;

import me.youmeng.service.CasesService;
import org.springframework.stereotype.Service;

@Service
public class CasesServiceImpl extends ServiceImpl<CasesMapper, Cases> implements CasesService {
}
