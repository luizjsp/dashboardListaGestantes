package br.com.mv.clinic.service.obstetric_plan_delivery_dashboard;


import br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard.*;
import br.com.mv.clinic.enums.obstetric_plan.PregnancyRisk;
import br.com.mv.clinic.enums.obstetric_plan_delivery_dashboard.Comparison;
import br.com.mv.clinic.enums.obstetric_plan_delivery_dashboard.DateClassification;
import br.com.mv.clinic.repository.obstetric_plan_delivery_dashboard.DeliveryDashboardRepository;
import br.com.mv.clinic.service.AbstractMessage;
import br.com.mv.clinic.util.DateUtils;
import br.com.mv.clinic.util.HeaderUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryDashBoardService extends AbstractMessage {

    @Autowired
    private final DeliveryDashboardRepository deliveryDashboardRepository;

    public DeliveryDashBoardService(DeliveryDashboardRepository deliveryDashboardRepository) {
        this.deliveryDashboardRepository = deliveryDashboardRepository;
    }

    public QuantityIndicatorsDTO getBirthingPanelPregnantTotals() throws IOException {

        int weekInterval = 37;

        QuantityIndicatorsDTO quantityIndicatorsDTO = new QuantityIndicatorsDTO();

        String clientKey = HeaderUtil.getClientKey();
        Long subscriptionId = HeaderUtil.getSubscriptionId() != null ? HeaderUtil.getSubscriptionId() : null;

        if (this.isCallCenterAccount()) {

            quantityIndicatorsDTO.setTotalPregnant(deliveryDashboardRepository.getTotalPregnantByClientKey(
                    clientKey).intValue());

            quantityIndicatorsDTO.setTotalPregnantHighRisk(deliveryDashboardRepository.getTotalPregnantWithHighRiskByClientKey(
                    clientKey).intValue());

            quantityIndicatorsDTO.setTotalPregnantUseApp(deliveryDashboardRepository.getTotalPregnantUseAppByClientKey(
                    clientKey).intValue());

            quantityIndicatorsDTO.setTotalPregnantDoNotUseApp(deliveryDashboardRepository.getTotalPregnantDoNotUseAppByClientKey(
                    clientKey).intValue());

            quantityIndicatorsDTO.setLessThanInWeeks(
                    this.getPregnanciesLessThanOrGreaterThanInWeeks(Comparison.LESS_THAN, weekInterval, Boolean.TRUE)[0]);

            quantityIndicatorsDTO.setGreaterThanInWeeks(
                    this.getPregnanciesLessThanOrGreaterThanInWeeks(Comparison.GREATER_THAN, weekInterval, Boolean.TRUE)[1]);

        } else if (this.isClinicAccount()) {

            quantityIndicatorsDTO.setTotalPregnant(deliveryDashboardRepository.getTotalPregnantBySubscriptionId(subscriptionId).intValue());

            quantityIndicatorsDTO.setTotalPregnantHighRisk(deliveryDashboardRepository.getTotalPregnantWithHighRiskBySubscriptionId(subscriptionId).intValue());

            quantityIndicatorsDTO.setTotalPregnantUseApp(deliveryDashboardRepository.getTotalPregnantUseAppBySubscriptionId(subscriptionId).intValue());

            quantityIndicatorsDTO.setTotalPregnantDoNotUseApp(deliveryDashboardRepository.getTotalPregnantDoNotUseAppBySubscriptionId(subscriptionId).intValue());

            quantityIndicatorsDTO.setLessThanInWeeks(
                    this.getPregnanciesLessThanOrGreaterThanInWeeks(Comparison.LESS_THAN, weekInterval, Boolean.FALSE)[0]);

            quantityIndicatorsDTO.setGreaterThanInWeeks(
                    this.getPregnanciesLessThanOrGreaterThanInWeeks(Comparison.GREATER_THAN, weekInterval, Boolean.FALSE)[1]);

        }

        return quantityIndicatorsDTO;
    }

    public ChartDataDTO getConductedConsultationPerMonthByClientKey() throws IOException {
        int interval = 12;

        String clientKey = HeaderUtil.getClientKey();
        Long subscriptionId = HeaderUtil.getSubscriptionId() != null ? HeaderUtil.getSubscriptionId() : null;

        List<Object[]> resultList = null;

        if (this.isCallCenterAccount()) {

            resultList = deliveryDashboardRepository.getConductedConsultationPerMonthByClientKey(
                    clientKey, interval);

        } else if (this.isClinicAccount()) {

            resultList = deliveryDashboardRepository.getConductedConsultationPerMonthBySubscriptionId(subscriptionId, interval);
        }

        ChartDataSeriesDTO chartDataSeries = new ChartDataSeriesDTO("");
        ChartDataDTO chartData = new ChartDataDTO();

        chartData.setXAxisCategories(this.getMonthNamesFrom(DateClassification.PAST, interval));
        chartDataSeries.setData(this.setData(resultList, interval, chartData.getXAxisCategories()));

        chartData.getSeries().add(chartDataSeries);

        return chartData;
    }

    public ChartDataDTO getForecastConsultationPerMonthByClientKey() throws IOException {
        int interval = 9;

        String clientKey = HeaderUtil.getClientKey();
        Long subscriptionId = HeaderUtil.getSubscriptionId() != null ? HeaderUtil.getSubscriptionId() : null;
        List<Object[]> resultList = null;
        List<Date> notScheduledList = null;

        if (this.isCallCenterAccount()) {

            resultList = deliveryDashboardRepository.getForecastConsultationPerMonthByClientKey(clientKey, interval);
            notScheduledList = deliveryDashboardRepository.getForecastConsultationsToBeScheduledByClientKey2(clientKey);

        } else if (this.isClinicAccount()) {

            resultList = deliveryDashboardRepository.getForecastConsultationPerMonthBySubscriptionId(subscriptionId, interval);
            notScheduledList = deliveryDashboardRepository.getForecastConsultationsToBeScheduledBySubscriptionId2(subscriptionId);
        }

        ChartDataDTO chartData = new ChartDataDTO();
        ChartDataSeriesDTO chartDataSeries = new ChartDataSeriesDTO("Recomendadas");

        chartData.setXAxisCategories(this.getMonthNamesFrom(DateClassification.FUTURE, interval));
        chartDataSeries.setData(this.setData(resultList, interval, chartData.getXAxisCategories()));

        chartDataSeries.setBorderColor("#40728d");
        chartDataSeries.setPointBackgroundcolor("#40728d");

        ChartDataSeriesDTO forecastNotScheduleChartSeries = new ChartDataSeriesDTO("Agendadas");

        forecastNotScheduleChartSeries.setBorderColor("#404c8d");
        forecastNotScheduleChartSeries.setPointBackgroundcolor("#404c8d");

//        if (notScheduledList != null) {
//            for (Object[] notScheduled : notScheduledList) {
//
//                Object monthNumber = notScheduled[0];
//                Object value = notScheduled[1];
//
//                forecastNotScheduleChartSeries.getData().add(((BigInteger) value).intValue());
//            }
//        }

        forecastNotScheduleChartSeries.setData(this.getForecastConsultationPerMonth(notScheduledList, interval));

        chartData.getSeries().add(chartDataSeries);
        chartData.getSeries().add(forecastNotScheduleChartSeries);

        return chartData;
    }

    public ChartDataDTO getForecastDeliveriesPerMonthByClientKey() throws IOException {
        int interval = 9;

        String clientKey = HeaderUtil.getClientKey();
        Long subscriptionId = HeaderUtil.getSubscriptionId() != null ? HeaderUtil.getSubscriptionId() : null;
        List<Object[]> resultList = null;

        if (this.isCallCenterAccount()) {

            resultList = deliveryDashboardRepository.getForecastDeliveriesPerMonthByClientKey(clientKey, interval);

        } else if (this.isClinicAccount()) {

            resultList = deliveryDashboardRepository.getForecastDeliveriesPerMonthBySubscriptionId(subscriptionId, interval);
        }

        ChartDataSeriesDTO chartDataSeries = new ChartDataSeriesDTO("");
        ChartDataDTO chartData = new ChartDataDTO();

        chartData.setXAxisCategories(this.getMonthNamesFrom(DateClassification.FUTURE, interval));
        chartDataSeries.setData(this.setData(resultList, interval, chartData.getXAxisCategories()));

        chartData.getSeries().add(chartDataSeries);

        return chartData;
    }

    private List<String> getMonthNamesFrom(DateClassification period, int interval) {

        List<String> months = new ArrayList<String>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        LocalDate date = null;

        if (period.equals(DateClassification.FUTURE)) {
            date = LocalDate.now();
        } else if (period.equals(DateClassification.PAST)) {
            date = LocalDate.now().plusMonths(1);
        }

        for (int i = 0; i < interval; i++) {
            months.add(formatter.format(date));
            date = date.plusMonths(1);
        }

        return months;
    }

    private List<Integer> setData(List<Object[]> resultSetList, int interval, List<String> months) {

        if (resultSetList == null) {
            return new ArrayList<Integer>();
        }

        int[] data = new int[interval];

        for (Object[] obj : resultSetList) {

            Object monthNumber = obj[0];
            Object value = obj[1];

            int monthIndex = 0;
            for (String m : months) {

                Month monthName = Month.valueOf(m.toUpperCase());
                int numberOfMonth = monthName.getValue();

                if (numberOfMonth == (Integer.parseInt(monthNumber.toString()))) {
                    data[monthIndex] = Integer.parseInt(value.toString());
                }

                monthIndex++;
            }

        }

        return Arrays.stream(data).boxed().collect(Collectors.toList());
    }

    private boolean isClinicAccount() {
        return HeaderUtil.getAccountType() == null;
    }

    private boolean isCallCenterAccount() {
        if (HeaderUtil.getAccountType() != null) {
            return HeaderUtil.getAccountType().equalsIgnoreCase("CALLCENTER");
        }
        return false;
    }

    private List<Integer> getForecastConsultationPerMonth(List<Date> deliveryDates, int interval) {

        if (Objects.isNull(deliveryDates)) {
            return null;
        }

        Integer[] amountPerMonth = new Integer[interval];
        Arrays.fill(amountPerMonth, 0);

        for (Date deliveryDate : deliveryDates) {

            int amount = DateUtils.getMonthDifference(new Date(), deliveryDate);

            for (int i = 0; i <= amount - 1; i++) {
                if (amountPerMonth[i] == null) {
                    amountPerMonth[i] = 1;
                } else {
                    amountPerMonth[i] += 1;
                }
            }
        }
        ;

        return Arrays.asList(amountPerMonth);
    }

    private Integer[] getPregnanciesLessThanOrGreaterThanInWeeks(Comparison comparison, Integer weeks, boolean isCallCenter) {

        List<Object[]> rawData = isCallCenter ?
                deliveryDashboardRepository
                        .getPregnanciesMoreThanAndGreaterThanInWeeksByClientKey(HeaderUtil.getClientKey(), weeks) :
                deliveryDashboardRepository
                        .getPregnanciesMoreThanAndGreaterThanInWeeksBysubscriptionId(HeaderUtil.getSubscriptionId(), weeks);

        Integer greaterThan = null;
        Integer lessThan = null;

        for (Object[] quantity : rawData) {
            lessThan = quantity[0] != null ? Integer.parseInt(String.valueOf(quantity[0])) : 0;
            greaterThan = quantity[1] != null ? Integer.parseInt(String.valueOf(quantity[1])) : 0;
        }

        return new Integer[]{lessThan, greaterThan};

    }

    @Transactional(readOnly = true)
    public Page<DashboardDTO> findAll(Pageable pageable, DashboardFilterDTO filter) {

        String clientKey = HeaderUtil.getClientKey();
        Long subscriptionId = HeaderUtil.getSubscriptionId() != null ? HeaderUtil.getSubscriptionId() : null;

        List<DashboardDTO> list = new ArrayList<>();
        Page<Object[]> pregnancyInfo = null;

        if (this.isCallCenterAccount()) {
            pregnancyInfo = this.deliveryDashboardRepository.listAllClientKey(pageable, filter.getEmployeeId(), clientKey, filter.getPregnancy(), filter.getInitialWeek(), filter.getFinalWeek(),
                    filter.getDpc(), filter.getStartDPC(), filter.getEndDPC(), filter.getLastAttendanceDate(), filter.getEndAttendanceDate());
        } else if (this.isClinicAccount()) {
            pregnancyInfo = this.deliveryDashboardRepository.listAllSubscriptionId(pageable, filter.getEmployeeId(), subscriptionId, filter.getPregnancy(), filter.getInitialWeek(), filter.getFinalWeek(),
                    filter.getDpc(), filter.getStartDPC(), filter.getEndDPC(), filter.getLastAttendanceDate(), filter.getEndAttendanceDate());
        }

        if (pregnancyInfo != null) {
            for (Object[] obsPlan : pregnancyInfo) {
                DashboardDTO dto = new DashboardDTO();

                dto.setId((BigInteger) obsPlan[0]);
                dto.setPregnancy((Integer) obsPlan[1]);
                dto.setName((String) obsPlan[2]);
            /*
            Conversão de TimeStamp em LocalDateTime e Calculo da idade em inteiro.
             */
                LocalDateTime birthDateTime = LocalDateTime.ofInstant(((Date) obsPlan[3]).toInstant(), ZoneId.systemDefault());
                LocalDate birthDate = birthDateTime.toLocalDate();
                int age = calculateAge(birthDate);
                dto.setPatientAge(age);
                dto.setLastAttendance((BigInteger) obsPlan[4]);
                dto.setBirthAppointment((BigInteger) obsPlan[5]);
                dto.setCellPhone((String) obsPlan[6]);
                dto.setEmail((String) obsPlan[7]);
                dto.setSubscriptionId((BigInteger) obsPlan[8]);
                dto.setClientKey((String) obsPlan[9]);
                dto.setActive((Boolean) obsPlan[10]);
                dto.setDum((Date) obsPlan[11]);
                dto.setDppus((Date) obsPlan[12]);
                dto.setDpc((Date) obsPlan[13]);
                dto.setPatientId((BigInteger) obsPlan[14]);
                dto.setPregnancies((Integer) obsPlan[15]);
                dto.setDeliveries((Integer) obsPlan[16]);
                dto.setAbortions((Integer) obsPlan[17]);
                dto.setPicture((String) obsPlan[18]);
                dto.setEmployeeId((BigInteger) obsPlan[19]);
                dto.setFullName((String) obsPlan[20]);
                DateTime lastAttendance = new DateTime(obsPlan[21]);
                dto.setLastAttendanceDate(lastAttendance);
                DateTime nextConsultation = new DateTime(obsPlan[22]);
                dto.setNextConsultationDate(nextConsultation);
            /*
            Conversão de Date dum String para LocalDate e Calculo da semana gestacional.
             */
//            int initialWeek = 1;
//            int finalWeek = 40;
//            if (gestationalAgeInWeeks >= initialWeek && gestationalAgeInWeeks <= finalWeek) {

                Date dumDate = dto.getDum(); // Supondo que dto.getDum() seja do tipo java.util.Date

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = dateFormat.format(dumDate);

                LocalDate dumLocalDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                LocalDate today = LocalDate.now();

                long gestationalAgeInDays = ChronoUnit.DAYS.between(dumLocalDate, today);
                int gestationalAgeInWeeks = (int) (gestationalAgeInDays / 7);
                dto.setGestationalAgeInWeeks(gestationalAgeInWeeks);

                String gpaValue = setGpaValue(dto.getPregnancies(), dto.getDeliveries(), dto.getAbortions());
                dto.setGpaValue(gpaValue);

                list.add(dto);

            }
        }

        return new PageImpl<>(list, pageable, pregnancyInfo.getTotalElements());

    }

    private String setGpaValue(Integer pPregnancies, Integer dDeliveries, Integer aAbortions) {

        int pregnancies = !Objects.isNull(pPregnancies) ? pPregnancies : 0;
        int deliveries = !Objects.isNull(dDeliveries) ? dDeliveries : 0;
        int abortions = !Objects.isNull(aAbortions) ? aAbortions : 0;

        return "G".concat(String.valueOf(pregnancies))
                .concat("P").concat(String.valueOf(deliveries))
                .concat("A").concat(String.valueOf(abortions));

    }

    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    @Transactional(readOnly = true)
    public Page<DashboardDTOV2> findAllV2(Pageable pageable, DashboardFilterDTOV2 filter) throws ParseException,
            IllegalAccessException, IOException {

        String clientKey = HeaderUtil.getClientKey();
        Long subscriptionId = HeaderUtil.getSubscriptionId();

        if (clientKey == null && subscriptionId == null) {
            throwsException("error.obstetricPlan.delivery_dashboard.header.empty");
        }

        Page<Object[]> resultData = null;

        Integer pregnancyRisk = (filter.getPregnancy() != null) ? filter.getPregnancy().ordinal() : null;

        if (isCallCenterAccount()) {
            resultData = deliveryDashboardRepository.listAllClientKeyV2(pageable, filter.getEmployeeId(), pregnancyRisk,
                    filter.getInitialWeek(), filter.getFinalWeek(), filter.getScheduledBirth(),
                    filter.getInitLastAttendanceDate(), filter.getEndLastAttendanceDate(), filter.getInitDPC(),
                    filter.getEndDPC(), filter.getHaveApp(), clientKey);
        } else if (isClinicAccount()) {
            resultData = deliveryDashboardRepository.listAllBySubscriptionIdV2(pageable, filter.getEmployeeId(),
                    pregnancyRisk, filter.getInitialWeek(), filter.getFinalWeek(), filter.getScheduledBirth(),
                    filter.getInitLastAttendanceDate(), filter.getEndLastAttendanceDate(), filter.getInitDPC(),
                    filter.getEndDPC(), filter.getHaveApp(), subscriptionId);
        }

        List<DashboardDTOV2> dashboardDTOV2List = new ArrayList<>();

        if (resultData != null) {
            for (Object[] rawData : resultData) {
                DashboardDTOV2 dashboardDTOV2 = new DashboardDTOV2();

                dashboardDTOV2.setId((BigInteger) rawData[0]);
                dashboardDTOV2.setDum((Date) rawData[1]);
                dashboardDTOV2.setGestationalAgeWeeks((Integer) rawData[2]);
                dashboardDTOV2.setDppus((Date) rawData[3]);
                dashboardDTOV2.setDpc((Date) rawData[4]);

                if (rawData[5] != null) {
                    PregnancyRisk[] risks = PregnancyRisk.values();
                    int index = (Integer) rawData[5];
                    dashboardDTOV2.setPregnancy(risks[index]);
                }

                dashboardDTOV2.setPatientName(rawData[6] != null ? (String) rawData[6] : "");
                dashboardDTOV2.setPatientEmail(rawData[7] != null ? (String) rawData[7] : "");
                dashboardDTOV2.setPatientPhone(rawData[8] != null ? (String) rawData[8] : "");
                dashboardDTOV2.setPatientAge((BigInteger) rawData[9]);
                dashboardDTOV2.setPatientPicture(rawData[10] != null ? (String) rawData[10] : "");
                dashboardDTOV2.setGpaValue(rawData[11] != null ? (String) rawData[11] : "");
                dashboardDTOV2.setLastAttendanceDate((Date) rawData[12]);
                dashboardDTOV2.setLastAttendanceClinicName((String) rawData[13]);
                dashboardDTOV2.setNextConsultationDate(rawData[14] != null ? new DateTime(rawData[14]) : null);
                dashboardDTOV2.setBirthAppointmentDate(rawData[15] != null ? new DateTime(rawData[15]) : null);
                dashboardDTOV2.setScheduledBirth(rawData[16] != null && (Integer) rawData[16] == 1);
                dashboardDTOV2.setBirthAppointmentEmployeeFullName((String) rawData[17]);
                dashboardDTOV2.setHaveApp(rawData[19] != null && (Integer) rawData[19] == 1);
                dashboardDTOV2.setPatientId((BigInteger) rawData[20]);

                dashboardDTOV2List.add(dashboardDTOV2);
            }
        }

        if (resultData != null) {
            Objects.requireNonNull(dashboardDTOV2List);
            long totalElements = resultData.getTotalElements();
            return new PageImpl<>(dashboardDTOV2List, pageable, totalElements);
        } else {
            return null;
        }
    }


}