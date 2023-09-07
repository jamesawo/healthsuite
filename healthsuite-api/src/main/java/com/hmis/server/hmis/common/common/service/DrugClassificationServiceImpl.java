package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IDrugClassificationService;
import com.hmis.server.hmis.common.common.dto.DrugClassificationDto;
import com.hmis.server.hmis.common.common.model.DrugClassification;
import com.hmis.server.hmis.common.common.repository.DrugClassificationRepository;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DrugClassificationServiceImpl implements IDrugClassificationService {

	@Autowired
	DrugClassificationRepository drugClassificationRepository;

	@Override
	public DrugClassificationDto createOne(DrugClassificationDto drugClassificationDto) {
		return mapModelToDto(drugClassificationRepository.save(mapDtoToModel(drugClassificationDto)));
	}

	@Override
	public void createInBatch(List< DrugClassificationDto > drugClassificationDtoList) {

	}

	@Override
	public DrugClassificationDto findOne(DrugClassificationDto drugClassificationDto) {
		if( drugClassificationDto.getId().isPresent() ) {
			return mapModelToDto(drugClassificationRepository.getOne(drugClassificationDto.getId().get()));
		}
		else {
			throw new HmisApplicationException("Provide Drug Classification Before Search");
		}
	}

	@Override
	public DrugClassification findOne(Long classificationId) {
		Optional< DrugClassification > byId = this.drugClassificationRepository.findById(classificationId);
		if( ! byId.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Invalid Classification ID: %s", classificationId));
		}
		return byId.get();
	}


	@Override
	public List< DrugClassificationDto > findAll() {
		return mapModelListToDtoList(drugClassificationRepository.findAll().stream().sorted(Comparator.comparing(DrugClassification::getId)).collect(Collectors.toList()));
	}

	@Override
	public DrugClassificationDto findByName(DrugClassificationDto drugClassificationDto) {
		return null;
	}

	@Override
	public DrugClassification findByName(String name) {
		Optional< DrugClassification > drugClassification = this.drugClassificationRepository.findByNameIgnoreCase(name);
		if( ! drugClassification.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Drug Classification With Name: %s Not found", name));
		}
		return drugClassification.get();
	}

	@Override
	public DrugClassification findByNameOrCode(String name) {
		Optional< DrugClassification > drugClassification = this.drugClassificationRepository.findByNameIgnoreCaseOrCodeIgnoreCase(name, name);
		if( ! drugClassification.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Drug Classification With Name: %s Not found", name));
		}
		return drugClassification.get();
	}

	@Override
	public DrugClassificationDto findByCode(DrugClassificationDto drugClassificationDto) {
		return null;
	}

	@Override
	public DrugClassificationDto updateOne(DrugClassificationDto drugClassificationDto) {
		return null;
	}

	@Override
	public DrugClassificationDto updateInBatch(List< DrugClassificationDto > drugClassificationDtoList) {
		return null;
	}

	@Override
	public void deactivateOne(DrugClassificationDto drugClassificationDto) {
	}

	@Override
	public boolean isDrugClassificationExist(DrugClassificationDto drugClassificationDto) {
		if( drugClassificationDto.getName().isPresent() ) {
			return drugClassificationRepository.findAll().stream().anyMatch(drugClassification -> drugClassification.getName().compareToIgnoreCase(drugClassificationDto.getName().get()) == 0);
		}
		else {
			throw new HmisApplicationException("Provide Drug Classification Name");
		}
	}

	public DrugClassificationDto mapModelToDto(DrugClassification drugClassification) {
		DrugClassificationDto drugClassificationDto = new DrugClassificationDto();
		if( drugClassification.getId() != null ) {
			drugClassificationDto.setId(Optional.of(drugClassification.getId()));
		}
		if( drugClassification.getName() != null ) {
			drugClassificationDto.setName(Optional.of(drugClassification.getName()));
		}
		return drugClassificationDto;
	}

	private DrugClassification mapDtoToModel(DrugClassificationDto drugClassificationDto) {
		DrugClassification drugClassification = new DrugClassification();
		if( drugClassificationDto.getId().isPresent() ) {
			drugClassification.setId(drugClassificationDto.getId().get());
		}
		if( drugClassificationDto.getName().isPresent() ) {
			drugClassification.setName(drugClassificationDto.getName().get());
		}
		return drugClassification;
	}

	private List< DrugClassification > mapDtoListToModelList(List< DrugClassificationDto > drugClassificationDtoList) {
		List< DrugClassification > drugClassificationList = new ArrayList<>();
		if( ! drugClassificationDtoList.isEmpty() ) {
			drugClassificationDtoList.forEach(surgeryDto -> {
				DrugClassification drugClassification = new DrugClassification();
				if( surgeryDto.getId().isPresent() ) {
					drugClassification.setId(surgeryDto.getId().get());
				}
				if( surgeryDto.getName().isPresent() ) {
					drugClassification.setName(surgeryDto.getName().get());
				}

				drugClassificationList.add(drugClassification);
			});
		}
		return drugClassificationList;

	}

	private List< DrugClassificationDto > mapModelListToDtoList(List< DrugClassification > drugClassificationList) {
		List< DrugClassificationDto > drugClassificationDtoList = new ArrayList<>();
		if( ! drugClassificationList.isEmpty() ) {
			drugClassificationList.forEach(drugClassification -> {
				DrugClassificationDto classificationDto = new DrugClassificationDto();
				if( drugClassification.getId() != null ) {
					classificationDto.setId(Optional.ofNullable(drugClassification.getId()));
				}
				if( drugClassification.getName() != null ) {
					classificationDto.setName(Optional.of(drugClassification.getName()));
				}
				drugClassificationDtoList.add(classificationDto);
			});
		}
		return drugClassificationDtoList;
	}

}
