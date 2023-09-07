package com.hmis.server.hmis.modules.others.service;

import com.hmis.server.hmis.modules.others.dto.VendorDto;
import com.hmis.server.hmis.modules.others.model.Vendor;
import com.hmis.server.hmis.modules.others.repository.VendorRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class VendorServiceImpl implements IVendorService {
	@Autowired
	private VendorRepository vendorRepository;

	@Override
	public Vendor findOne(Long id){
		Optional< Vendor > one = this.vendorRepository.findById(id);
		if( !one.isPresent() ){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Vendor");
		}
		return one.get();
	}

	@Override
	public VendorDto create(VendorDto vendorDto) {
		try {
			Vendor vendor = new Vendor();
			this.mapToModel(vendorDto, vendor);
			Vendor save = this.vendorRepository.save(vendor);
			vendorDto.setId(save.getId());
			return vendorDto;
		}
		catch( Exception e ) {
			log.error(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public VendorDto update(VendorDto dto){

		try{
			Vendor one = this.findOne(dto.getId());
			this.mapToModel(dto, one);
			this.vendorRepository.save(one);
			return dto;
		}catch( Exception e ){
			log.error(e.getMessage(), e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@Override
	public List<VendorDto> findVendorsByNameOrPhone(String name, String phone){
		List<VendorDto> dtoList = new ArrayList<>();
		List< Vendor > vendorList = this.vendorRepository.findAllBySupplierNameContainsIgnoreCaseOrPhoneNumberContainsIgnoreCase(name, phone);
		if( vendorList.size() > 0 ){
			dtoList = vendorList.stream().map(this::mapToDto).collect(Collectors.toList());
		}
		return dtoList;
	}

	public VendorDto mapToDto(Vendor model) {
		VendorDto dto = new VendorDto();
		if( model.getId() != null ){
			dto.setId(model.getId());
		}
		if( model.getOfficeAddress() != null ) {
			dto.setOfficeAddress(model.getOfficeAddress());
		}
		if( model.getSupplierName() != null ) {
			dto.setSupplierName(model.getSupplierName());
		}
		if( model.getPhoneNumber() != null ) {
			dto.setPhoneNumber(model.getPhoneNumber());
		}
		if( model.getCompanyRegistration() != null ) {
			dto.setCompanyRegistration(model.getCompanyRegistration());
		}
		if( model.getFaxNumber() != null ){
			dto.setFaxNumber(model.getFaxNumber());
		}
		if( model.getWebsiteUrl() != null ){
			dto.setWebsiteUrl(model.getWebsiteUrl());
		}
		if( model.getPostalAddress() != null ) {
			dto.setPostalAddress(model.getPostalAddress());
		}
		if( model.getEmailAddress() != null ) {
			dto.setEmailAddress(model.getEmailAddress());
		}
		if( model.getIsPharmacyVendor() != null ){
			dto.setIsPharmacyVendor(model.getIsPharmacyVendor());
		}
		return dto;
	}

	private void mapToModel(VendorDto dto, Vendor model) {
		if( dto.getSupplierName() != null ) {
			model.setSupplierName(dto.getSupplierName());
		}
		if( dto.getPhoneNumber() != null ) {
			model.setPhoneNumber(dto.getPhoneNumber());
		}
		if( dto.getCompanyRegistration() != null ) {
			model.setCompanyRegistration(dto.getCompanyRegistration());
		}
		if( dto.getOfficeAddress() != null ) {
			model.setOfficeAddress(dto.getOfficeAddress());
		}
		if( dto.getPostalAddress() != null ) {
			model.setPostalAddress(dto.getPostalAddress());
		}
		if( dto.getEmailAddress() != null ) {
			model.setEmailAddress(dto.getEmailAddress());
		}
		if( dto.getFaxNumber() != null ){
			model.setFaxNumber(dto.getFaxNumber());
		}
		if( dto.getWebsiteUrl() != null ){
			model.setWebsiteUrl(dto.getWebsiteUrl());
		}
		if( dto.getIsPharmacyVendor() != null ){
			model.setIsPharmacyVendor(dto.getIsPharmacyVendor());
		}
	}
}
