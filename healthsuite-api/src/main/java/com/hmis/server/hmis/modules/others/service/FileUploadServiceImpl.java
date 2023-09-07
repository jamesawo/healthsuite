package com.hmis.server.hmis.modules.others.service;

import com.hmis.server.hmis.common.common.model.ProductService;
import com.hmis.server.hmis.common.common.service.*;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import com.hmis.server.hmis.modules.pharmacy.service.DrugRegisterServiceImpl;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileUploadServiceImpl implements IFileUploadService {

	public static String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	@Autowired
	RevenueDepartmentServiceImpl revenueDepartmentService;
	@Autowired
	DepartmentServiceImpl serviceDepartmentService;
	@Autowired
	ProductServiceImpl productService;
	@Autowired
	CommonService commonService;
	@Autowired
	private DrugFormulationServiceImpl drugFormulationService;
	@Autowired
	private DrugClassificationServiceImpl drugClassificationService;
	@Autowired
	private DrugRegisterServiceImpl drugRegisterService;

	private Workbook workbook;
	private Map< String, String > scrapMap = new HashMap<>();


	@Override
	public boolean isExcelFile(MultipartFile file) {
		return EXCEL_TYPE.equals(file.getContentType());
	}

	@Override
	public Map< String, Integer > handleServicesBatchUpload(MultipartFile file) {
		this.validateFile(file);
		try {
			// create a workbook
			workbook = new XSSFWorkbook(file.getInputStream());
			// Retrieving the number of sheets in the Workbook
			//System.out.println("-------Workbook has '" + workbook.getNumberOfSheets() + "' Sheets-----");
			// Getting the Sheet at index zero
			Sheet sheet = workbook.getSheetAt(0);
			// Getting number of columns in the Sheet
			int noOfColumns = sheet.getRow(0).getLastCellNum();
			//this.checkNumberOfColumns(noOfColumns, EXCEL_COLUMNS_FOR_SERVICE_REGISTER);

			//System.out.println("-------Sheet has '" + noOfColumns + "' columns------");

			List< ProductService > list = new ArrayList<>();
			Iterator< Row > iterator = sheet.rowIterator();
			for( int index = 0; index < sheet.getPhysicalNumberOfRows(); index++ ) {
				if( index > 0 ) {
					ProductService service = new ProductService();
					XSSFRow row = ( XSSFRow ) sheet.getRow(index);
					if( isRowEmpty(row) ) {
						continue;
					}
					// TODO: 10/7/21 :: validate row value
					String revenueDepartmentCode = row.getCell(0).getStringCellValue();
					String serviceDepartmentCode = row.getCell(1).getStringCellValue();
					String serviceName = row.getCell(2).getStringCellValue();
					Double costPrice = row.getCell(3).getNumericCellValue();
					Double unitCost = row.getCell(4).getNumericCellValue();
					Double regularPrice = row.getCell(5).getNumericCellValue();
					Double nhisPrice = row.getCell(6).getNumericCellValue();
					Double discount = row.getCell(7).getNumericCellValue();
					String usage = row.getCell(8).getStringCellValue();
					String applyDiscount = row.getCell(9).getStringCellValue();

					service.setRevenueDepartment(this.revenueDepartmentService.findByCode(revenueDepartmentCode)); //todo:: check if department code is valid
					service.setDepartment(this.serviceDepartmentService.findByCode(serviceDepartmentCode));
					service.setName(serviceName);
					service.setCostPrice(costPrice);
					service.setUnitCost(unitCost);
					service.setRegularSellingPrice(regularPrice);
					service.setNhisSellingPrice(nhisPrice);
					service.setDiscount(discount);
					service.setUsedFor(this.commonService.getServiceUsageValue(usage).name());
					if( applyDiscount.equalsIgnoreCase("yes") ) {
						service.setApplyDiscount(true);
					}
					else {
						service.setApplyDiscount(false);
					}
					list.add(service);
				}
			}
			// Closing the workbook
			workbook.close();
			//save services
			if (list.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, HmisConstant.EMPTY_FILE);
			}
			return this.productService.createMany(list);
		}
		catch( IOException e ) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public Map< String, Integer > handleDrugBatchUpload(MultipartFile file) {
		this.validateFile(file);
		try {
			workbook = new XSSFWorkbook(file.getInputStream());
			Sheet sheet = workbook.getSheetAt(0);
			int noOfColumns = sheet.getRow(0).getLastCellNum();
			//this.checkNumberOfColumns(noOfColumns, EXCEL_COLUMNS_FOR_DRUG_REGISTER);
			List< DrugRegister > drugRegisters = new ArrayList<>();

			for( int index = 0; index < sheet.getPhysicalNumberOfRows(); index++ ) {
				if( index > 0 ) {
					XSSFRow row = ( XSSFRow ) sheet.getRow(index);
					if( isRowEmpty(row) ) {
						continue;
					}
					String revenueDepartmentCode = row.getCell(0).getStringCellValue();
					String genericName = row.getCell(1).getStringCellValue();
					String brandName = row.getCell(2).getStringCellValue();
					Double regularSellingPrice = row.getCell(3).getNumericCellValue();
					Double nhisSellingPrice = row.getCell(4).getNumericCellValue();
					Double discountPercent = row.getCell(5).getNumericCellValue();
					String strength = row.getCell(6).getStringCellValue();
					Double costPrice = row.getCell(7).getNumericCellValue();
					Double unitCostPrice = row.getCell(8).getNumericCellValue();
					Double unitsPerPack = row.getCell(9).getNumericCellValue();
					Double packsPerPacking = row.getCell(10).getNumericCellValue();
					String formulation = row.getCell(11).getStringCellValue();
					Double normalMarkUp = row.getCell(12).getNumericCellValue();
					Double nhisMarkUp = row.getCell(13).getNumericCellValue();
					String classification = row.getCell(14).getStringCellValue();
					Double unitOfIssue = row.getCell(15).getNumericCellValue();
					Double reorderLevel = row.getCell(16).getNumericCellValue();

					DrugRegister drugRegister = new DrugRegister();
					drugRegister.setRevenueDepartment(this.revenueDepartmentService.findByCode(revenueDepartmentCode));
					drugRegister.setFormulation(drugFormulationService.findByName(formulation));
					drugRegister.setClassification(drugClassificationService.findByName(classification));
					drugRegister.setBrandName(brandName);
					drugRegister.setGenericName(genericName);
					drugRegister.setStrength(strength);
					drugRegister.setUnitOfIssue(unitOfIssue.intValue());
					drugRegister.setUnitPerPack(unitsPerPack.intValue());
					drugRegister.setPacksPerPackingUnit(packsPerPacking.intValue());
					drugRegister.setCostPrice(costPrice);
					drugRegister.setUnitCostPrice(unitCostPrice);
					drugRegister.setNhisMarkup(nhisMarkUp.intValue());
					drugRegister.setGeneralMarkUp(normalMarkUp.intValue());
					drugRegister.setRegularSellingPrice(regularSellingPrice);
					drugRegister.setNhisSellingPrice(nhisSellingPrice);
					drugRegister.setDiscountPercent(discountPercent);
					drugRegister.setReorderLevel(reorderLevel.intValue());
					drugRegisters.add(drugRegister);
				}
			}
			workbook.close();
			return this.drugRegisterService.registerDrugsInBatchRaw(drugRegisters);

		}
		catch( IOException e ) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public Resource downloadFile(String filename) {
		String filesPath = "src/main/resources/downloads";
		try {
			Path file = Paths.get(filesPath).resolve(filename);
			Resource resource = new UrlResource(file.toUri());

			if( resource.exists() || resource.isReadable() ) {
				return resource;
			}
			else {
				throw new RuntimeException("Could not read the file!");
			}
		}
		catch( MalformedURLException e ) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	private void checkNumberOfColumns(int noOfColumns, int expectedNumber) {
		if( noOfColumns != expectedNumber ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID NO. OF SHEET COLUMNS");
		}
	}

	private static boolean isRowEmpty(Row row) {
		boolean isEmpty = true;
		DataFormatter dataFormatter = new DataFormatter();
		if( row != null ) {
			for( Cell cell : row ) {
				if( dataFormatter.formatCellValue(cell).trim().length() > 0 ) {
					isEmpty = false;
					break;
				}
			}
		}
		return isEmpty;
	}

	private void validateFile(MultipartFile file) {
		if( ! this.isExcelFile(file) ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

	private void prepareScrapFile(Object value) {
	}

	private boolean isCellValueNullOrEmpty(Object value) {
		//todo: use null/invalid cell value to make scrap file.
		return value == null || ObjectUtils.isEmpty(value);
	}

	/*
		public static List parseExcelFile(MultipartFile file) {
		try {
			Workbook workbook = new XSSFWorkbook(file.getInputStream());

			Sheet sheet = workbook.getSheetAt(0);
			Iterator rows = sheet.iterator();


			List< ProductServiceDto > dtoList = new ArrayList<>();

			int rowNumber = 0;
			while( rows.hasNext() ) {
				Row currentRow = ( Row ) rows.next();

				// skip header
				if( rowNumber == 0 ) {
					rowNumber++;
					continue;
				}

				Iterator cellsInRow = currentRow.iterator();

				ProductServiceDto productServiceDto = new ProductServiceDto();

				int cellIndex = 0;
				while( cellsInRow.hasNext() ) {
					Cell currentCell = ( Cell ) cellsInRow.next();

					//                    if (cellIndex == 0) {
					//                        // RevenueDepartmentCode
					//                        productServiceDto.setRevenueDepartmentId( currentCell.getStringCellValue());
					//                    } else if (cellIndex == 1) {
					//                        // ServiceDepartmentCode
					//                        productServiceDto.setDepartmentId( currentCell.getStringCellValue());
					//                    } else if (cellIndex == 2) {
					//                        // Title
					//                        productServiceDto.setAddress(currentCell.getStringCellValue());
					//                    } else if (cellIndex == 3) {
					//                        // CostPrice
					//                        productServiceDto.setAge((int) currentCell.getNumericCellValue());
					//                    } else if (cellIndex == 4) {
					//                        // UnitCostPrice
					//                        productServiceDto.setName(currentCell.getStringCellValue());
					//                    } else if (cellIndex == 5) {
					//                        // RegularPrice
					//                        productServiceDto.setAddress(currentCell.getStringCellValue());
					//                    } else if (cellIndex == 6) {
					//                        // NHISPrice
					//                        productServiceDto.setAge((int) currentCell.getNumericCellValue());
					//                    }else if (cellIndex == 7) {
					//                        // Discount (%)
					//                        productServiceDto.setAddress(currentCell.getStringCellValue());
					//                    } else if (cellIndex == 8) {
					//                        // Usage
					//                        productServiceDto.setAge((int) currentCell.getNumericCellValue());
					//                    } else if (cellIndex == 9) {
					//                        // ApplyDiscount
					//                        productServiceDto.setName(currentCell.getStringCellValue());
					//                    }

					cellIndex++;
				}

				dtoList.add(productServiceDto);
			}

			// Close WorkBook
			workbook.close();

			return dtoList;
		}
		catch( IOException e ) {
			throw new RuntimeException("FAIL! -> message = " + e.getMessage());
		}
	}
	 */

}
