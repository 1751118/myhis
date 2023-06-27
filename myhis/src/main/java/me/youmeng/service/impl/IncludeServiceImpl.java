package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Drug;
import me.youmeng.domain.Include;
import me.youmeng.mapper.DrugMapper;
import me.youmeng.mapper.IncludeMapper;
import me.youmeng.service.DrugService;
import me.youmeng.service.IncludeService;
import org.springframework.stereotype.Service;

@Service
public class IncludeServiceImpl extends ServiceImpl<IncludeMapper, Include> implements IncludeService {
}
