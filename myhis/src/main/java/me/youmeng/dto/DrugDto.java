package me.youmeng.dto;

import lombok.Data;
import me.youmeng.domain.Doctor;
import me.youmeng.domain.Drug;

@Data
public class DrugDto extends Drug {

    //医院名
    private String hospital;

}
