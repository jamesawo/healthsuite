package com.hmis.server.hmis.common.common.Iservice;

import com.hmis.server.hmis.common.common.dto.BedDto;
import com.hmis.server.hmis.common.common.dto.WardDto;
import com.hmis.server.hmis.common.common.model.Bed;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.exception.BadRequestException;

import java.util.List;

public interface IBedService {

    boolean createBeds(int counts, Ward ward);

    BedDto findById(Long id);

    BedDto findByCode(String code);

    List<BedDto> findAllOccupiedBedByWard(Long wardId);

    List<BedDto> findAllBedByWard(Long wardId);

    BedDto updateOne(BedDto bedDto);

    void deactivateOne(BedDto bedDto);

	Bed findOneRaw(Long bedId);

	void updateBedIsOccupiedStatus(Long bedId, boolean b);

	boolean addBedCountToWard(Ward ward, int numberOfBeds);

	boolean subtractBedCountFromWard(Ward ward, int numberOfBeds);

	int countUnusedBed();

	List<Bed> findAllEmptyBeds();
}
