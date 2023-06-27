package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Drug;
import me.youmeng.mapper.DrugMapper;
import me.youmeng.service.DrugService;
import org.springframework.stereotype.Service;

@Service
public class DrugServiceImpl extends ServiceImpl<DrugMapper, Drug> implements DrugService {
}
