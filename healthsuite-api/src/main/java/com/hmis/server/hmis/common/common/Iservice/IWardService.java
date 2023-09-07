package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.BedDto;
import com.hmis.server.hmis.common.common.dto.WardDto;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.exception.BadRequestException;
import com.hmis.server.hmis.common.exception.EntityNotFoundException;

import java.util.List;

public interface IWardService {

    WardDto addBedsToNewWard(WardDto dto);

    WardDto addBedsToWard(WardDto wardDto);

    List<WardDto> createInBatch(List<WardDto> wardDtoList);

    List<WardDto> findAll();

    WardDto findOne(Long wardId);

    WardDto findByCode(String code);

    List<WardDto> updateInBatch(List<WardDto> wardDtoList);

    WardDto addBedsToExistingWard(WardDto  dto);

    WardDto updateOne(WardDto wardDto);

    void activateOne(WardDto wardDto);

    void deactivateOne(WardDto wardDto);

    void activateInBatch(List<WardDto> wardDtoList);

    void deactivateInBatch(List<WardDto> wardDtoList);

    boolean isWardExist(Long wardDto);

    boolean isWardExistByDepartment(Department department);

    boolean isWardEmpty(WardDto wardDto);

    int findWardBedCount(long wardId);

    int findWardBedOccupiedCount(long wardId);

    int findBedUsagePercentage(long wardId);

	List<WardDto> searchWard(String search);

	Ward findOneRaw(Long wardId);

	boolean updateWardBedCount(WardDto wardDto);
}
