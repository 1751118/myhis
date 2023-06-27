package me.youmeng.dto;

import lombok.Data;
import me.youmeng.domain.Drug;
import me.youmeng.domain.Prescription;

import java.util.ArrayList;
import java.util.List;

@Data
public class PrescriptionDto extends Prescription {

    //药品列表
    private List<String> drugs = new ArrayList<>();
}
