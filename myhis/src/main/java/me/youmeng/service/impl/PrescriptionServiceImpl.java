package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Drug;
import me.youmeng.domain.Prescription;
import me.youmeng.mapper.DrugMapper;
import me.youmeng.mapper.PrescriptionMapper;
import me.youmeng.service.DrugService;
import me.youmeng.service.PrescriptionService;
import org.springframework.stereotype.Service;

@Service
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription> implements PrescriptionService {
}
