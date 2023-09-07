package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.INursingNoteLabelService;
import com.hmis.server.hmis.common.common.dto.NursingNoteLabelDto;
import com.hmis.server.hmis.common.common.model.NursingNoteLabel;
import com.hmis.server.hmis.common.common.repository.NursingNoteLabelRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NursingNoteLabelServiceImpl implements INursingNoteLabelService {

	private static final String title = "Nursing Note Label";

	@Autowired
	NursingNoteLabelRepository nursingNoteLabelRepository;

	@Override
	public NursingNoteLabelDto createOne(NursingNoteLabelDto nursingNoteLabelDto) {
		if( isNursingNoteLabelExist(nursingNoteLabelDto) ) {
			throw new HmisApplicationException("Nursing Note Already Exist");
		}
		return mapModelToDto(nursingNoteLabelRepository.save(mapDtoToModel(nursingNoteLabelDto)));
	}

	@Override
	public void createInBatch(List< NursingNoteLabelDto > nursingNoteLabelDtoList) {

	}

	@Override
	public NursingNoteLabel findOneRaw(Long id) {
		Optional< NursingNoteLabel > optional = this.nursingNoteLabelRepository.findById(id);
		if( ! optional.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return optional.get();
	}

	@Override
	public List< NursingNoteLabelDto > findAll() {
		return mapModelListToDtoList(nursingNoteLabelRepository.findAll());
	}

	@Override
	public NursingNoteLabelDto findOne(NursingNoteLabelDto nursingNoteLabelDto) {
		return null;
	}

	@Override
	public NursingNoteLabelDto findByName(NursingNoteLabelDto nursingNoteLabelDto) {
		return null;
	}

	@Override
	public NursingNoteLabelDto findByCode(NursingNoteLabelDto nursingNoteLabelDto) {
		return null;
	}

	@Override
	public List< NursingNoteLabelDto > findByNameLike(NursingNoteLabelDto nursingNoteLabelDto) {
		return null;
	}

	@Override
	public NursingNoteLabelDto updateOne(NursingNoteLabelDto nursingNoteLabelDto) {
		return null;
	}

	@Override
	public void updateInBatch(List< NursingNoteLabelDto > nursingNoteLabelDtoList) {

	}

	@Override
	public void deactivateOne(NursingNoteLabelDto nursingNoteLabelDto) {

	}

	@Override
	public boolean isNursingNoteLabelExist(NursingNoteLabelDto nursingNoteLabelDto) {
		if( nursingNoteLabelDto.getName().isPresent() ) {
			return nursingNoteLabelRepository.findAll().stream().anyMatch(nursingNoteLabel -> nursingNoteLabel.getName().compareToIgnoreCase(nursingNoteLabelDto.getName().get()) == 0);
		}
		else {
			throw new HmisApplicationException("Provide " + title + " Name Before Is Exist Check");
		}
	}

	private NursingNoteLabelDto mapModelToDto(NursingNoteLabel nursingNoteLabel) {
		if( nursingNoteLabel != null ) {
			NursingNoteLabelDto nursingNoteLabelDto = new NursingNoteLabelDto();
			setDto(nursingNoteLabelDto, nursingNoteLabel);

			return nursingNoteLabelDto;
		}
		else {
			throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
		}
	}

	private List< NursingNoteLabelDto > mapModelListToDtoList(List< NursingNoteLabel > nursingNoteLabelList) {
		List< NursingNoteLabelDto > nursingNoteLabelDtoList = new ArrayList<>();
		if( nursingNoteLabelList != null && ! nursingNoteLabelList.isEmpty() ) {
			nursingNoteLabelList.forEach(nursingNoteLabel -> {
				NursingNoteLabelDto nursingNoteLabelDto = new NursingNoteLabelDto();
				setDto(nursingNoteLabelDto, nursingNoteLabel);
				nursingNoteLabelDtoList.add(nursingNoteLabelDto);
			});
			return nursingNoteLabelDtoList;
		}
		return nursingNoteLabelDtoList;
		//        else throw new HmisApplicationException("Cannot Map Empty ModelList To DtoList : " + title);
	}

	private void setDto(NursingNoteLabelDto nursingNoteLabelDto, NursingNoteLabel nursingNoteLabel) {
		if( nursingNoteLabel.getId() != null ) {
			nursingNoteLabelDto.setId(Optional.of(nursingNoteLabel.getId()));
		}
		if( nursingNoteLabel.getName() != null ) {
			nursingNoteLabelDto.setName(Optional.of(nursingNoteLabel.getName()));
		}
	}

	private NursingNoteLabel mapDtoToModel(NursingNoteLabelDto nursingNoteLabelDto) {
		if( nursingNoteLabelDto != null ) {
			NursingNoteLabel nursingNoteLabel = new NursingNoteLabel();
			setModel(nursingNoteLabelDto, nursingNoteLabel);
			return nursingNoteLabel;
		}
		else {
			throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
		}
	}


	private List< NursingNoteLabel > mapDtoListToModelList(List< NursingNoteLabelDto > nursingNoteLabelDtoList) {
		if( nursingNoteLabelDtoList != null && ! nursingNoteLabelDtoList.isEmpty() ) {
			List< NursingNoteLabel > nursingNoteLabelList = new ArrayList<>();
			nursingNoteLabelDtoList.forEach(nursingNoteLabelDto -> {
				NursingNoteLabel nursingNoteLabel = new NursingNoteLabel();
				setModel(nursingNoteLabelDto, nursingNoteLabel);
				nursingNoteLabelList.add(nursingNoteLabel);
			});
			return nursingNoteLabelList;
		}
		else {
			throw new HmisApplicationException("Cannot Map Empty DtoList to ModelList: " + title);
		}
	}

	private void setModel(NursingNoteLabelDto nursingNoteLabelDto, NursingNoteLabel nursingNoteLabel) {
		if( nursingNoteLabelDto.getId().isPresent() ) {
			nursingNoteLabel.setId(nursingNoteLabelDto.getId().get());
		}

		if( nursingNoteLabelDto.getName().isPresent() ) {
			nursingNoteLabel.setName(nursingNoteLabelDto.getName().get());
		}
	}
}
