package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.ISpecialityUnitService;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.SpecialityUnitDto;
import com.hmis.server.hmis.common.common.model.SpecialityUnit;
import com.hmis.server.hmis.common.common.repository.SpecialityUnitRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SpecialityUnitServiceImpl implements ISpecialityUnitService {

	private static final String title = "Speciality Unit";

	@Autowired
	SpecialityUnitRepository specialityUnitRepository;

	@Autowired
	CommonService commonService;

	@Override
	public SpecialityUnitDto createOne(SpecialityUnitDto specialityUnitDto) {
        if (!specialityUnitDto.getId().isPresent()) {
            specialityUnitDto.setCode(Optional.of(generateSpecialityUnitCode()));
        }
		return mapModelToDto(specialityUnitRepository.save(mapDtoToModel(specialityUnitDto)));
	}

	@Override
	public List<SpecialityUnitDto> createInBatch(List<SpecialityUnitDto> specialityUnitDtoList) {
		return null;
	}

	@Override
	public List<SpecialityUnitDto> findAll() {
		return mapModelListToDtoList(specialityUnitRepository.findAll());
	}

	@Override
	public List<SpecialityUnitDto> findByNameLike(SpecialityUnitDto specialityUnitDto) {
		return null;
	}

	@Override
	public SpecialityUnitDto findByName(SpecialityUnitDto specialityUnitDto) {
		return null;
	}

	@Override
	public SpecialityUnitDto findByCode(SpecialityUnitDto specialityUnitDto) {
		return null;
	}

	@Override
	public SpecialityUnitDto findOne(SpecialityUnitDto specialityUnitDto) {
		return null;
	}

	@Override
	public SpecialityUnit findOneRaw(Long id) {
		Optional< SpecialityUnit > model = this.specialityUnitRepository.findById(id);
		if( ! model.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return model.get();
	}

	@Override
	public SpecialityUnitDto updateOne(SpecialityUnitDto specialityUnitDto) {
		return null;
	}

	@Override
	public List<SpecialityUnitDto> updateInBatch(List<SpecialityUnitDto> specialityUnitDtoList) {
		return null;
	}

	@Override
	public void activateOne(SpecialityUnitDto specialityUnitDto) {

	}

	@Override
	public void deactivateOne(SpecialityUnitDto specialityUnitDto) {

	}

	@Override
	public void activateInBatch(List<SpecialityUnitDto> specialityUnitDtoList) {

	}

	@Override
	public void deactivatedInBatch(List<SpecialityUnitDto> specialityUnitDtoList) {

	}

	@Override
	public boolean isSpecialityUnitExist(SpecialityUnitDto specialityUnitDto) {
        if (specialityUnitDto.getName().isPresent()) {
            return specialityUnitRepository.findAll().stream().anyMatch(specialityUnit -> specialityUnit.getName().compareToIgnoreCase(specialityUnitDto.getName().get()) == 0);
        } else {
            throw new HmisApplicationException("Provide Speciality Unit Name Before Checking IsExist");
        }
	}

	@Override
	public List<SpecialityUnitDto> searchByNameOrCode(String search) {
		List<SpecialityUnitDto> dtoList = new ArrayList<>();
		List<SpecialityUnit> list = this.specialityUnitRepository.findAllByNameContainsIgnoreCaseOrCodeContainsIgnoreCase(search, search);
		if (list.size() > 0) {
			dtoList = list.stream().map(this::mapModelToDto).collect(Collectors.toList());
		}
		return dtoList;
	}

	private SpecialityUnit mapDtoToModel(SpecialityUnitDto specialityUnitDto) {

        if (specialityUnitDto != null) {

            SpecialityUnit specialityUnit = new SpecialityUnit();
            setDtoToModel(specialityUnitDto, specialityUnit);

            return specialityUnit;

        } else {
            throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
        }
	}

	public SpecialityUnitDto mapModelToDto( SpecialityUnit specialityUnit ) {
        if (specialityUnit != null) {
            SpecialityUnitDto specialityUnitDto = new SpecialityUnitDto();
            setModelToDto(specialityUnitDto, specialityUnit);
            return specialityUnitDto;

        } else {
            throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
        }
	}

	private List<SpecialityUnit> mapDtoListToModelList(List<SpecialityUnitDto> specialityUnitDtoList) {
        if (specialityUnitDtoList != null && !specialityUnitDtoList.isEmpty()) {
            List<SpecialityUnit> specialityUnitList = new ArrayList<>();
            specialityUnitDtoList.forEach(specialityUnitDto -> {
                SpecialityUnit specialityUnit = new SpecialityUnit();
                setDtoToModel(specialityUnitDto, specialityUnit);
                specialityUnitList.add(specialityUnit);
            });
            return specialityUnitList;

        } else {
            throw new HmisApplicationException("Cannot Map Empty List Of Dto To Model" + title);
        }
	}

	private List<SpecialityUnitDto> mapModelListToDtoList(List<SpecialityUnit> specialityUnitList) {
		List<SpecialityUnitDto> specialityUnitDtoList = new ArrayList<>();
		if (specialityUnitList != null && !specialityUnitList.isEmpty()) {
			specialityUnitList.forEach(specialityUnit -> {
				SpecialityUnitDto specialityUnitDto = new SpecialityUnitDto();
				setModelToDto(specialityUnitDto, specialityUnit);
				specialityUnitDtoList.add(specialityUnitDto);
			});
			return specialityUnitDtoList;

		}
		return specialityUnitDtoList;
	}

	private void setModelToDto(SpecialityUnitDto specialityUnitDto, SpecialityUnit specialityUnit) {
        String label = "";
		if (specialityUnit.getId() != null) {
			specialityUnitDto.setId(Optional.of(specialityUnit.getId()));
		}

        if (specialityUnit.getName() != null) {
            specialityUnitDto.setName(Optional.of(specialityUnit.getName()));
            label = label + " "  + specialityUnit.getName();
        }

        if (specialityUnit.getCode() != null) {
            specialityUnitDto.setCode(Optional.of(specialityUnit.getCode()));
            label = label + " - " + specialityUnit.getCode();

        }

        specialityUnitDto.setLabel(label);

	}


	private void setDtoToModel(SpecialityUnitDto specialityUnitDto, SpecialityUnit specialityUnit) {
        if (specialityUnitDto.getId() != null && specialityUnitDto.getId().isPresent()) {
            specialityUnit.setId(specialityUnitDto.getId().get());
        }

        if (specialityUnitDto.getName() != null && specialityUnitDto.getName().isPresent()) {
            specialityUnit.setName(specialityUnitDto.getName().get());
        }

        if (specialityUnitDto.getCode() != null && specialityUnitDto.getCode().isPresent()) {
            specialityUnit.setCode(specialityUnitDto.getCode().get());
        }
	}

	private String generateSpecialityUnitCode() {
		GenerateCodeDto codeDto = new GenerateCodeDto();
		codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.SPECIALITY_UNIT_CODE));
		codeDto.setDefaultPrefix(HmisCodeDefaults.SPECIALITY_UNIT_DEFAULT_CODE);
		codeDto.setLastGeneratedCode(specialityUnitRepository.findTopByOrderByIdDesc().map(SpecialityUnit::getCode));
		return commonService.generateDataCode(codeDto);
	}

}
