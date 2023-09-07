package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IDrugFormulationService;
import com.hmis.server.hmis.common.common.dto.DrugFormulationDto;
import com.hmis.server.hmis.common.common.model.DrugFormulation;
import com.hmis.server.hmis.common.common.repository.DrugFormulationRepository;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;
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
public class DrugFormulationServiceImpl implements IDrugFormulationService {
	@Autowired
	DrugFormulationRepository drugFormulationRepository;

	@Override
	public DrugFormulationDto createOne(DrugFormulationDto drugFormulationDto) {
		return mapModelToDto(drugFormulationRepository.save(mapDtoToModel(drugFormulationDto)));
	}

	@Override
	public void createInBatch(List< DrugFormulationDto > drugFormulationDtoList) {
	}

	@Override
	public DrugFormulationDto findOne(DrugFormulationDto drugFormulationDto) throws EntityNotFoundException {
		if( drugFormulationDto.getId().isPresent() ) {
			return mapModelToDto(drugFormulationRepository.getOne(drugFormulationDto.getId().get()));
		}
		else {
			throw new EntityNotFoundException(HmisConstant.STATUS_400, "No Record Found");
		}
	}

	@Override
	public DrugFormulation findOne(Long formulationId) {
		Optional< DrugFormulation > formulation = this.drugFormulationRepository.findById(formulationId);
		if( ! formulation.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Drug Formulation ID");
		}
		return formulation.get();
	}


	@Override
	public DrugFormulationDto findByName(DrugFormulationDto drugFormulationDto) {
		return null;
	}

	@Override
	public DrugFormulation findByName(String name) {
		Optional< DrugFormulation > optional = this.drugFormulationRepository.findByNameIgnoreCase(name);
		if( ! optional.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Drug Formulation With Name: %s Not Found", name));
		}
		return optional.get();
	}

	@Override
	public List< DrugFormulationDto > findAll() {
		return mapModelListToDtoList(drugFormulationRepository.findAll().stream().sorted(Comparator.comparing(DrugFormulation::getId)).collect(Collectors.toList()));
	}

	@Override
	public DrugFormulationDto updateOne(DrugFormulationDto drugFormulationDto) {
		return null;
	}

	@Override
	public void updateInBatch(List< DrugFormulationDto > drugFormulationDtoList) {

	}

	@Override
	public void deactivateOne(DrugFormulationDto drugFormulationDto) {

	}

	@Override
	public boolean isDrugFormulationExist(DrugFormulationDto drugFormulationDto) throws BadRequestException {
		if( drugFormulationDto.getName().isPresent() ) {
			return drugFormulationRepository.findAll().stream().anyMatch(drugFormulation -> drugFormulation.getName().compareToIgnoreCase(drugFormulationDto.getName().get()) == 0);
		}
		else {
			throw new BadRequestException(HmisConstant.STATUS_400, "Provide Drug Formulation Name");
		}
	}

	public DrugFormulationDto mapModelToDto(DrugFormulation drugFormulation) {
		DrugFormulationDto drugFormulationDto = new DrugFormulationDto();
		if( drugFormulation.getId() != null ) {
			drugFormulationDto.setId(Optional.of(drugFormulation.getId()));
		}
		if( drugFormulation.getName() != null ) {
			drugFormulationDto.setName(Optional.of(drugFormulation.getName()));
		}
		return drugFormulationDto;
	}

	private DrugFormulation mapDtoToModel(DrugFormulationDto drugFormulationDto) {
		DrugFormulation drugFormulation = new DrugFormulation();
		if( drugFormulationDto.getId().isPresent() ) {
			drugFormulation.setId(drugFormulationDto.getId().get());
		}
		if( drugFormulationDto.getName().isPresent() ) {
			drugFormulation.setName(drugFormulationDto.getName().get());
		}
		return drugFormulation;
	}


	private List< DrugFormulation > mapDtoListToModelList(List< DrugFormulationDto > drugFormulationDtoList) {
		List< DrugFormulation > formulationArrayList = new ArrayList<>();
		if( ! drugFormulationDtoList.isEmpty() ) {
			drugFormulationDtoList.forEach(drugFormulationDto -> {
				DrugFormulation drugClassification = new DrugFormulation();
				if( drugFormulationDto.getId().isPresent() ) {
					drugClassification.setId(drugFormulationDto.getId().get());
				}
				if( drugFormulationDto.getName().isPresent() ) {
					drugClassification.setName(drugFormulationDto.getName().get());
				}
				formulationArrayList.add(drugClassification);
			});
		}
		return formulationArrayList;
	}

	private List< DrugFormulationDto > mapModelListToDtoList(List< DrugFormulation > drugFormulationList) {
		List< DrugFormulationDto > drugFormulationDtoList = new ArrayList<>();
		if( ! drugFormulationList.isEmpty() ) {
			drugFormulationList.forEach(drugClassification -> {
				DrugFormulationDto drugFormulationDto = new DrugFormulationDto();
				if( drugClassification.getId() != null ) {
					drugFormulationDto.setId(Optional.ofNullable(drugClassification.getId()));
				}
				if( drugClassification.getName() != null ) {
					drugFormulationDto.setName(Optional.of(drugClassification.getName()));
				}
				drugFormulationDtoList.add(drugFormulationDto);
			});
		}
		return drugFormulationDtoList;
	}
}
