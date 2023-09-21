package br.com.mv.clinic.repository.obstetric_plan_delivery_dashboard;

import br.com.mv.clinic.domain.obstetric_plan.ObstetricPlan;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface DeliveryDashboardRepository extends JpaRepository<ObstetricPlan, Long>, JpaSpecificationExecutor<ObstetricPlan> {


    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant " +
            "FROM obstetric_plan op " +
            "WHERE op.active = true " +
            "AND op.deleted = false " +
            "AND op.client_key = :clientKey", nativeQuery = true)
    BigInteger getTotalPregnantByClientKey(@Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant_risk_high " +
            "FROM obstetric_plan op " +
            "WHERE op.active = true " +
            "AND op.pregnancy = 1 " +
            "AND op.deleted = false " +
            "AND op.client_key = :clientKey" , nativeQuery = true)
    BigInteger getTotalPregnantWithHighRiskByClientKey(@Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant_use_app " +
            "FROM obstetric_plan op " +
            "JOIN patient p ON op.patient_id = p.id " +
            "WHERE op.active = TRUE " +
            "AND op.deleted = false " +
            "AND op.client_key = :clientKey " +
            "AND p.login_personal_health IS NOT NULL" , nativeQuery = true)
    BigInteger getTotalPregnantUseAppByClientKey(@Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant_not_app " +
            "FROM obstetric_plan op " +
            "JOIN patient p ON op.patient_id = p.id " +
            "WHERE op.active = true " +
            "AND op.deleted = false " +
            "AND op.client_key = :clientKey " +
            "AND p.login_personal_health IS NULL " +
            "OR TRIM(p.login_personal_health) = ''", nativeQuery = true)
    BigInteger getTotalPregnantDoNotUseAppByClientKey(@Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant " +
            "FROM obstetric_plan op " +
            "WHERE op.active = true " +
            "AND op.deleted = false " +
            "AND op.subscription_id = :subscriptionId", nativeQuery = true)
    BigInteger getTotalPregnantBySubscriptionId(@Param(value = "subscriptionId") long subscriptionId);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant_risk_high " +
            "FROM obstetric_plan op " +
            "WHERE op.active = TRUE " +
            "AND op.pregnancy = 1 " +
            "AND op.deleted = false " +
            "AND op.subscription_id = :subscriptionId" , nativeQuery = true)
    BigInteger getTotalPregnantWithHighRiskBySubscriptionId(@Param(value = "subscriptionId") long subscriptionId);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant_use_app " +
            "FROM obstetric_plan op " +
            "JOIN patient p ON op.patient_id = p.id " +
            "WHERE op.active = TRUE " +
            "AND op.deleted = false " +
            "AND op.subscription_id = :subscriptionId " +
            "AND p.login_personal_health IS NOT NULL" , nativeQuery = true)
    BigInteger getTotalPregnantUseAppBySubscriptionId(@Param(value = "subscriptionId") long subscriptionId);

    @Query(value = "SELECT COUNT(DISTINCT op.id) AS total_pregnant_not_app " +
            "FROM obstetric_plan op " +
            "JOIN patient p ON op.patient_id = p.id " +
            "WHERE op.active = true " +
            "AND op.deleted = false " +
            "AND op.subscription_id = :subscriptionId " +
            "AND p.login_personal_health IS NULL " +
            "OR TRIM(p.login_personal_health) = ''", nativeQuery = true)
    BigInteger getTotalPregnantDoNotUseAppBySubscriptionId(@Param(value = "subscriptionId") long subscriptionId);

    @Query(value = "SELECT MONTH(a.created_date) AS month, COUNT(a.created_date) AS count_per_month " +
            "FROM pregnancy_consultation_info ci " +
            "JOIN attendance a ON ci.attendance_id = a.id " +
            "WHERE ci.attendance_id IS NOT NULL " +
            "AND ci.client_key = :clientKey " +
            "AND a.created_date >= DATE_SUB(NOW(), INTERVAL :interval MONTH) " +
            "AND a.status = 3 " + // concluded attendance
            "GROUP BY YEAR(a.created_date), MONTH(a.created_date) " +
            "ORDER BY YEAR(a.created_date)", nativeQuery = true)
    List<Object[]> getConductedConsultationPerMonthByClientKey(
            @Param(value = "clientKey") String clientKey,
            @Param(value = "interval") Integer interval);

    @Query(value = "SELECT MONTH(a.created_date) AS month, COUNT(a.created_date) AS count_per_month " +
            "FROM pregnancy_consultation_info ci " +
            "JOIN attendance a ON ci.attendance_id = a.id " +
            "WHERE ci.attendance_id IS NOT NULL " +
            "AND a.id_subscription = :subscriptionId " +
            "AND a.created_date >= DATE_SUB(NOW(), INTERVAL :interval MONTH) " +
            "AND a.status = 3 " + // concluded attendance
            "GROUP BY YEAR(a.created_date), MONTH(a.created_date) " +
            "ORDER BY YEAR(a.created_date)", nativeQuery = true)
    List<Object[]> getConductedConsultationPerMonthBySubscriptionId(
            @Param(value = "subscriptionId") long subscriptionId,
            @Param(value = "interval") Integer interval);

    @Query(value = "SELECT MONTH(s.date) AS month, COUNT(s.date) AS count_per_month " +
            "FROM pregnancy_consultation_info ci " +
            "JOIN attendance a ON ci.attendance_id = a.id " +
            "JOIN schedules s ON a.id = s.attendance_id " +
            "JOIN obstetric_plan op ON ci.obstetric_plan_id = op.id " +
            "WHERE ci.attendance_id IS NOT NULL " +
            "AND ci.client_key = :clientKey " +
            "AND s.date <= DATE_ADD(NOW(), INTERVAL :interval MONTH) " +
            "AND s.date >= NOW() " +
            "AND op.active = true " +
            "AND op.deleted = false " +
            "GROUP BY MONTH(s.date) ", nativeQuery = true)
    List<Object[]> getForecastConsultationPerMonthByClientKey(
            @Param(value = "clientKey") String clientKey,
            @Param(value = "interval") Integer interval);

    @Query(value = "SELECT MONTH(s.date) AS month, COUNT(s.date) AS count_per_month " +
            "FROM pregnancy_consultation_info ci " +
            "JOIN attendance a ON ci.attendance_id = a.id " +
            "JOIN schedules s ON a.id = s.attendance_id " +
            "JOIN obstetric_plan op ON ci.obstetric_plan_id = op.id " +
            "WHERE ci.attendance_id IS NOT NULL " +
            "AND a.id_subscription = :subscriptionId " +
            "AND s.date <= DATE_ADD(NOW(), INTERVAL :interval MONTH) " +
            "AND s.date >= NOW() " +
            "AND op.active = true " +
            "AND op.deleted = false " +
            "GROUP BY MONTH(s.date) ", nativeQuery = true)
    List<Object[]> getForecastConsultationPerMonthBySubscriptionId(
            @Param(value = "subscriptionId") long subscriptionId,
            @Param(value = "interval") Integer interval);

    @Query(value = "SELECT MONTH(op.forecast_date) AS months, COUNT(op.forecast_date) AS count_per_month, " +
            "YEAR(op.forecast_date) AS years " +
            "FROM " +
            "( " +
            "SELECT COALESCE(op.dpc, op.dpp_us, op.dpp) AS forecast_date, " +
            "op.active, op.deleted, op.client_key " +
            "FROM obstetric_plan op " +
            ") op " +
            "WHERE op.client_key = :clientKey " +
            "AND forecast_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL :interval MONTH) " +
            "AND op.active = true " +
            "AND op.deleted = false " +
            "GROUP BY YEAR(forecast_date), MONTH(forecast_date) " +
            "ORDER BY YEAR(forecast_date)", nativeQuery = true)
    List<Object[]> getForecastDeliveriesPerMonthByClientKey(
            @Param(value = "clientKey") String clientKey,
            @Param(value = "interval") Integer interval);

    @Query(value = "SELECT MONTH(op.forecast_date) AS months, COUNT(op.forecast_date) AS count_per_month, " +
            "YEAR(op.forecast_date) AS years " +
            "FROM " +
            "( " +
            "SELECT COALESCE(op.dpc, op.dpp_us, op.dpp) AS forecast_date, " +
            "op.active, op.deleted, op.client_key, op.subscription_id " +
            "FROM obstetric_plan op " +
            ") op " +
            "WHERE op.subscription_id = :subscriptionId " +
            "AND forecast_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL :interval MONTH) " +
            "AND op.active = true " +
            "AND op.deleted = false " +
            "GROUP BY YEAR(forecast_date), MONTH(forecast_date) " +
            "ORDER BY YEAR(forecast_date)", nativeQuery = true)
    List<Object[]> getForecastDeliveriesPerMonthBySubscriptionId(
            @Param(value = "subscriptionId") long subscriptionId,
            @Param(value = "interval") Integer interval);

    @Query(value = "SELECT MONTH(NOW()) + n AS month, COUNT(DISTINCT op.id) AS consultations " +
            "FROM obstetric_plan op " +
            "CROSS JOIN (SELECT 0 AS n UNION ALL " +
            "SELECT 1 UNION ALL " +
            "SELECT 2 UNION ALL " +
            "SELECT 3 UNION ALL " +
            "SELECT 4 UNION ALL " +
            "SELECT 5 UNION ALL " +
            "SELECT 6 UNION ALL " +
            "SELECT 7 UNION ALL " +
            "SELECT 8 UNION ALL " +
            "SELECT 9 UNION ALL " +
            "SELECT 10 UNION ALL " +
            "SELECT 11) union_tables " +
            "WHERE op.client_key = :clientKey " +
            "AND op.active = true " +
            "AND op.deleted = false " +
            "AND ABS(MONTH(NOW()) - MONTH(COALESCE(op.dpc, op.dpp_us, op.dpp))) >= (n + 1) " +
            "GROUP BY n", nativeQuery = true)
    List<Object[]> getForecastConsultationsToBeScheduledByClientKey(
            @Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT MONTH(NOW()) + n AS month, COUNT(DISTINCT op.id) AS consultations " +
            "FROM obstetric_plan op " +
            "CROSS JOIN (SELECT 0 AS n UNION ALL " +
            "SELECT 1 UNION ALL " +
            "SELECT 2 UNION ALL " +
            "SELECT 3 UNION ALL " +
            "SELECT 4 UNION ALL " +
            "SELECT 5 UNION ALL " +
            "SELECT 6 UNION ALL " +
            "SELECT 7 UNION ALL " +
            "SELECT 8 UNION ALL " +
            "SELECT 9 UNION ALL " +
            "SELECT 10 UNION ALL " +
            "SELECT 11) union_tables " +
            "WHERE op.subscription_id = :subscriptionId " +
            "AND op.active = true " +
            "AND op.deleted = false " +
            "AND ABS(MONTH(NOW()) - MONTH(COALESCE(op.dpc, op.dpp_us, op.dpp))) >= (n + 1) " +
            "GROUP BY n", nativeQuery = true)
    List<Object[]> getForecastConsultationsToBeScheduledBySubscriptionId(
            @Param(value = "subscriptionId") long subscriptionId);

    @Query(value = "SELECT COALESCE(op.dpc, op.dpp_us, op.dpp) delivery_date\n" +
            "FROM obstetric_plan op \n" +
            "WHERE op.active = TRUE\n" +
            "AND op.deleted = FALSE\n" +
            "AND op.subscription_id = :subscriptionId\n" +
            "ORDER BY delivery_date ASC ", nativeQuery = true)
    List<Date> getForecastConsultationsToBeScheduledBySubscriptionId2(
            @Param(value = "subscriptionId") Long subscriptionId);

    @Query(value = "SELECT COALESCE(op.dpc, op.dpp_us, op.dpp) delivery_date\n" +
            "FROM obstetric_plan op \n" +
            "WHERE op.active = TRUE\n" +
            "AND op.deleted = FALSE\n" +
            "AND op.client_key = :clientKey\n" +
            "ORDER BY delivery_date ASC ", nativeQuery = true)
    List<Date> getForecastConsultationsToBeScheduledByClientKey2(
            @Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT \n" +
            "SUM(CASE WHEN DATEDIFF(NOW(), op.DUM) < :weeks * 7 THEN 1 ELSE 0 END) less_than_in_weeks,\n" +
            "SUM(CASE WHEN DATEDIFF(NOW(), op.DUM) >= :weeks * 7 THEN 1 ELSE 0 END) greater_than_in_weeks\n" +
            "FROM obstetric_plan op\n" +
            "WHERE op.active IS TRUE\n" +
            "AND op.deleted IS FALSE\n" +
            "AND op.client_key = :clientKey", nativeQuery = true)
    List<Object[]> getPregnanciesMoreThanAndGreaterThanInWeeksByClientKey(
            @Param(value = "clientKey") String clientKey,
            @Param(value = "weeks") Integer weeks);

    @Query(value = "SELECT \n" +
            "SUM(CASE WHEN DATEDIFF(NOW(), op.DUM) < :weeks * 7 THEN 1 ELSE 0 END) less_than_in_weeks,\n" +
            "SUM(CASE WHEN DATEDIFF(NOW(), op.DUM) >= :weeks * 7 THEN 1 ELSE 0 END) greater_than_in_weeks\n" +
            "FROM obstetric_plan op\n" +
            "WHERE op.active IS TRUE\n" +
            "AND op.deleted IS FALSE\n" +
            "AND op.subscription_id = :subscriptionId", nativeQuery = true)
    List<Object[]> getPregnanciesMoreThanAndGreaterThanInWeeksBysubscriptionId(
            @Param(value = "subscriptionId") Long subscriptionId,
            @Param(value = "weeks") Integer weeks);


    @Query(value = "SELECT DISTINCT \n" +
            "p.id, " +
            "p.pregnancy, \n" +
            "op.name, \n" +
            "op.birth_date AS birthDate, \n" +
            "p.last_attendence_id AS lastAttendance, \n" +
            "p.birth_appointment_id AS birthAppointment, \n" +
            "sp.cell_phone AS cellPhone, \n" +
            "sp.email, \n" +
            "p.subscription_id AS subscriptionId, \n" +
            "p.client_key AS clientKey, \n" +
            "p.active, \n" +
            "p.DUM AS dum, \n" +
            "p.DPP_US AS dppus, \n" +
            "p.DPC AS dpc, \n" +
            "p.patient_id AS patientId, \n" +
            "p.pregnancies, \n" +
            "p.deliveries, \n" +
            "p.abortions, \n" +
            "op.picture, " +
            "e.id AS employeeId, " +
            "CONCAT(u.first_name, ' ', u.last_name) AS fullName," +
            "att.created_date AS lastAttendanceDate, " +
            "sc.date AS nextConsultationDate " +
            "FROM obstetric_plan p " +
            "INNER JOIN patient op ON op.id = p.patient_id " +
            "INNER JOIN attendance att ON p.last_attendence_id = att.id " +
            "INNER JOIN employee e ON att.id_employee = e.id " +
            "INNER JOIN user u ON att.id_employee = u.id " +
            "INNER JOIN subscription_patient sp ON att.id_patient = sp.id " +
            "INNER JOIN schedules sc ON sc.patient_id = att.id_patient " +
            "WHERE (e.id = :employeeId OR :employeeId IS NULL) " +
            "AND p.client_key = :clientKey " +
            "AND p.active IS TRUE " +
            "AND (p.pregnancy = :pregnancy OR :pregnancy IS NULL)  " +
            "AND (:initialWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) >= :initialWeek) " +
            "AND (:finalWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) <= :finalWeek) " +
            "AND (:lastAttendanceDate IS NULL OR att.created_date >= :lastAttendanceDate) " +
            "AND (:endAttendanceDate IS NULL OR att.created_date <= :endAttendanceDate) " +
            "AND (p.DPC = :dpc OR :dpc IS NULL OR (p.DPC BETWEEN :startDPC AND :endDPC)) -- #pageable\n",
//            //"WHERE sc.date = :nextConsultationDate > CURDATE()
            countQuery = "SELECT count(*) FROM obstetric_plan p " +
                    "INNER JOIN patient op ON op.id = p.patient_id\n" +
                    "INNER JOIN attendance att ON p.last_attendence_id = att.id\n" +
                    "INNER JOIN employee e ON att.id_employee = e.id \n" +
                    "INNER JOIN user u ON att.id_employee = u.id \n" +
                    "INNER JOIN subscription_patient sp ON att.id_patient = sp.id \n" +
                    "INNER JOIN schedules sc ON sc.patient_id = att.id_patient\n" +
                    "WHERE (e.id = :employeeId OR :employeeId IS NULL) " +
                    "AND p.client_key = :clientKey " +
                    "AND p.active IS TRUE " +
                    "AND (p.pregnancy = :pregnancy OR :pregnancy IS NULL) " +
                    "AND (:initialWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) >= :initialWeek) " +
                    "AND (:finalWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) <= :finalWeek) " +
                    "AND (:lastAttendanceDate IS NULL OR att.created_date >= :lastAttendanceDate) " +
                    "AND (:endAttendanceDate IS NULL OR att.created_date <= :endAttendanceDate) " +
                    "AND (p.DPC = :dpc OR :dpc IS NULL OR (p.DPC BETWEEN :startDPC AND :endDPC)) ",
                    //"WHERE sc.date = :nextConsultationDate > CURDATE() \n"
            nativeQuery = true)
    Page<Object[]> listAllClientKey(Pageable pageable, @Param(value = "employeeId") BigInteger employeeId,
                           @Param(value = "clientKey") String clientKey,
                           @Param(value = "pregnancy") Integer pregnancy,
                           @Param(value = "initialWeek") Integer initialWeek,
                           @Param(value = "finalWeek") Integer finalWeek,
                           @Param(value = "dpc") String dpc,
                           @Param(value = "startDPC") String startDPC,
                           @Param(value = "endDPC") String endDPC,
                           @Param(value = "lastAttendanceDate") String lastAttendanceDate,
                           @Param(value = "endAttendanceDate") String endAttendanceDate);

    @Query(value = "SELECT DISTINCT \n" +
            "p.id, " +
            "p.pregnancy, \n" +
            "op.name, \n" +
            "op.birth_date AS birthDate, \n" +
            "p.last_attendence_id AS lastAttendance, \n" +
            "p.birth_appointment_id AS birthAppointment, \n" +
            "sp.cell_phone AS cellPhone, \n" +
            "sp.email, \n" +
            "p.subscription_id AS subscriptionId, \n" +
            "p.client_key AS clientKey, \n" +
            "p.active, \n" +
            "p.DUM AS dum, \n" +
            "p.DPP_US AS dppus, \n" +
            "p.DPC AS dpc, \n" +
            "p.patient_id AS patientId, \n" +
            "p.pregnancies, \n" +
            "p.deliveries, \n" +
            "p.abortions, \n" +
            "op.picture, " +
            "e.id AS employeeId, " +
            "CONCAT(u.first_name, ' ', u.last_name) AS fullName," +
            "att.created_date AS lastAttendanceDate, " +
            "sc.date AS nextConsultationDate " +
            "FROM obstetric_plan p " +
            "INNER JOIN patient op ON op.id = p.patient_id " +
            "INNER JOIN attendance att ON p.last_attendence_id = att.id " +
            "INNER JOIN employee e ON att.id_employee = e.id " +
            "INNER JOIN user u ON att.id_employee = u.id " +
            "INNER JOIN subscription_patient sp ON att.id_patient = sp.id " +
            "INNER JOIN schedules sc ON sc.patient_id = att.id_patient " +
            "WHERE (e.id = :employeeId OR :employeeId IS NULL) " +
            "AND p.subscription_id = :subscriptionId " +
            "AND p.active IS TRUE " +
            "AND (p.pregnancy = :pregnancy OR :pregnancy IS NULL)  " +
            "AND (:initialWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) >= :initialWeek) " +
            "AND (:finalWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) <= :finalWeek) " +
            "AND (:lastAttendanceDate IS NULL OR att.created_date >= :lastAttendanceDate) " +
            "AND (:endAttendanceDate IS NULL OR att.created_date <= :endAttendanceDate) " +
            "AND (p.DPC = :dpc OR :dpc IS NULL OR (p.DPC BETWEEN :startDPC AND :endDPC))  -- #pageable\n",
//            //"WHERE sc.date = :nextConsultationDate > CURDATE()
            countQuery = "SELECT count(*) FROM obstetric_plan p " +
                    "INNER JOIN patient op ON op.id = p.patient_id\n" +
                    "INNER JOIN attendance att ON p.last_attendence_id = att.id\n" +
                    "INNER JOIN employee e ON att.id_employee = e.id \n" +
                    "INNER JOIN user u ON att.id_employee = u.id \n" +
                    "INNER JOIN subscription_patient sp ON att.id_patient = sp.id \n" +
                    "INNER JOIN schedules sc ON sc.patient_id = att.id_patient\n" +
                    "WHERE (e.id = :employeeId OR :employeeId IS NULL) " +
                    "AND p.subscription_id = :subscriptionId " +
                    "AND p.active IS TRUE " +
                    "AND (p.pregnancy = :pregnancy OR :pregnancy IS NULL) " +
                    "AND (:initialWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) >= :initialWeek) " +
                    "AND (:finalWeek IS NULL OR TIMESTAMPDIFF(WEEK, p.DUM, CURDATE()) <= :finalWeek) " +
                    "AND (:lastAttendanceDate IS NULL OR att.created_date >= :lastAttendanceDate) " +
                    "AND (:endAttendanceDate IS NULL OR att.created_date <= :endAttendanceDate) " +
                    "AND (p.DPC = :dpc OR :dpc IS NULL OR (p.DPC BETWEEN :startDPC AND :endDPC)) " ,
            //"WHERE sc.date = :nextConsultationDate > CURDATE() \n"
            nativeQuery = true)
    Page<Object[]> listAllSubscriptionId(Pageable pageable, @Param(value = "employeeId") BigInteger employeeId,
                                    @Param(value = "subscriptionId") Long subscriptionId,
                                    @Param(value = "pregnancy") Integer pregnancy,
                                    @Param(value = "initialWeek") Integer initialWeek,
                                    @Param(value = "finalWeek") Integer finalWeek,
                                    @Param(value = "dpc") String dpc,
                                    @Param(value = "startDPC") String startDPC,
                                    @Param(value = "endDPC") String endDPC,
                                    @Param(value = "lastAttendanceDate") String lastAttendanceDate,
                                    @Param(value = "endAttendanceDate") String endAttendanceDate);

    @Query(value = "  SELECT DISTINCT\n" +
            "  op.id op_id, op.DUM, FLOOR(DATEDIFF(NOW(), op.DUM) / 7) gestational_age_weeks,COALESCE(op.dpp_us, op.dpp) DPP_US, op.DPC,\n" +
            "  op.pregnancy pregnancy_risk, p.name patient_name, sp.email patient_email, sp.cell_phone patient_phone,\n" +
            "  TIMESTAMPDIFF(YEAR, p.birth_date, CURDATE()) patient_age, p.picture patient_picture,\n" +
            "  CONCAT('G', COALESCE(op.pregnancies, '0'), 'P', COALESCE(op.deliveries, '0'), 'A', COALESCE(op.abortions, '0')) gpa, DATE(att.created_date) last_attendance_date,\n" +
            "  cl.name last_att_clinic_name,\n" +
            "  (SELECT sc.date FROM schedules sc WHERE sc.patient_id = op.patient_id AND sc.date > CURDATE() LIMIT 1) next_consultation_date,\n" +
            "  sc1.date birth_appointment_date, (op.birth_appointment_id IS NOT NULL) scheduled_birth,\n" +
            "  (SELECT u.first_name FROM user u WHERE u.login = e.login) birth_appointment_employee_full_name,\n" +
            "  e.id birth_appointment_employee_id, (p.login_personal_health IS NOT NULL) have_app, p.id patient_id\n" +
            "FROM obstetric_plan op\n" +
            "INNER JOIN patient p ON op.patient_id = p.id\n" +
            "INNER JOIN subscription_patient sp ON sp.id_patient = p.id\n" +
            "INNER JOIN attendance att ON op.last_attendence_id = att.id\n" +
            "INNER JOIN clinic cl ON att.id_clinic = cl.id\n" +
            "LEFT JOIN schedules sc1 ON sc1.id = op.birth_appointment_id\n" +
            "LEFT JOIN employee e ON e.id = sc1.employee_id\n" +
            "WHERE\n" +
            "op.active IS TRUE AND op.deleted IS FALSE AND op.client_key = :clientKey\n" +
            "AND (e.id = :employeeId OR :employeeId IS NULL)\n" +
            "AND (op.pregnancy = :pregnancy OR :pregnancy IS NULL)\n" +
            "AND ((:initDPC IS NOT NULL AND :endDPC IS NOT NULL AND op.DPC BETWEEN :initDPC AND :endDPC) OR (:initDPC IS NULL AND :endDPC IS NULL))\n" +
            "HAVING\n" +
            "(scheduled_birth = :scheduledBirth OR :scheduledBirth IS NULL)\n" +
            "AND (have_app = :haveApp OR :haveApp IS NULL)\n" +
            "AND ((gestational_age_weeks BETWEEN :initialWeek AND :finalWeek) OR (:initialWeek IS NULL AND :finalWeek IS NULL))\n" +
            "AND ((:initLastAttendanceDate IS NOT NULL AND :endLastAttendanceDate IS NOT NULL AND last_attendance_date BETWEEN :initLastAttendanceDate AND :endLastAttendanceDate) OR (:initLastAttendanceDate IS NULL AND :endLastAttendanceDate IS NULL)) \n" +
            "ORDER BY gestational_age_weeks DESC \n" +
            "\n-- #pageable\n",

            countQuery = "SELECT COUNT(*) FROM (\n" +
                    "  SELECT DISTINCT\n" +
                    "  op.id op_id, op.DUM, FLOOR(DATEDIFF(NOW(), op.DUM) / 7) gestational_age_weeks,COALESCE(op.dpp_us, op.dpp) DPP_US, op.DPC,\n" +
                    "  op.pregnancy pregnancy_risk, p.name patient_name, sp.email patient_email, sp.cell_phone patient_phone,\n" +
                    "  TIMESTAMPDIFF(YEAR, p.birth_date, CURDATE()) patient_age, p.picture patient_picture,\n" +
                    "  CONCAT('G', op.pregnancies, 'P', op.deliveries, 'A', op.abortions) gpa, DATE(att.created_date) last_attendance_date,\n" +
                    "  cl.name last_att_clinic_name,\n" +
                    "  (SELECT sc.date FROM schedules sc WHERE sc.patient_id = op.patient_id AND sc.date > CURDATE() LIMIT 1) next_consultation_date,\n" +
                    "  sc1.date birth_appointment_date, (op.birth_appointment_id IS NOT NULL) scheduled_birth,\n" +
                    "  (SELECT u.first_name FROM user u WHERE u.login = e.login) birth_appointment_employee_full_name,\n" +
                    "  e.id birth_appointment_employee_id, (p.login_personal_health IS NOT NULL) have_app, p.id patient_id\n" +
                    "  FROM obstetric_plan op\n" +
                    "  INNER JOIN patient p ON op.patient_id = p.id\n" +
                    "  INNER JOIN subscription_patient sp ON sp.id_patient = p.id\n" +
                    "  INNER JOIN attendance att ON op.last_attendence_id = att.id\n" +
                    "  INNER JOIN clinic cl ON att.id_clinic = cl.id\n" +
                    "  LEFT JOIN schedules sc1 ON sc1.id = op.birth_appointment_id\n" +
                    "  LEFT JOIN employee e ON e.id = sc1.employee_id\n" +
                    "WHERE\n" +
                    "op.active IS TRUE AND op.deleted IS FALSE AND op.client_key = :clientKey\n" +
                    "AND (e.id = :employeeId OR :employeeId IS NULL)\n" +
                    "AND (op.pregnancy = :pregnancy OR :pregnancy IS NULL)\n" +
                    "AND ((:initDPC IS NOT NULL AND :endDPC IS NOT NULL AND op.DPC BETWEEN :initDPC AND :endDPC) OR (:initDPC IS NULL AND :endDPC IS NULL))\n" +
                    "HAVING\n" +
                    "(scheduled_birth = :scheduledBirth OR :scheduledBirth IS NULL)\n" +
                    "AND (have_app = :haveApp OR :haveApp IS NULL)\n" +
                    "AND ((gestational_age_weeks BETWEEN :initialWeek AND :finalWeek) OR (:initialWeek IS NULL AND :finalWeek IS NULL))\n" +
                    "AND ((:initLastAttendanceDate IS NOT NULL AND :endLastAttendanceDate IS NOT NULL AND last_attendance_date BETWEEN :initLastAttendanceDate AND :endLastAttendanceDate) OR (:initLastAttendanceDate IS NULL AND :endLastAttendanceDate IS NULL))\n" +
                    ") AS subquery \n" ,

            nativeQuery = true)
    Page<Object[]> listAllClientKeyV2(Pageable pageable,
                                      @Param(value = "employeeId") BigInteger employeeId,
                                      @Param(value = "pregnancy") Integer pregnancy,
                                      @Param(value = "initialWeek") Integer initialWeek,
                                      @Param(value = "finalWeek") Integer finalWeek,
                                      @Param(value = "scheduledBirth") Boolean scheduledBirth,
                                      @Param(value = "initLastAttendanceDate") String lastAttendanceDate,
                                      @Param(value = "endLastAttendanceDate") String endAttendanceDate,
                                      @Param(value = "initDPC") String startDPC,
                                      @Param(value = "endDPC") String endDPC,
                                      @Param(value = "haveApp") Boolean haveApp,
                                      @Param(value = "clientKey") String clientKey);

    @Query(value = "SELECT DISTINCT\n" +
            "  op.id op_id, op.DUM, FLOOR(DATEDIFF(NOW(), op.DUM) / 7) gestational_age_weeks,COALESCE(op.dpp_us, op.dpp) DPP_US, op.DPC,\n" +
            "  op.pregnancy pregnancy_risk, p.name patient_name, sp.email patient_email, sp.cell_phone patient_phone,\n" +
            "  TIMESTAMPDIFF(YEAR, p.birth_date, CURDATE()) patient_age, p.picture patient_picture,\n" +
            "  CONCAT('G', COALESCE(op.pregnancies, '0'), 'P', COALESCE(op.deliveries, '0'), 'A', COALESCE(op.abortions, '0')) gpa, DATE(att.created_date) last_attendance_date,\n" +
            "  cl.name last_att_clinic_name,\n" +
            "  (SELECT sc.date FROM schedules sc WHERE sc.patient_id = op.patient_id AND sc.date > CURDATE() LIMIT 1) next_consultation_date,\n" +
            "  sc1.date birth_appointment_date, (op.birth_appointment_id IS NOT NULL) scheduled_birth,\n" +
            "  (SELECT u.first_name FROM user u WHERE u.login = e.login) birth_appointment_employee_full_name,\n" +
            "  e.id birth_appointment_employee_id, (p.login_personal_health IS NOT NULL) have_app, p.id patient_id\n" +
            "  FROM obstetric_plan op\n" +
            "  INNER JOIN patient p ON op.patient_id = p.id\n" +
            "  INNER JOIN subscription_patient sp ON sp.id_patient = p.id\n" +
            "  INNER JOIN attendance att ON op.last_attendence_id = att.id\n" +
            "  INNER JOIN clinic cl ON att.id_clinic = cl.id\n" +
            "  LEFT JOIN schedules sc1 ON sc1.id = op.birth_appointment_id\n" +
            "  LEFT JOIN employee e ON e.id = sc1.employee_id\n" +
            "WHERE\n" +
            "op.active IS TRUE AND op.deleted IS FALSE AND op.subscription_id = :subscriptionId\n" +
            "AND (e.id = :employeeId OR :employeeId IS NULL)\n" +
            "AND (op.pregnancy = :pregnancy OR :pregnancy IS NULL)\n" +
            "AND ((:initDPC IS NOT NULL AND :endDPC IS NOT NULL AND op.DPC BETWEEN :initDPC AND :endDPC) OR (:initDPC IS NULL AND :endDPC IS NULL))\n" +
            "HAVING\n" +
            "(scheduled_birth = :scheduledBirth OR :scheduledBirth IS NULL)\n" +
            "AND (have_app = :haveApp OR :haveApp IS NULL)\n" +
            "AND ((gestational_age_weeks BETWEEN :initialWeek AND :finalWeek) OR (:initialWeek IS NULL AND :finalWeek IS NULL))\n" +
            "AND ((:initLastAttendanceDate IS NOT NULL AND :endLastAttendanceDate IS NOT NULL AND last_attendance_date BETWEEN :initLastAttendanceDate AND :endLastAttendanceDate) OR (:initLastAttendanceDate IS NULL AND :endLastAttendanceDate IS NULL))\n" +
            "ORDER BY gestational_age_weeks DESC \n" +
            "\n-- #pageable\n",

            countQuery = "SELECT COUNT(*) FROM (\n" +
                    "  SELECT DISTINCT\n" +
                    "  op.id op_id, op.DUM, FLOOR(DATEDIFF(NOW(), op.DUM) / 7) gestational_age_weeks,COALESCE(op.dpp_us, op.dpp) DPP_US, op.DPC,\n" +
                    "  op.pregnancy pregnancy_risk, p.name patient_name, sp.email patient_email, sp.cell_phone patient_phone,\n" +
                    "  TIMESTAMPDIFF(YEAR, p.birth_date, CURDATE()) patient_age, p.picture patient_picture,\n" +
                    "  CONCAT('G', op.pregnancies, 'P', op.deliveries, 'A', op.abortions) gpa, DATE(att.created_date) last_attendance_date,\n" +
                    "  cl.name last_att_clinic_name,\n" +
                    "  (SELECT sc.date FROM schedules sc WHERE sc.patient_id = op.patient_id AND sc.date > CURDATE() LIMIT 1) next_consultation_date,\n" +
                    "  sc1.date birth_appointment_date, (op.birth_appointment_id IS NOT NULL) scheduled_birth,\n" +
                    "  (SELECT u.first_name FROM user u WHERE u.login = e.login) birth_appointment_employee_full_name,\n" +
                    "  e.id birth_appointment_employee_id, (p.login_personal_health IS NOT NULL) have_app, p.id patient_id\n" +
                    "  FROM obstetric_plan op\n" +
                    "  INNER JOIN patient p ON op.patient_id = p.id\n" +
                    "  INNER JOIN subscription_patient sp ON sp.id_patient = p.id\n" +
                    "  INNER JOIN attendance att ON op.last_attendence_id = att.id\n" +
                    "  INNER JOIN clinic cl ON att.id_clinic = cl.id\n" +
                    "  LEFT JOIN schedules sc1 ON sc1.id = op.birth_appointment_id\n" +
                    "  LEFT JOIN employee e ON e.id = sc1.employee_id\n" +
                    "WHERE\n" +
                    "op.active IS TRUE AND op.deleted IS FALSE AND op.subscription_id = :subscriptionId\n" +
                    "AND (e.id = :employeeId OR :employeeId IS NULL)\n" +
                    "AND (op.pregnancy = :pregnancy OR :pregnancy IS NULL)\n" +
                    "AND ((:initDPC IS NOT NULL AND :endDPC IS NOT NULL AND op.DPC BETWEEN :initDPC AND :endDPC) OR (:initDPC IS NULL AND :endDPC IS NULL))\n" +
                    "HAVING\n" +
                    "(scheduled_birth = :scheduledBirth OR :scheduledBirth IS NULL)\n" +
                    "AND (have_app = :haveApp OR :haveApp IS NULL)\n" +
                    "AND ((gestational_age_weeks BETWEEN :initialWeek AND :finalWeek) OR (:initialWeek IS NULL AND :finalWeek IS NULL))\n" +
                    "AND ((:initLastAttendanceDate IS NOT NULL AND :endLastAttendanceDate IS NOT NULL AND last_attendance_date BETWEEN :initLastAttendanceDate AND :endLastAttendanceDate) OR (:initLastAttendanceDate IS NULL AND :endLastAttendanceDate IS NULL))\n" +
                    ") AS subquery \n" ,

            nativeQuery = true)
    Page<Object[]> listAllBySubscriptionIdV2(Pageable pageable,
                                             @Param(value = "employeeId") BigInteger employeeId,
                                             @Param(value = "pregnancy") Integer pregnancy,
                                             @Param(value = "initialWeek") Integer initialWeek,
                                             @Param(value = "finalWeek") Integer finalWeek,
                                             @Param(value = "scheduledBirth") Boolean scheduledBirth,
                                             @Param(value = "initLastAttendanceDate") String lastAttendanceDate,
                                             @Param(value = "endLastAttendanceDate") String endAttendanceDate,
                                             @Param(value = "initDPC") String startDPC,
                                             @Param(value = "endDPC") String endDPC,
                                             @Param(value = "haveApp") Boolean haveApp,
                                             @Param(value = "subscriptionId") Long subscriptionId);


}