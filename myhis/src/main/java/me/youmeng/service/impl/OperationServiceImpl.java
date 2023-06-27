package me.youmeng.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.youmeng.domain.Operation;
import me.youmeng.domain.Prescription;
import me.youmeng.mapper.OperationMapper;
import me.youmeng.mapper.PrescriptionMapper;
import me.youmeng.service.OperationService;
import me.youmeng.service.PrescriptionService;
import org.springframework.stereotype.Service;

@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation> implements OperationService {
}
