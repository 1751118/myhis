package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Include;
import me.youmeng.domain.Need;
import me.youmeng.mapper.IncludeMapper;
import me.youmeng.mapper.NeedMapper;
import me.youmeng.service.IncludeService;
import me.youmeng.service.NeedService;
import org.springframework.stereotype.Service;

@Service
public class NeedServiceImpl extends ServiceImpl<NeedMapper, Need> implements NeedService {
}
