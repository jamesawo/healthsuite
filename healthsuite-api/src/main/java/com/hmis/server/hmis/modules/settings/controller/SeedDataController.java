package com.hmis.server.hmis.modules.settings.controller;

import com.hmis.server.hmis.common.common.dto.*;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.modules.settings.service.SeedDataService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping( HmisConstant.API_PREFIX + "/seed/" )
public class SeedDataController {

	@Autowired
	SeedDataService seedDataService;

	/* ---------------------------Seed Gender---------------- */
	@PostMapping( "gender" )
	public ResponseDto createGender(@RequestBody GenderDto genderDto) {
		return seedDataService.createOneGender(genderDto);
	}

	@GetMapping( "gender/all" )
	public ResponseDto findAllGender() {
		return seedDataService.findAllGender();
	}

	@PatchMapping( value = "gender/update" )
	public ResponseDto updateGender(@RequestBody GenderDto genderDto) {
		return seedDataService.updateOneGender(genderDto);
	}

	/* ---------------------------Seed Marital Status---------------- */


	/* Seed Marital Status */
	@PostMapping( "maritalStatus" )
	public ResponseDto createMaritalStatus(@RequestBody MaritalStatusDto maritalStatusDto) throws BadRequestException {
		return seedDataService.createOneMaritalStatus(maritalStatusDto);
	}


	@GetMapping( "maritalStatus/all" )
	public ResponseDto getAllMaritalStatus() {
		return seedDataService.findAllMaritalStatus();
	}

	/* ---------------------------Seed Relationship-------------- */
	@PostMapping( "relationship" )
	public ResponseDto createRelationship(@RequestBody RelationshipDto relationshipDto) {
		return seedDataService.createRelationship(relationshipDto);
	}

	@GetMapping( "relationship/all" )
	public ResponseDto getAllRelationship() {
		return seedDataService.findAllRelationship();
	}

	/* -----------------------------Seed Religion-------------------------- */
	@GetMapping( "religion/all" )
	public ResponseDto getAllReligion() {
		return seedDataService.findAllReligion();
	}

	@PostMapping( "religion" )
	public ResponseDto createReligion(@RequestBody ReligionDto religionDto) {
		return seedDataService.createOneReligion(religionDto);
	}

	/* ----------------Seed Role---------------- */
	@GetMapping( "role/all" )
	public ResponseDto getAllRoles() {
		return seedDataService.findAllRoles();
	}

	@PostMapping( "role/create" )
	public ResponseDto createRole(@RequestBody RoleDto roleDto) {
		return seedDataService.createOneRole(roleDto);
	}

	@PostMapping( "role/update" )
	public ResponseDto updateRole(@RequestBody RoleDto roleDto) {
		return seedDataService.updateOneRole(roleDto);
	}

	/* Seed Department */
	@GetMapping( "department/all" )
	public ResponseDto getAllDepartment() {
		return seedDataService.findAllDepartment();
	}

	@GetMapping( "department-by-category-name/{departmentCategoryName}" )
	public List getDepartmentByDepartmentCategory(@PathVariable() String departmentCategoryName) {
		return seedDataService.findDepartmentByDepartmentCategory(departmentCategoryName);
	}

	@GetMapping( "departmentCategories/department" )
	public ResponseDto getAllDepartmentWithAllDepartmentCategory() {
		return seedDataService.findAllDepartmentWithAllDepartmentCategory();
	}

	@GetMapping( "departmentCategories/all" )
	public ResponseDto getAllDepartmentCategory() {
		return seedDataService.findAllDepartmentCategory();
	}

	@PostMapping( "department" )
	public ResponseDto createOneDepartment(@RequestBody DepartmentDto departmentDto) {
		return seedDataService.createOneDepartment(departmentDto);
	}

	@PostMapping( "department/update" )
	public ResponseDto updateOneDepartment(@RequestBody DepartmentDto departmentDto) {
		return seedDataService.updateOneDepartment(departmentDto);
	}

	@PostMapping( "department/batchUpload" )
	public ResponseDto createDepartmentInBatch(@RequestParam MultipartFile file) throws IOException {
		return seedDataService.createDepartmentInBatch(file);
	}

	@GetMapping( "get-one-department" )
	public DepartmentDto findDepartment(@RequestParam Long departmentId) {
		return seedDataService.findOneDepartment(departmentId);
	}

	@GetMapping( "get-one-department-by-code/{code}" )
	public DepartmentDto findDepartmentByCode(@PathVariable String code) {
		return seedDataService.findOneDepartmentByCode(code);
	}

	@GetMapping( "get-one-department-by-name/{name}" )
	public DepartmentDto findDepartmentByName(@PathVariable String name) {
		return seedDataService.findOneDepartmentByName(name);
	}

	@GetMapping( "verify-department-location" )
	public ResponseDto verifyLocation(@RequestParam Long depId, @RequestParam String depCatName) {
		return seedDataService.isLocationValid(depId, depCatName);
	}

	/* Seed Surgery */
	@GetMapping( "surgery/all" )
	public ResponseDto getAllSurgery() {
		return seedDataService.findAllSurgery();
	}

	@PostMapping( "surgery" )
	public ResponseDto createOneSurgery(@RequestBody SurgeryDto surgeryDto) {
		return seedDataService.createOneSurgery(surgeryDto);
	}

	/* Seed Drug Classification */
	@GetMapping( "drugClassification/all" )
	public ResponseDto getAllDrugClassification() {
		return seedDataService.findAllDrugClassification();
	}

	@PostMapping( "drugClassification" )
	public ResponseDto createOneDrugClassification(@RequestBody DrugClassificationDto drugClassificationDto) {
		return seedDataService.createOneDrugClassification(drugClassificationDto);
	}

	/* Seed Drug Formulation */
	@GetMapping( "drugFormulation/all" )
	public ResponseDto getAllDrugFormulation() {
		return seedDataService.findAllDrugFormulation();
	}

	@PostMapping( "drugFormulation" )
	public ResponseDto createOneDrugFormulation(@RequestBody DrugFormulationDto drugFormulationDto) throws BadRequestException {
		return seedDataService.createOneDrugFormulation(drugFormulationDto);
	}

	/* Seed Ward */
	@GetMapping( "ward/all" )
	public ResponseDto getAllWard() {
		// return List<department> with category as wards
		return seedDataService.findAllWard();
	}

	@GetMapping( "search-ward" )
	public List searchPatient(@RequestParam( value = "search" ) String search) {
		return this.seedDataService.searchWard(search);
	}

	/* Seed Bed */
	@GetMapping( "ward/wards-and-bed-count" )
	public ResponseDto getAllBed() {
		return seedDataService.findWardsWithBedCount();
	}

	@GetMapping( "bed/getByWard" )
	public ResponseDto getBedsByWard(@RequestParam Long wardId) {
		return seedDataService.findBedsByWard(wardId);
	}

	@PostMapping( "bed/add-beds-to-ward" )
	public ResponseDto createOneBed(@RequestBody WardDto wardDto) {
		return seedDataService.createBeds(wardDto);
	}

	@PostMapping( "ward/update-bed-count" )
	public ResponseDto updateWardBedCount(@RequestBody WardDto wardDto) {
		return seedDataService.updateWardBedCount(wardDto);
	}

	/* Seed Revenue Department */
	@GetMapping( "revenueDepartment/all" )
	public ResponseDto getAllRevenueDepartment() {
		return seedDataService.findAllRevenueDepartment();
	}

	@PostMapping( "revenueDepartment/create" )
	public ResponseDto createOneRevenueDepartment(@RequestBody RevenueDepartmentDto revenueDepartmentDto) {
		return seedDataService.createOneRevenueDepartment(revenueDepartmentDto);
	}

	@GetMapping( "revenue-department/deposit-revenue-department" )
	public ResponseDto findDepositRevenueDepartment() {
		return this.seedDataService.findDepositRevenueDepartment();
	}

	@PostMapping( "revenueDepartment/createMany" )
	public ResponseDto createManyRevenueDepartment(@RequestBody List< RevenueDepartmentDto > revenueDepartmentDtoList) {
		return seedDataService.createRevenueDepartmentInBatch(revenueDepartmentDtoList);
	}

	@PostMapping( "revenueDepartment/createFromExcel" )
	public ResponseDto createRevenueDepartmentFromExcel(@RequestParam MultipartFile file) {
		return seedDataService.createRevenueDepartmentFromExcel(file);
	}

	/* Revenue Department Type */
	@PostMapping( "revenueDepartmentType/create" )
	public ResponseDto createRevenueDepartmentType(@RequestBody RevenueDepartmentTypeDto revenueDepartmentTypeDto) {
		return seedDataService.createOneRevenueDepartmentType(revenueDepartmentTypeDto);
	}

//	@GetMapping( "revenueDepartmentType/seedDefault" )
//	public ResponseDto seedDefaultRevenueDepartmentType() {
//		return seedDataService.seedDefaultRevenueDepartmentType();
//	}

	@GetMapping( "revenueDepartmentType/all" )
	public ResponseDto getAllDefaultRevenueDepartmentType() {
		return seedDataService.getAllRevenueDepartmentType();
	}

	/* Seed Speciality Unit */
	@GetMapping( "specialityUnit/all" )
	public ResponseDto getAllSpecialityUnit() {
		return seedDataService.findAllSpecialityUnit();
	}

	@PostMapping( "specialityUnit/create" )
	public ResponseDto createSpecialityUnit(@RequestBody SpecialityUnitDto specialityUnitDto) {
		return seedDataService.createOneSpecialityUnit(specialityUnitDto);
	}

	@GetMapping( "search-speciality" )
	public List searchSpeciality(@RequestParam( "search" ) String search) {
		return this.seedDataService.searchSpeciality(search);
	}

	/*  Seed Pharmacy Patient Category  */
	@GetMapping( "pharmacyPatientCategory/all" )
	public ResponseDto getAllPharmacyPatientCategory() {
		return seedDataService.findAllPharmacyPatientCategory();
	}

	@PostMapping( "pharmacyPatientCategory/create" )
	public ResponseDto createOnePharmacyPatientCategory(@RequestBody PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
		return seedDataService.createOnePharmacyPatientCategory(pharmacyPatientCategoryDto);
	}

	/* Seed Payment Method */
//	@GetMapping( "paymentMethod/seedDefault" )
//	public ResponseDto seedDefaultPaymentMethod() {
//		return seedDataService.seedDefaultPaymentMethod();
//	}

	@GetMapping( "paymentMethod/all" )
	public ResponseDto getAllPaymentMethod() {
		return seedDataService.getAllPaymentMethod();
	}

	/* Seed Pharmacy Patient Category Types */
	@GetMapping( "pharmacyPatientCategoryTypes/seedDefault" )
	public ResponseDto seedDefaultPharmacyPatientCategoryTypes() {
		return seedDataService.seedDefaultPharmacyPatientCategoryTypes();
	}

	@GetMapping( "pharmacyPatientCategoryTypes/all" )
	public ResponseDto getAllPharmacyPatientCategoryTypes() {
		return seedDataService.getAllPharmacyPatientCategoryTypes();
	}

	/* Seed Pharmacy Supplier Category */
	@GetMapping( "pharmacySupplierCategory/all" )
	public ResponseDto getAllPharmacySupplierCategory() {
		return seedDataService.findAllPharmacySupplierCategory();
	}

	@PostMapping( "pharmacySupplierCategory/create" ) // create and update
	public ResponseDto createOnePharmacySupplierCategory(@RequestBody PharmacySupplierCategoryDto supplierCategoryDto) {
		return seedDataService.createOnePharmacySupplierCategory(supplierCategoryDto);
	}

	/* Seed Nursing Note Label */
	@GetMapping( "nursingNoteLabel/all" )
	public ResponseDto getAllNursingNoteLabel() {
		return seedDataService.findAllNursingNoteLabel();
	}

	@PostMapping( "nursingNoteLabel/create" ) // create and update
	public ResponseDto createOneNursingNoteLabel(@RequestBody NursingNoteLabelDto nursingNoteLabelDto) {
		return seedDataService.createOneNursingNoteLabel(nursingNoteLabelDto);
	}

	/*Seed Lab Specimen*/
	@GetMapping( "labSpecimen/all" )
	public ResponseDto getAllLabSpecimen() {
		return seedDataService.findAllLabSpecimen();
	}

	@PostMapping( "labSpecimen/create" ) // create and update
	public ResponseDto createOneLabSpecimen(@RequestBody LabSpecimenDto labSpecimenDto) {
		return seedDataService.createOneLabSpecimen(labSpecimenDto);
	}

	/*Seed Organism*/
	@GetMapping( "organism/all" )
	public ResponseDto getAllOrganism() {
		return seedDataService.findAllOrganism();
	}

	@PostMapping( "organism/create" ) // create and update
	public ResponseDto createOneOrganism(@RequestBody OrganismDto organismDto) {
		return seedDataService.createOneOrganism(organismDto);
	}

	/*Seed Antibiotics*/
	@GetMapping( "antibiotics/all" )
	public ResponseDto getAllAntibiotics() {
		return seedDataService.findAllAntibiotics();
	}

	@PostMapping( "antibiotics/create" ) // create and update
	public ResponseDto createOneAntibiotics(@RequestBody AntibioticsDto antibioticsDto) {
		return seedDataService.createOneAntibiotics(antibioticsDto);
	}

	/*Seed Bill Waiver Category*/
	@GetMapping( "billWaiver/all" )
	public ResponseDto getAllBillWaiver() {
		return seedDataService.findAllBillWaiver();
	}

	@PostMapping( "billWaiver/create" ) // create and update
	public ResponseDto createOneBillWaiver(@RequestBody BillWaiverCategoryDto billWaiverCategoryDto) {
		return seedDataService.createOneBillWaiverCategory(billWaiverCategoryDto);
	}

	/* Seed All Default Parameters At Once */
	@GetMapping( "seedAllDefaultParam" )
	public void seedAllDefaultParams() {
//		seedDataService.seedDefaultRevenueDepartmentType();
//		seedDataService.seedDefaultPaymentMethod();
//		seedDataService.seedDefaultPharmacyPatientCategoryTypes();
	}

	/* Nationality */
	@GetMapping( "nationality/all" )
	public ResponseDto getNationality() {
		return seedDataService.findAllNationality();
	}

	@GetMapping( "nationality/parent-only" )
	public ResponseDto getNationalityParentOnly() {
		return seedDataService.findAllNationalityParent();
	}

	@PostMapping( "nationality/create" )
	public ResponseDto createNationality(@RequestBody NationalityDto nationalityDto) {
		return seedDataService.createNationality(nationalityDto);
	}

	@GetMapping( "nationality/parent-with-children" )
	public ResponseDto getNationalityWithChildren() {
		return this.seedDataService.findAllNationalityWithChildren();
	}

	/*Ethnic Group*/
	@GetMapping( "ethnic-group/all" )
	public ResponseDto getAllEthnicGroup() {
		return seedDataService.findAllEthnicGroup();
	}

	@PostMapping( "ethnic-group/create" )
	public ResponseDto createEthnicGroup(@RequestBody EthnicGroupDto ethnicGroup) {
		return seedDataService.createEthnicGroup(ethnicGroup);
	}

	@PutMapping( "ethnic-group/update" )
	public ResponseDto update(@RequestBody EthnicGroupDto ethnicGroup) {
		return this.seedDataService.updateEthnicGroup(ethnicGroup);
	}

	/* means of identification */
	@GetMapping( "means-of-identification/all" )
	public ResponseDto getAllMeansOfIdentification() {
		return this.seedDataService.findAllMeansOfIdentification();
	}

	@PostMapping( "means-of-identification/create" )
	public ResponseDto createMeansOfIdentification(@RequestBody MeansOfIdentificationDto payload) {
		return this.seedDataService.createMeansOfIdentification(payload);
	}

	/* scheme */
	@GetMapping( "scheme/all" )
	public ResponseDto getAllSchemeData() {
		return this.seedDataService.findAllScheme();
	}

	@PostMapping( "scheme/create" )
	public ResponseDto createSchemeData(@RequestBody SchemeDto schemeDto) {
		return this.seedDataService.createScheme(schemeDto);
	}
}
