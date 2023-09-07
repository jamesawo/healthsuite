package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IGenderService;
import com.hmis.server.hmis.common.common.dto.GenderDto;
import com.hmis.server.hmis.common.common.model.Gender;
import com.hmis.server.hmis.common.common.repository.GenderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GenderServiceImpl implements IGenderService {

    @Autowired
    GenderRepository genderRepository;

    @Override
    public GenderDto createOne(GenderDto genderDto) {
        return mapModelToDto( genderRepository.save( mapDtoToModel(genderDto) ) );
    }

    @Override
    public void createInBatch(List<GenderDto> genderDtoList) {
    }

    @Override
    public List<GenderDto> findAll() {
        return mapModelListToDtoList( genderRepository.findAll() );
    }

    @Override
    public GenderDto findOne(GenderDto genderDto) {
        return null;
    }

    @Override
    public Gender findOneRaw(Long id){
        Optional<Gender> optional = this.genderRepository.findById(id);
        if (!optional.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Gender Not Found");
        }
        return optional.get();
    }

    @Override
    public Gender findByName(GenderDto genderDto) {
        if (genderDto.getName().isPresent()){
            Gender gender = genderRepository.findByName(genderDto.getName().get());
            if (gender != null){
                return gender;
            }
        }
        return new Gender();
    }

    @Override
    public GenderDto updateOne(GenderDto genderDto) {
	    if( ! genderDto.getId().isPresent() && ! genderDto.getName().isPresent() ) {
		    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Gender Identity Is Required");
	    }
	    this.genderRepository.save(new Gender(genderDto.getId().get(), genderDto.getName().get()));
	    return genderDto;
    }

    @Override
    public void updateInBatch(List<GenderDto> genderDtoList) {

    }

    @Override
    public void deactivateOne(GenderDto genderDto) {

    }

    @Override
    public boolean isGenderExist(GenderDto genderDto) {
       List<Gender> genders = genderRepository.findAll();
       boolean flag = false;

       if (genderDto.getName().isPresent()){
           for (Gender gender: genders){
               if (gender.getName().compareToIgnoreCase(genderDto.getName().get() ) == 0){
                   flag = true;
               }
           }
       }
        return flag;
    }

    private Gender mapDtoToModel(GenderDto genderDto) {
        Gender gender = new Gender();

        if (genderDto.getName().isPresent())
            gender.setName(genderDto.getName().get());
        if (genderDto.getId().isPresent())
            gender.setId(genderDto.getId().get());
        return gender;
    }

    private GenderDto mapModelToDto(Gender gender) {
        GenderDto genderDto = new GenderDto();
        genderDto.setName(Optional.ofNullable(gender.getName()));
        genderDto.setId(Optional.ofNullable(gender.getId()));
        return genderDto;
    }

    private List<Gender> mapDtoListToModelList(List<GenderDto> genderDtoList) {
        List<Gender> genderList = new ArrayList<>();
        if (!genderDtoList.isEmpty()){
            for(GenderDto genderDto : genderDtoList){
                Gender gender = new Gender();
                gender.setId(genderDto.getId().get());
                gender.setName(genderDto.getName().get());
                genderList.add(gender);
            }
        }
        return genderList;
    }

    private List<GenderDto> mapModelListToDtoList(List<Gender> genders) {
        List<GenderDto> genderDtoList = new ArrayList<>();
        if (!genders.isEmpty()){
            for(Gender gender : genders){
                GenderDto genderDto = new GenderDto();
                genderDto.setId(Optional.ofNullable(gender.getId()));
                genderDto.setName(Optional.ofNullable(gender.getName()));
                genderDtoList.add(genderDto);
            }
        }
        return genderDtoList;
    }

}
