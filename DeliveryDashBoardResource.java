package br.com.mv.clinic.rest.obstetric_plan.delivery_dashboard;

import br.com.mv.clinic.constants.AppConstants;
import br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard.ChartDataDTO;
import br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard.DashboardDTOV2;
import br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard.DashboardFilterDTOV2;
import br.com.mv.clinic.dto.obstetric_plan_delivery_dashboard.QuantityIndicatorsDTO;
import br.com.mv.clinic.service.obstetric_plan_delivery_dashboard.DeliveryDashBoardService;
import br.com.mv.clinic.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.PathParam;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping(value = AppConstants.PATH + AppConstants.V1 + "/obstetric-plans/delivery-dashboard")
public class DeliveryDashBoardResource {

    @Autowired
    private final DeliveryDashBoardService deliveryDashBoardService;

    public DeliveryDashBoardResource(DeliveryDashBoardService deliveryDashBoardService) {
        this.deliveryDashBoardService = deliveryDashBoardService;
    }

    @Timed
    @RequestMapping(value = "/pregnant-totals",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuantityIndicatorsDTO> getPregnantTotals() throws IOException {

        QuantityIndicatorsDTO quantityIndicatorsDTO = deliveryDashBoardService.getBirthingPanelPregnantTotals();

        return new ResponseEntity<>(quantityIndicatorsDTO, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/conducted-consultations-per-month",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChartDataDTO> getConductedConsultationPerMonth(
            @PathParam("interval") Integer interval) throws IOException {

        ChartDataDTO chartData = deliveryDashBoardService
                .getConductedConsultationPerMonthByClientKey();

        return new ResponseEntity<>(chartData, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/forecast-consultations-per-month",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChartDataDTO> getForecastConsultationPerMonthByClientKey(
            @PathParam("interval") Integer interval) throws IOException {

        ChartDataDTO chartData = deliveryDashBoardService
                .getForecastConsultationPerMonthByClientKey();

        return new ResponseEntity<>(chartData, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/forecast-deliveries-per-month",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChartDataDTO> getForecastDeliveriesPerMonthByClientKey(
            @PathParam("interval") Integer interval) throws IOException {

        ChartDataDTO chartData = deliveryDashBoardService
                .getForecastDeliveriesPerMonthByClientKey();

        return new ResponseEntity<>(chartData, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/pregnant-list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DashboardDTOV2>> findAll(Pageable pageable, DashboardFilterDTOV2 filter)
            throws IOException, ParseException, IllegalAccessException {

        Page<DashboardDTOV2> dtoList = this.deliveryDashBoardService.findAllV2(pageable, filter);
        return new ResponseEntity<>(dtoList.getContent(), HeaderUtil.createPaginationHeader(dtoList), HttpStatus.OK);
    }
}