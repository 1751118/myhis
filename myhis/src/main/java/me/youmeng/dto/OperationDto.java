package me.youmeng.dto;

import lombok.Data;
import me.youmeng.domain.Operation;

import java.util.ArrayList;
import java.util.List;


@Data
public class OperationDto extends Operation {
    //设备
    private List<String> devices = new ArrayList<>();
}
