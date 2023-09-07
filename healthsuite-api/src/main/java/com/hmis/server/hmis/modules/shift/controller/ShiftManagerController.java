package com.hmis.server.hmis.modules.shift.controller;

import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.shift.dto.*;
import com.hmis.server.hmis.modules.shift.service.CashierShiftServiceImpl;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(HmisConstant.API_PREFIX + "/manage-shift")
public class ShiftManagerController {
    private final CashierShiftServiceImpl cashierShiftService;

    public ShiftManagerController(CashierShiftServiceImpl cashierShiftService) {
        this.cashierShiftService = cashierShiftService;
    }

    @GetMapping(value = "/close-shift/{shiftNumber}/{userId}")
    public ResponseEntity<Boolean> closeCashierShift(@PathVariable String shiftNumber, @PathVariable String userId) {
        return this.cashierShiftService.closeShiftByCashier(shiftNumber, Long.parseLong(userId));
    }

    @GetMapping(value = "/look-up-code/{userId}")
    public ResponseEntity<ShiftCodeSearchDto> lookUpCode(@PathVariable String userId) {
        return this.cashierShiftService.findCashierActiveCode(Long.valueOf(userId));
    }

    @GetMapping(value = "/search-shift-by-code")
    public List<ShiftCodeSearchDto> searchCashierShiftNumber(
            @RequestParam(value = "search") String search,
            @RequestParam(value = "showClosedShift") boolean showClosedShift
    ) {
        return this.cashierShiftService.searchCashierShiftTypeAhead(search, showClosedShift);
    }

    @GetMapping(value = "/search-shift-wrap-by-id/{shiftId}")
    public CashierShiftWrapperDto getCashierShitWrapperRecord(@PathVariable String shiftId) {
        return this.cashierShiftService.findShiftWrapperByShiftId(Long.parseLong(shiftId));
    }

    @GetMapping(value = "/search-shift-by-id/{shiftId}")
    public CashierShiftDto getCashierShitRecord(@PathVariable String shiftId) {
        return this.cashierShiftService.findCashierShiftByShiftId(Long.parseLong(shiftId));
    }

    @PostMapping(value = "/search-shift-by-date-range")
    public CashierShiftWrapperDto getCashierShitRecordByDateRange(@RequestBody CashierShiftSearchDto dto) {
        return this.cashierShiftService.findShiftWrapperByDateRange(dto);
    }

    @PostMapping(value = "/close-multiple-shifts")
    public ResponseEntity<Boolean> closeMultipleShiftRecords(@RequestBody CloseShiftDto dto) {
        return this.cashierShiftService.closeMultipleShiftRecord(dto);
    }

    @PostMapping(value = "/download-cashier-shift-pdf")
    public ResponseEntity< byte[] > fetchCashierShiftReport(
            HttpServletResponse response,
            @RequestBody CashierShiftSearchDto dto) throws IOException, JRException {
        try {
            byte[] bytes = this.cashierShiftService.generateCashierShiftReport(dto);
            return ResponseEntity.ok(bytes);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/search-shift-by-date-range-and-user")
    public List<CashierShiftDto> getCashierShitRecordByDateRangeAndUser(@RequestBody CashierShiftSearchDto dto) {
        return this.cashierShiftService.findShiftByDateRangeAndCashierRequired(dto);
    }

    @PostMapping(value = "/search-shift-by-date-range-and-user-not-required")
    public List<CashierShiftDto> getCashierShitRecordByDateRangeOnly(@RequestBody CashierShiftSearchDto dto) {
        return this.cashierShiftService.findShiftByDateRangeAndCashierNotRequired(dto);
    }

    @PostMapping(value = "/search-shift-by-date-range-for-reception")
    public List<CashierShiftDto> getCashierShitRecordByDateForReception(@RequestBody CashierShiftSearchDto dto) {
        return this.cashierShiftService.findShiftByDateRangeForReception(dto);
    }

    @PostMapping(value = "/get-all-shift-per-day-report-pdf")
    public ResponseEntity<byte[]> getAllCashierShitPerDayReport(@RequestBody CashierShiftSearchDto dto) {
        return this.cashierShiftService.getAllShiftPerDayReport(dto);
    }

    @PostMapping(value = "acknowledge-cashier-shift-fund-pdf")
    public ResponseEntity<byte[]> acknowledgeCashierShiftFund(@RequestBody CashierFundReceptionDto dto){
        return this.cashierShiftService.acknowledgeCashierShiftFund(dto);
    }

    @PostMapping(value = "compile-cashier-shifts-pdf")
    public ResponseEntity<byte[]> compileCashierShifts(@RequestBody CashierCompiledShiftDto dto){
        return this.cashierShiftService.compileCashierShifts(dto);
    }

}
