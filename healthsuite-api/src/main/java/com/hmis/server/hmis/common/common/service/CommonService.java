package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.ServiceUsageEnum;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.reports.dto.HmisReportFileEnum;
import com.hmis.server.hmis.modules.settings.dto.GlobalSettingsDto;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.jasperreports.engine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    GlobalSettingsImpl globalSettingsService;

    @Value(value = "${hmis.frontend.logo}")
    private String logoPath;

    public String getAuthenticatedUser() {
        Optional<org.springframework.security.core.userdetails.User> user = this.getCurrentUser();
        return user.isPresent() ? user.get().getUsername() : "";
    }

    public String getLoggerMessage(String message, String type, Object... object) {
        switch (type) {
            case "attempt":
                return getAuthenticatedUser() + HmisConstant.LOGGER_IS_ATTEMPTING + message;
            case "failed":
                return getAuthenticatedUser() + message + HmisConstant.LOGGER_FAILED;
            case "passed":
                return getAuthenticatedUser() + message + HmisConstant.LOGGER_PASSED;
            case "duplicate":
                return message;
            default:
                return getAuthenticatedUser() + message;
        }
    }

    public void setLOGGER(String type, String message, Optional<String> constantMessage) {
        switch (type) {
            case "info":
                LOGGER.info(getLoggerMessage(message, HmisConstant.LOGGER_TYPE_ATTEMPT));
            case "debug":
                LOGGER.debug(getLoggerMessage(message, constantMessage.orElse("")));
        }
    }

    public String generateDataCode(GenerateCodeDto codeDto) {
        String codePrefix;
        GlobalSettingsDto settingsDto = globalSettingsService
                .findByKey(new GlobalSettingsDto(codeDto.getGlobalSettingKey()));
        if (settingsDto != null) {
            codePrefix = settingsDto.getValue().orElse(codeDto.getDefaultPrefix());
        } else {
            codePrefix = codeDto.getDefaultPrefix();
        }

        int lastGeneratedCode;
        if (codeDto.getLastGeneratedCode().isPresent()
                && (codeDto.getLastGeneratedCode().get().length() > codePrefix.length())) {
            lastGeneratedCode = Integer.parseInt(codeDto.getLastGeneratedCode().get().replaceAll("[^0-9]", ""));
        } else {
            lastGeneratedCode = HmisCodeDefaults.APP_CODE_START_NUMBER_DEFAULT;
        }

        return codePrefix + new AtomicInteger(lastGeneratedCode).incrementAndGet();
    }

    public ServiceUsageEnum getServiceUsageValue(String usageText) {
        if (usageText.contains(" ")) {
            usageText = usageText.replaceAll("\\s+", "_");
            return ServiceUsageEnum.valueOf(usageText);
        } else {
            return ServiceUsageEnum.valueOf(usageText);
        }
    }

    public InputStream getLogoAsStream() {
        try {
            return new ClassPathResource(logoPath).getInputStream();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String formatAmount(Double amount) {
        return String.format("%,.2f", amount);
    }

    public byte[] generatePDFBytes(HashMap<String, Object> parameters, String reportPath) {
        byte[] invoice = null;
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(new FileInputStream(reportPath));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                    parameters, new JREmptyDataSource());
            invoice = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return invoice;
    }

    public byte[] generatePDFBytes(HashMap<String, Object> parameters, InputStream reportPath) {
        byte[] invoice = null;
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportPath);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
                    parameters, new JREmptyDataSource());
            invoice = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return invoice;
    }

    public static byte[] generatePDFBytes(HashMap<String, Object> parameters, InputStream reportFileAsInputStream,
            JRDataSource dataSource) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(reportFileAsInputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return outputStream.toByteArray();
        } catch (JRException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public JasperReport compileReportDesign(HmisUtilService utilService, HmisReportFileEnum fileEnum) {
        try {
            // return JasperCompileManager.compileReport(new
            // FileInputStream(fileEnum.absoluteFilePath(utilService)));
            return JasperCompileManager.compileReport(fileEnum.absoluteFilePath(utilService));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // TODO:: String cannot be cast to class
    // org.springframework.security.core.userdetails.User -- Rewrite block
    private Optional<User> getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return Optional.of(principal);
    }
}
