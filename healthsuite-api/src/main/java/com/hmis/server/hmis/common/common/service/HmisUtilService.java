package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.dto.DateDto;
import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.emr.model.PatientInsuranceDetail;
import com.hmis.server.hmis.modules.reports.dto.PageDto;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class HmisUtilService {

	public static final String INVALID_FILE_PATH = "INVALID REPORT FILE PATH SUPPLIED";

	@Autowired
	private Environment environment;

	public boolean isDevOrProdProfile() {
		String[] profiles = this.environment.getActiveProfiles();
		return profiles.length == 1 && profiles[0].contains("dev") || profiles[0].contains("prod");
	}

	/*
	 * public String getAbsoluteFilePath(String path) {
	 * try {
	 * return ResourceUtils.getFile(
	 * ResourceUtils.CLASSPATH_URL_PREFIX + path).getAbsolutePath();
	 * 
	 * } catch (Exception e) {
	 * log.info("Failed to get file path: " + path);
	 * log.error(e.getMessage(), e);
	 * e.printStackTrace();
	 * return null;
	 * }
	 * }
	 * 
	 */

	public InputStream getAbsoluteFilePath(String path) {
		try {
			InputStream inputStream = getFileFromClassPathAsInputStream(path);
			return inputStream;
		} catch (Exception e) {
			log.info("Failed to get file path: " + path);
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
	}

	public String convertInputStreamToString(InputStream inputStream) {
		try {
			StringWriter writer = new StringWriter();
			String encoding = StandardCharsets.UTF_8.name();
			IOUtils.copy(inputStream, writer, encoding);
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static InputStream getFileFromClassPathAsInputStream(String path) {
		try {
			return new ClassPathResource(path).getInputStream();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(INVALID_FILE_PATH + " " + e.getMessage(), e);
		}
	}

	public Pageable transformToPage(PageDto dto) {
		return PageRequest.of(dto.getPageNumber() - 1, dto.getPageSize());
	}

	public PageDto transformToPageDto(Page page) {
		return new PageDto(
				page.getPageable().getPageNumber() + 1,
				page.getPageable().getPageSize(),
				page.getTotalPages(),
				page.getTotalElements());
	}

	public LocalDate transformToLocalDate(DateDto dateDto) {
		return LocalDate.of(dateDto.getYear(), dateDto.getMonth(), dateDto.getDay());
	}

	public DateDto transformToDateDto(LocalDate date) {
		DateDto dateDto = new DateDto();
		this.setDateOnly(dateDto, date);
		return dateDto;
	}

	public DateDto transformDateAndTime(LocalDate date, LocalTime time) {
		DateDto dateDto = new DateDto();
		this.setDateOnly(dateDto, date);
		this.setTimeOnly(dateDto, time);
		return dateDto;
	}

	public DateDto transformToDateDto(LocalDateTime date) {
		DateDto dateDto = new DateDto();
		dateDto.setYear(date.getYear());
		dateDto.setMonth(date.getMonthValue());
		dateDto.setDay(date.getDayOfMonth());
		dateDto.setHour(date.getHour());
		dateDto.setMin(date.getMinute());
		return dateDto;
	}

	public LocalTime transformToLocalTime(DateDto dateDto) {
		return LocalTime.of(dateDto.getHour(), dateDto.getMin());
	}

	public LocalDateTime transformToLocalDateTime(DateDto dateDto) {
		return LocalDateTime.of(dateDto.getYear(),
				dateDto.getMonth(),
				dateDto.getDay(), dateDto.getHour(), dateDto.getMin());
	}

	public DateDto transformDateToDto(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int year = localDate.getYear();
		int month = localDate.getMonthValue();
		int day = localDate.getDayOfMonth();
		int hour = date.getHours();
		int min = date.getMinutes();

		DateDto dateDto = new DateDto();
		dateDto.setYear(year);
		dateDto.setMonth(month);
		dateDto.setDay(day);
		dateDto.setHour(hour);
		dateDto.setMin(min);
		return dateDto;
	}

	public boolean compareDateInstanceOnly(DateDto date1, DateDto date2) {
		try {
			Date firstDate = this.transformToDate(date1);
			Date secondDate = this.transformToDate(date1);
			return this.compareDate(firstDate, secondDate);
		} catch (ParseException e) {
			return false;
		}
	}

	public Date transformToDate(DateDto dateDto) throws ParseException {
		String dateString = String.format("%1$d-%2$d-%3$d", dateDto.getDay(), dateDto.getMonth(), dateDto.getYear());
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		return formatter.parse(dateString);
	}

	public Date transformLocalDateToDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public void setTimeOnly(DateDto dateDto, LocalTime time) {
		dateDto.setHour(time.getHour());
		dateDto.setMin(time.getMinute());
	}

	public void setDateOnly(DateDto dateDto, LocalDate date) {
		dateDto.setYear(date.getYear());
		dateDto.setMonth(date.getMonthValue());
		dateDto.setDay(date.getDayOfMonth());
	}

	public boolean compareDate(Date date1, Date date2) {
		return DateUtils.isSameDay(date1, date2);
	}

	public String replaceUnderscore(String text) {
		return text.contains("_") ? text.replace("_", " ") : text;
	}

	public String formatAmount(Double amount) {
		return String.format("%,.2f", amount);
	}

	public DateTimeFormatter getTimeFormatter() {
		return DateTimeFormatter.ofPattern("KK:mm:ss a", Locale.ENGLISH);
	}

	public String formatTimeToAM_PM(LocalTime time) {
		return time.format(this.getTimeFormatter());
	}

	public DateTimeFormatter getDateFormatter() {
		return DateTimeFormatter.ofPattern("yyyy:MM:dd", Locale.ENGLISH);
	}

	public static Optional<String> getPatientSchemeName(PatientDetail patient) {
		if (patient.isSchemePatient()) {
			PatientInsuranceDetail insuranceDetail = patient.getPatientInsuranceDetail();
			if (insuranceDetail != null && insuranceDetail.getScheme() != null) {
				String insuranceName = insuranceDetail.getScheme().getInsuranceName();
				return Optional.of(insuranceName);
			}
		}
		return Optional.empty();
	}

	public static Optional<String> getSchemePatientTypeOfCare(PatientDetail patient) {
		if (patient.isSchemePatient()) {
			PatientInsuranceDetail insuranceDetail = patient.getPatientInsuranceDetail();
			if (insuranceDetail != null) {
				return Optional.ofNullable(insuranceDetail.getTypeOfCare());
			}
		}
		return Optional.empty();
	}

	public static Optional<String> getSchemePatientApprovalCode(PatientDetail patient) {
		if (patient.isSchemePatient()) {
			PatientInsuranceDetail insuranceDetail = patient.getPatientInsuranceDetail();
			if (insuranceDetail != null) {
				return Optional.ofNullable(insuranceDetail.getTypeOfCare());
			}
		}
		return Optional.empty();
	}
}
