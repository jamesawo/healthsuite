package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.NursingNoteLabelDto;
import com.hmis.server.hmis.common.common.model.NursingNoteLabel;
import java.util.List;

public interface INursingNoteLabelService {
    NursingNoteLabelDto createOne(NursingNoteLabelDto nursingNoteLabelDto);

    void createInBatch(List<NursingNoteLabelDto> nursingNoteLabelDtoList);

	NursingNoteLabel findOneRaw(Long id);

	List< NursingNoteLabelDto > findAll();

    NursingNoteLabelDto findOne(NursingNoteLabelDto nursingNoteLabelDto);

    NursingNoteLabelDto findByName(NursingNoteLabelDto nursingNoteLabelDto);

    NursingNoteLabelDto findByCode(NursingNoteLabelDto nursingNoteLabelDto);

    List<NursingNoteLabelDto> findByNameLike(NursingNoteLabelDto nursingNoteLabelDto);

    NursingNoteLabelDto updateOne(NursingNoteLabelDto nursingNoteLabelDto);

    void updateInBatch(List<NursingNoteLabelDto> nursingNoteLabelDtoList);

    void deactivateOne(NursingNoteLabelDto nursingNoteLabelDto);

    boolean isNursingNoteLabelExist(NursingNoteLabelDto nursingNoteLabelDto);
}
