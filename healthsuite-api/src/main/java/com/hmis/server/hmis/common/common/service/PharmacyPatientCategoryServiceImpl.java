package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IPharmacyPatientCategoryService;
import com.hmis.server.hmis.common.common.dto.PharmacyPatientCategoryDto;
import com.hmis.server.hmis.common.common.model.PharmacyPatientCategory;
import com.hmis.server.hmis.common.common.repository.PharmacyPatientCategoryRepository;
import com.hmis.server.hmis.common.constant.HmisExceptionMessage;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PharmacyPatientCategoryServiceImpl implements IPharmacyPatientCategoryService {

    private static final String title = "Pharmacy Patient Category";

    @Autowired
    PaymentMethodServiceImpl paymentMethodService;

    @Autowired
    PharmacyPatientCategoryTypeServiceImpl pharmacyPatientCategoryTypeService;

    @Autowired
    PharmacyPatientCategoryRepository pharmacyPatientCategoryRepository;

    @Override
    public PharmacyPatientCategoryDto createOne(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return mapModelToDto(pharmacyPatientCategoryRepository.save(mapDtoToModel(pharmacyPatientCategoryDto)));
    }

    @Override
    public List<PharmacyPatientCategoryDto> createInBatch(List<PharmacyPatientCategoryDto> pharmacyPatientCategoryDtoList) {
        return null;
    }

    @Override
    public List<PharmacyPatientCategoryDto> findAll() {
        return mapModelListToDtoList(pharmacyPatientCategoryRepository.findAll());
    }

    @Override
    public List<PharmacyPatientCategoryDto> findByPaymentMethod(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return null;
    }

    @Override
    public List<PharmacyPatientCategoryDto> findByPharmacyPatientCategoryType(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return null;
    }

    @Override
    public List<PharmacyPatientCategoryDto> findByNameLike(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return null;
    }

    @Override
    public PharmacyPatientCategoryDto findByName(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return null;
    }

    @Override
    public PharmacyPatientCategoryDto findByCode(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return null;
    }

    @Override
    public PharmacyPatientCategoryDto updateOne(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<PharmacyPatientCategoryDto> pharmacyPatientCategoryDtoList) {

    }

    @Override
    public void deactivateOne(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
    }

    @Override
    public boolean isPharmacyPatientCategoryExist(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        if (pharmacyPatientCategoryDto.getName().isPresent()) {
            boolean flag = false;
            for (PharmacyPatientCategory pharmacyPatientCategory : pharmacyPatientCategoryRepository.findAll()) {
                if (pharmacyPatientCategory.getName().compareToIgnoreCase(pharmacyPatientCategoryDto.getName().get()) == 0) {
                    if (pharmacyPatientCategoryDto.getPharmacyPatientCategoryType().isPresent()) {
                        Long id = pharmacyPatientCategory.getPharmacyPatientCategoryType().getId();
                        Long id1 = pharmacyPatientCategoryDto.getPharmacyPatientCategoryType().get().getId().get();
                        if (id.equals(id1)) {
                            flag = true;
                        }
                    }
                }
            }
            return flag;
        }
        throw new HmisApplicationException(HmisExceptionMessage.IS_EXIST_CHECK_ERROR + title);

    }

    private PharmacyPatientCategory mapDtoToModel(PharmacyPatientCategoryDto pharmacyPatientCategoryDto) {
        if (pharmacyPatientCategoryDto != null) {
            PharmacyPatientCategory pharmacyPatientCategory = new PharmacyPatientCategory();
            setModel(pharmacyPatientCategoryDto, pharmacyPatientCategory);
            return pharmacyPatientCategory;
        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_DTO_TO_MODEL_ERROR + title);
    }

    private void setModel(PharmacyPatientCategoryDto pharmacyPatientCategoryDto, PharmacyPatientCategory pharmacyPatientCategory) {

        if (pharmacyPatientCategoryDto.getId().isPresent())
            pharmacyPatientCategory.setId(pharmacyPatientCategoryDto.getId().get());

        if (pharmacyPatientCategoryDto.getName().isPresent())
            pharmacyPatientCategory.setName(pharmacyPatientCategoryDto.getName().get());

        if (pharmacyPatientCategoryDto.getPaymentMethod().isPresent())
            pharmacyPatientCategory.setPaymentMethod(paymentMethodService.mapDtoToModel(
                    pharmacyPatientCategoryDto.getPaymentMethod().get()));

        if (pharmacyPatientCategoryDto.getPharmacyPatientCategoryType().isPresent()) {
            pharmacyPatientCategory.setPharmacyPatientCategoryType(pharmacyPatientCategoryTypeService.mapDtoToModel(
                    pharmacyPatientCategoryDto.getPharmacyPatientCategoryType().get()));
        }
    }

    private PharmacyPatientCategoryDto mapModelToDto(PharmacyPatientCategory pharmacyPatientCategory) {
        if (pharmacyPatientCategory != null) {
            PharmacyPatientCategoryDto pharmacyPatientCategoryDto = new PharmacyPatientCategoryDto();
            setDto(pharmacyPatientCategoryDto, pharmacyPatientCategory);
            return pharmacyPatientCategoryDto;

        } else throw new HmisApplicationException(HmisExceptionMessage.MAP_MODEL_TO_DTO_ERROR + title);
    }

    private List<PharmacyPatientCategory> mapDtoListToModelList(List<PharmacyPatientCategoryDto> pharmacyPatientCategoryDtoList) {
        if (pharmacyPatientCategoryDtoList != null && !pharmacyPatientCategoryDtoList.isEmpty()) {
            List<PharmacyPatientCategory> pharmacyPatientCategoryList = new ArrayList<>();
            pharmacyPatientCategoryDtoList.forEach(pharmacyPatientCategoryDto -> {
                PharmacyPatientCategory pharmacyPatientCategory = new PharmacyPatientCategory();
                setModel(pharmacyPatientCategoryDto, pharmacyPatientCategory);
                pharmacyPatientCategoryList.add(pharmacyPatientCategory);
            });
            return pharmacyPatientCategoryList;
        } else throw new HmisApplicationException("Cannot Map Empty Dto List To Model: " + title);
    }

    private List<PharmacyPatientCategoryDto> mapModelListToDtoList(List<PharmacyPatientCategory> pharmacyPatientCategoryList) {
        List<PharmacyPatientCategoryDto> pharmacyPatientCategoryDtoList = new ArrayList<>();

        if (pharmacyPatientCategoryList != null && !pharmacyPatientCategoryList.isEmpty()) {
            pharmacyPatientCategoryList.forEach(pharmacyPatientCategory -> {
                PharmacyPatientCategoryDto pharmacyPatientCategoryDto = new PharmacyPatientCategoryDto();
                setDto(pharmacyPatientCategoryDto, pharmacyPatientCategory);
                pharmacyPatientCategoryDtoList.add(pharmacyPatientCategoryDto);
            });
            return pharmacyPatientCategoryDtoList;
        }
//        else throw new HmisApplicationException("Cannot Map Empty Model List To Dto List" + title);
        return pharmacyPatientCategoryDtoList;
    }

    private void setDto(PharmacyPatientCategoryDto pharmacyPatientCategoryDto, PharmacyPatientCategory pharmacyPatientCategory) {
        if (pharmacyPatientCategory.getId() != null)
            pharmacyPatientCategoryDto.setId(Optional.of(pharmacyPatientCategory.getId()));

        if (pharmacyPatientCategory.getName() != null)
            pharmacyPatientCategoryDto.setName(Optional.of(pharmacyPatientCategory.getName()));

        if (pharmacyPatientCategory.getPharmacyPatientCategoryType() != null) {
            pharmacyPatientCategoryDto.setPharmacyPatientCategoryType(
                    Optional.ofNullable(pharmacyPatientCategoryTypeService
                            .mapModelToDto(pharmacyPatientCategory.getPharmacyPatientCategoryType())));
        }

        if (pharmacyPatientCategory.getPaymentMethod() != null) {
            pharmacyPatientCategoryDto.setPaymentMethod(
                    Optional.of(paymentMethodService.mapModelToDto(pharmacyPatientCategory
                            .getPaymentMethod())));
        }

    }
}
