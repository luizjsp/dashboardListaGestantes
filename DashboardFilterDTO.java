package br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardFilterDTO {

    private BigInteger employeeId;

    private Long subscriptionId;

    private Integer pregnancy;

    private Integer initialWeek;

    private Integer finalWeek;

    private String dpc;

    private String startDPC;

    private String endDPC;

    private String lastAttendanceDate;

    private String endAttendanceDate;

    private String nextConsultationDate;


}