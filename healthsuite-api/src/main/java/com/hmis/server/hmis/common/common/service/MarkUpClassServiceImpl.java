package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IMarkUpClassService;
import com.hmis.server.hmis.common.common.dto.MarkUpClassDto;
import com.hmis.server.hmis.common.common.repository.MarkUpClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkUpClassServiceImpl implements IMarkUpClassService {

    @Autowired
    MarkUpClassRepository markUpClassRepository;

    @Override
    public MarkUpClassDto createOne(MarkUpClassDto markUpClassDto) {
        return null;
    }

    @Override
    public void createInBatch(List<MarkUpClassDto> markUpClassDtoList) {

    }

    @Override
    public List<MarkUpClassDto> findAll() {
        return null;
    }

    @Override
    public MarkUpClassDto findOne(MarkUpClassDto markUpClassDto) {
        return null;
    }

    @Override
    public MarkUpClassDto findByName(MarkUpClassDto markUpClassDto) {
        return null;
    }

    @Override
    public List<MarkUpClassDto> findByNameLike(MarkUpClassDto markUpClassDto) {
        return null;
    }

    @Override
    public MarkUpClassDto findByCode(MarkUpClassDto markUpClassDto) {
        return null;
    }

    @Override
    public MarkUpClassDto updateOne(MarkUpClassDto markUpClassDto) {
        return null;
    }

    @Override
    public void updateInBatch(List<MarkUpClassDto> markUpClassDtoList) {

    }

    @Override
    public void deactivateOne(MarkUpClassDto markUpClassDto) {

    }
}
