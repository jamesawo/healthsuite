package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IDepartmentCategoryService;
import com.hmis.server.hmis.common.common.dto.DepartmentCategoryDto;
import com.hmis.server.hmis.common.common.model.DepartmentCategory;
import com.hmis.server.hmis.common.common.repository.DepartmentCategoryRepository;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentCategoryServiceImpl implements IDepartmentCategoryService {

    @Autowired
     private DepartmentCategoryRepository departmentCategoryRepository;

    @Override
    public DepartmentCategoryDto createOne(DepartmentCategoryDto departmentCategoryDto) {
        return null;
    }

    @Override
    public void createInBatch(List<DepartmentCategoryDto> departmentCategoryDtoList) {

    }

    @Override
    public List<DepartmentCategoryDto> findAll() {
        return mapModelListToDtoList(departmentCategoryRepository.findAll());
    }

    @Override
    public DepartmentCategoryDto findOne(DepartmentCategoryDto departmentCategoryDto) {
        return null;
    }

    @Override
    public Optional<DepartmentCategory> findByName(DepartmentCategoryDto departmentCategoryDto) {
        if (departmentCategoryDto.getName().isPresent())
            return departmentCategoryRepository.findByNameIgnoreCase(departmentCategoryDto.getName().get());
        else throw new HmisApplicationException("No Name Provided To Find Department Category");
    }

    @Override
    public DepartmentCategory findByName(String categoryName) {
        Optional<DepartmentCategory> category = this.departmentCategoryRepository.findByNameIgnoreCase(categoryName);
        if (!category.isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nothing found with category name "+ categoryName);
        }
        return category.get();
    }

    @Override
    public DepartmentCategoryDto findByCode(DepartmentCategoryDto departmentCategoryDto) {
        return null;
    }

    @Override
    public DepartmentCategoryDto updateOne(DepartmentCategoryDto departmentCategoryDto) {
        return null;
    }

    @Override
    public void updateInBatch(DepartmentCategoryDto departmentCategoryDto) {

    }

    @Override
    public void deactivateOne(DepartmentCategoryDto departmentCategoryDto) {

    }

    @Override
    public DepartmentCategory findOneDepartmentCategory(DepartmentCategoryDto departmentCategoryDto) {
        if (departmentCategoryDto.getId().isPresent())
            return departmentCategoryRepository.getOne(departmentCategoryDto.getId().get());
        throw new HmisApplicationException("Cannot find Department Category");
    }

    public DepartmentCategoryDto mapModelToDto(DepartmentCategory departmentCategory){
        DepartmentCategoryDto departmentCategoryDto = new DepartmentCategoryDto();
        if (departmentCategory != null)
            setDepartmentDto(departmentCategoryDto, departmentCategory);
        return departmentCategoryDto;
    }

    public DepartmentCategory mapDtoToModel(DepartmentCategoryDto departmentCategoryDto){
        DepartmentCategory departmentCategory = new DepartmentCategory();
        if (departmentCategoryDto.getId().isPresent())
            departmentCategory.setId(departmentCategoryDto.getId().get());
        if (departmentCategoryDto.getName().isPresent())
            departmentCategory.setName(departmentCategoryDto.getName().get());
        if (departmentCategoryDto.getCode().isPresent())
            departmentCategory.setCode(departmentCategoryDto.getCode().get());
        if (departmentCategoryDto.getDescription().isPresent())
            departmentCategory.setDescription(departmentCategoryDto.getDescription().get());
        return departmentCategory;
    }

    private List<DepartmentCategory> mapDtoListToModelList(List<DepartmentCategoryDto> departmentCategoryDtoListList){
        List<DepartmentCategory> departmentCategoryList = new ArrayList<>();
        for (DepartmentCategoryDto x : departmentCategoryDtoListList) {
            DepartmentCategory departmentCategory = new DepartmentCategory();
            if (x.getId().isPresent())
                departmentCategory.setId(x.getId().get());
            if (x.getCode().isPresent())
                departmentCategory.setCode(x.getCode().get());
            if (x.getDescription().isPresent())
                departmentCategory.setDescription(x.getDescription().get());
            departmentCategoryList.add(departmentCategory);
        }
        return departmentCategoryList;
    }

    private List<DepartmentCategoryDto> mapModelListToDtoList(List<DepartmentCategory> departmentCategoryList){
        List<DepartmentCategoryDto> departmentCategoryDtoList = new ArrayList<>();
        for (DepartmentCategory x : departmentCategoryList) {
            DepartmentCategoryDto departmentCategoryDto = new DepartmentCategoryDto();
            setDepartmentDto(departmentCategoryDto, x);
            departmentCategoryDtoList.add(departmentCategoryDto);
        }
        return departmentCategoryDtoList;
    }

    private void setDepartmentDto(DepartmentCategoryDto departmentCategoryDto, DepartmentCategory departmentCategory){
        if (departmentCategory.getId() != null)
            departmentCategoryDto.setId(Optional.of(departmentCategory.getId()));
        if (departmentCategory.getDescription() != null)
            departmentCategoryDto.setDescription(Optional.of(departmentCategory.getDescription()));
        if (departmentCategory.getCode() != null)
            departmentCategoryDto.setCode(Optional.of(departmentCategory.getCode()));
        if (departmentCategory.getName() != null)
            departmentCategoryDto.setName(Optional.of(departmentCategory.getName()));
    }
}
