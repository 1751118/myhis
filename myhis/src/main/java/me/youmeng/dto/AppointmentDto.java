package me.youmeng.dto;

import lombok.Data;
import me.youmeng.domain.Appointment;
import me.youmeng.domain.Cases;

@Data
public class AppointmentDto extends Appointment {

    //医生名
    private String doctorName;

    //患者名
    private String patientName;

    //医院名
    private String hospitalName;
}
