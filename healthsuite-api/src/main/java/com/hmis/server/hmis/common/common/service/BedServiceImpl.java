package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IBedService;
import com.hmis.server.hmis.common.common.dto.BedDto;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.model.Bed;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.common.repository.BedRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BedServiceImpl implements IBedService {

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private CommonService commonService;


    @Override
    public boolean createBeds(int counts, Ward ward) {
        try{
            if (counts > 0){
                for (int i = 0; i < counts; i++) {
                    Bed bed = new Bed();
                    bed.setIsOccupied(false);
                    bed.setCode(this.generateBedCode());
                    bed.setWard(ward);
                    this.bedRepository.save(bed);
                }
            }
            return true;
        }catch (Exception e){
            //throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            return false;
        }
    }

    @Override
    public BedDto findById(Long id) {
        return null;
    }

    @Override
    public BedDto findByCode(String code) {
        return null;
    }

    @Override
    public List<BedDto> findAllOccupiedBedByWard(Long wardId) {
        return null;
    }

    @Override
    public List<BedDto> findAllBedByWard(Long wardId) {
        return null;
    }

    @Override
    public BedDto updateOne(BedDto bedDto) {
        return null;
    }

    @Override
    public void deactivateOne(BedDto bedDto) {

    }

	@Override
	public Bed findOneRaw(Long bedId) {
        Optional<Bed> bed = this.bedRepository.findById(bedId);
        if (!bed.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bed not found, Id: "+ bedId);
        }
        return bed.get();
	}

	@Override
	public void updateBedIsOccupiedStatus(Long bedId, boolean status) {
		this.bedRepository.updateBedIsOccupiedStatus(bedId, status, LocalDate.now());
	}

	@Override
	public boolean addBedCountToWard(Ward wardId, int numberOfBeds) {
        return this.createBeds(numberOfBeds, wardId);
	}

    @Override
    public boolean subtractBedCountFromWard(Ward ward, int numberOfBedsToRemove) {
        try{
            List<Bed> unUsedBeds = this.bedRepository.findAllByWardAndIsOccupied(ward, false);
            if (unUsedBeds.size() >= numberOfBedsToRemove){
                Collections.reverse(unUsedBeds);
                List<Bed> beds = unUsedBeds.stream().limit(numberOfBedsToRemove).collect(Collectors.toList());
                this.bedRepository.deleteAll(beds);
                return true;
            }else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There is not enough unoccupied beds to remove");
            }
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }


    }

    @Override
    public int countUnusedBed() {
        return 0;
    }

    @Override
    public List<Bed> findAllEmptyBeds() {
        return null;
    }

    public List<Bed> mapDtoListToModelList(List<BedDto> bedDtoList) {
        List<Bed> bedList = new ArrayList<>();
        if (!bedDtoList.isEmpty()) {
            bedDtoList.forEach(bedDto -> {
                Bed bed = new Bed();
                mapToModel(bedDto, bed);
                bedList.add(bed);
            });
        }
        return bedList;
    }

    public List<BedDto> mapModelListToDtoList(List<Bed> bedList) {
        List<BedDto> bedDtoList = new ArrayList<>();
        if (!bedList.isEmpty()) {
            bedList.forEach(bed -> {
                BedDto bedDto = new BedDto();
                mapToDto(bed, bedDto);
                bedDtoList.add(bedDto);
            });
        }
        return bedDtoList;
    }

    public void deAllocateBed(Bed bed) {
        bed.setIsOccupied(false);
        bed.setLastDeallocateDate(LocalDate.now());
        this.bedRepository.save(bed);
    }

    private void mapToModel(BedDto bedDto, Bed bed) {

        if (bedDto.getId() != null) bed.setId(bedDto.getId());

        if (bedDto.getIsOccupied() != null ) bed.setIsOccupied(bedDto.getIsOccupied());

        if (bedDto.getCode() != null ) bed.setCode(bedDto.getCode());

        if (bedDto.getCurrentState() != null ) bed.setState(bedDto.getCurrentState());
    }


    private void mapToDto(Bed bed, BedDto bedDto) {

        if (bed.getId() != null)
            bedDto.setId(bed.getId());
        if (bed.getCode() != null)
            bedDto.setCode(bed.getCode());
        if (bed.getIsOccupied() != null)
            bedDto.setIsOccupied(bed.getIsOccupied());
        if (bed.getState() != null)
            bedDto.setCurrentState(bed.getState());

    }

    private String generateBedCode() {
        GenerateCodeDto codeDto = new GenerateCodeDto();
        codeDto.setDefaultPrefix(HmisCodeDefaults.BED_CODE_DEFAULT_PREFIX);
        codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.BED_CODE_PREFIX));
        codeDto.setLastGeneratedCode(bedRepository.findTopByOrderByIdDesc().map(Bed::getCode));
        return commonService.generateDataCode(codeDto);
    }

    private Optional<String> generateBedCodeWithWardCode(String wardCode) {
        List<String> strings = Arrays.asList(wardCode, generateBedCode());
        return Optional.of(String.join(HmisCodeDefaults.DELIMITER, strings));
    }

    //todo:: create util for incrementing entity code (BED or WARD) without saving first
}
