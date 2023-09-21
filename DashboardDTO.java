package br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard;

import br.com.mv.clinic.util.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDTO {

    private BigInteger id;

    private Integer pregnancy;

    private String name;

    private Integer patientAge;

    private BigInteger lastAttendance;

    private BigInteger birthAppointment;

    private String cellPhone;

    private String email;

    private BigInteger subscriptionId;

    private String clientKey;

    private Boolean active;

    @JsonDeserialize(using = DateDefaultDeserializer.class, as = Date.class)
    @JsonSerialize(using = DateDefaultSerializer.class)
    private Date dum;

    @JsonDeserialize(using = DateDefaultDeserializer.class, as = Date.class)
    @JsonSerialize(using = DateDefaultSerializer.class)
    private Date dppus;

    @JsonDeserialize(using = DateDefaultDeserializer.class, as = Date.class)
    @JsonSerialize(using = DateDefaultSerializer.class)
    private Date dpc;

    private BigInteger patientId;

    private Integer pregnancies;

    private Integer deliveries;

    private Integer abortions;

    private String picture;

    private BigInteger employeeId;

    private String fullName;

    @JsonDeserialize(using = DateTimeDeserializer.class, as = DateTime.class)
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime lastAttendanceDate;

    private String gpaValue;

    private Integer gestationalAgeInWeeks;

    @JsonDeserialize(using = DateTimeDeserializer.class, as = DateTime.class)
    @JsonSerialize(using = DateTimeSerializer.class)
    private DateTime nextConsultationDate;

}