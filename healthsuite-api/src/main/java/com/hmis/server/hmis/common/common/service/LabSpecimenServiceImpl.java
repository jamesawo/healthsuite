package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.ILabSpecimenService;
import com.hmis.server.hmis.common.common.dto.LabSpecimenDto;
import com.hmis.server.hmis.common.common.model.LabSpecimen;
import com.hmis.server.hmis.common.common.repository.LabSpecimenRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LabSpecimenServiceImpl implements ILabSpecimenService {

    private static final String title = "Lab Specimen";

    @Autowired
    LabSpecimenRepository labSpecimenRepository;

    @Override
    public LabSpecimenDto createOne(LabSpecimenDto labSpecimenDto) {
        return mapModelToDto(labSpecimenRepository.save(mapDtoToModel(labSpecimenDto)));
    }

    @Override
    public void createInBatch(List<LabSpecimenDto> labSpecimenDtoList) {

    }

    @Override
    public List<LabSpecimenDto> findAll() {
        return mapModelListToDtoList(labSpecimenRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(LabSpecimen::getId)).collect(Collectors.toList()));
    }

    @Override
    public LabSpecimenDto findOne(LabSpecimenDto labSpecimenDto) {
        return null;
    }

    @Override
    public LabSpecimenDto findByName(LabSpecimenDto labSpecimenDto) {
        return null;
    }

    @Override
    public LabSpecimenDto findByCode(LabSpecimenDto labSpecimenDto) {
        return null;
    }

    @Override
    public List<LabSpecimenDto> findByNameLike(LabSpecimenDto labSpecimenDto) {
        return null;
    }

    @Override
    public LabSpecimenDto updateOne(LabSpecimenDto labSpecimenDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<LabSpecimenDto> labSpecimenDtoList) {

    }

    @Override
    public void deactivateOne(LabSpecimenDto labSpecimenDto) {

    }

    @Override
    public boolean isLabSpecimenExist(LabSpecimenDto labSpecimenDto) {
        if (labSpecimenDto.getName().isPresent()){
            return labSpecimenRepository.findAll().stream().anyMatch(labSpecimen ->
                    labSpecimen.getName().compareToIgnoreCase(labSpecimenDto.getName().get()) == 0);
        }else throw new HmisApplicationException("Provide Name Before Is Exist Check :" +title);
    }

    private LabSpecimenDto mapModelToDto(LabSpecimen labSpecimen) {
        if (labSpecimen != null) {
            LabSpecimenDto labSpecimenDto = new LabSpecimenDto();
            setDto(labSpecimenDto, labSpecimen);

            return labSpecimenDto;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
    }

    private List<LabSpecimenDto> mapModelListToDtoList(List<LabSpecimen> labSpecimenList) {
        List<LabSpecimenDto> labSpecimenDtoList = new ArrayList<>();
        if (labSpecimenList != null && !labSpecimenList.isEmpty()) {
            labSpecimenList.forEach(labSpecimen -> {
                LabSpecimenDto labSpecimenDto = new LabSpecimenDto();
                setDto(labSpecimenDto, labSpecimen);
                labSpecimenDtoList.add(labSpecimenDto);
            });
            return labSpecimenDtoList;
        }
        return labSpecimenDtoList;
//        else throw new HmisApplicationException("Cannot Map Empty ModelList To DtoList : " + title);
    }

    private void setDto(LabSpecimenDto labSpecimenDto, LabSpecimen labSpecimen) {
        if (labSpecimen.getId() != null)
            labSpecimenDto.setId(Optional.of(labSpecimen.getId()));
        if (labSpecimen.getName() != null)
            labSpecimenDto.setName(Optional.of(labSpecimen.getName()));
    }

    private LabSpecimen mapDtoToModel(LabSpecimenDto labSpecimenDto) {
        if (labSpecimenDto != null) {
            LabSpecimen labSpecimen = new LabSpecimen();
            setModel(labSpecimenDto, labSpecimen);
            return labSpecimen;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
    }


    private List<LabSpecimen> mapDtoListToModelList(List<LabSpecimenDto> labSpecimenDtoList) {
        if (labSpecimenDtoList != null && !labSpecimenDtoList.isEmpty()) {
            List<LabSpecimen> labSpecimenList = new ArrayList<>();
            labSpecimenDtoList.forEach(labSpecimenDto -> {
                LabSpecimen labSpecimen = new LabSpecimen();
                setModel(labSpecimenDto, labSpecimen);
                labSpecimenList.add(labSpecimen);
            });
            return labSpecimenList;
        } else throw new HmisApplicationException("Cannot Map Empty DtoList to ModelList: " + title);
    }

    private void setModel(LabSpecimenDto labSpecimenDto, LabSpecimen labSpecimen) {
        if (labSpecimenDto.getId().isPresent())
            labSpecimen.setId(labSpecimenDto.getId().get());

        if (labSpecimenDto.getName().isPresent())
            labSpecimen.setName(labSpecimenDto.getName().get());
    }

}
