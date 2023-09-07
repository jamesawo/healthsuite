package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.INationalityService;
import com.hmis.server.hmis.common.common.dto.NationalityDto;
import com.hmis.server.hmis.common.common.model.Nationality;
import com.hmis.server.hmis.common.common.repository.NationalityRepository;
import com.hmis.server.hmis.common.exception.HmisApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NationalityServiceImpl implements INationalityService {

    private final NationalityRepository nationalityRepository;

    @Autowired
    public NationalityServiceImpl(NationalityRepository nationalityRepository) {
        this.nationalityRepository = nationalityRepository;
    }

    @Override
    public Nationality createNationality(NationalityDto nationalityDto) {
        return null;
    }

    @Override
    public Nationality updateNationality(NationalityDto nationalityDto) {
        return null;
    }

    @Override
    public Nationality createParent(String parentName) {
        return this.nationalityRepository.save(new Nationality(parentName));
    }

    @Override
    public List<Nationality> updateChildren(List<String> children, Long parentId) {
        List<Nationality> list = new ArrayList<>();
        children.forEach(child -> list.add(new Nationality(child)));

        return this.nationalityRepository.saveAll(list);
    }

    @Override
    public List<Nationality> findAllNationality() {
        return this.nationalityRepository.findAll();
    }

    @Override
    public List<Nationality> findAllByParent(Long parentId) {
        return this.nationalityRepository.findAllByParent(new Nationality(parentId));
    }

    @Override
    public List<Nationality> findParentOnly() {
        return nationalityRepository.findAllByParentNull();
    }

    @Override
    public Nationality createParentAndChildren(NationalityDto nationalityDto) {
        Nationality nationality = this.nationalityRepository.save(new Nationality(nationalityDto.getParent()));
        try {
            List<Nationality> children = new ArrayList<>();
            for (String child : nationalityDto.getChildrenString()) {
                Nationality nationality1 = new Nationality();
                nationality1.setParent(nationality);
                nationality1.setName(child);
                Nationality savedChild = this.nationalityRepository.save(nationality1);
                children.add(savedChild);
            }
            nationality.setChildren(children);
            return this.nationalityRepository.save(nationality);
        } catch (Exception e) {
            this.nationalityRepository.delete(nationality);
            throw new HmisApplicationException(e.getMessage());
        }


    }

    @Override
    public List<Nationality> findAllGroupByParent() {
        return null;
    }

    public Nationality findNationalityById(Long id) {
        Optional<Nationality> optional = this.nationalityRepository.findById(id);
        if (!optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return optional.get();
    }

    public NationalityDto getParentByChildId(Long childId) {
        Nationality nationalityById = this.findNationalityById(childId);
        if (nationalityById.getParent() == null) {
            return new NationalityDto();
        }
        return this.mapToDto(nationalityById.getParent());
    }

    public NationalityDto mapToDto(Nationality model) {
        NationalityDto dto = new NationalityDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        if (model.getParent() != null) {
            dto.setParentDto(new NationalityDto(model.getId(), model.getParent().getName()));
        }
        if (!model.getChildren().isEmpty()) {
            List<NationalityDto> children = new ArrayList<>();
            for (Nationality child : model.getChildren()) {
                children.add(new NationalityDto( child.getId(), child.getName() ));
            }
            dto.setChildrenDto(children);
        }
        return dto;
    }


}
