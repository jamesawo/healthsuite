package com.hmis.server.hmis.common.common.service;

import com.hmis.server.hmis.common.common.Iservice.IWardService;
import com.hmis.server.hmis.common.common.dto.DepartmentDto;
import com.hmis.server.hmis.common.common.dto.GenerateCodeDto;
import com.hmis.server.hmis.common.common.dto.WardDto;
import com.hmis.server.hmis.common.common.dto.WardUpdateTypeEnum;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.model.Ward;
import com.hmis.server.hmis.common.common.repository.WardRepository;
import com.hmis.server.hmis.common.constant.HmisCodeDefaults;
import com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WardServiceImpl implements IWardService {

	@Autowired
	WardRepository wardRepository;

	@Autowired
	BedServiceImpl bedService;

	@Autowired
	CommonService commonService;

	@Autowired
	DepartmentServiceImpl departmentService;


	@Override
	public WardDto addBedsToWard(WardDto dto) {

		if( dto.getId() != null ) {
			return this.addBedsToExistingWard(dto);
		}
		else {
			return this.addBedsToNewWard(dto);
		}


	}

	@Override
	public WardDto addBedsToNewWard(WardDto dto) {

		Ward ward = new Ward();

		dto.setCode(this.generateWardCode());
		this.setWard(dto, ward);
		Ward saved = this.wardRepository.save(ward);

		this.bedService.createBeds(dto.getTotalBedCount(), saved);
		return this.mapToDto(saved);
	}

	@Override
	public WardDto addBedsToExistingWard(WardDto dto) {
		this.bedService.createBeds(dto.getTotalBedCount(), new Ward(dto.getId()));
		return dto;
	}


	@Override
	public List< WardDto > createInBatch(List< WardDto > wardDtoList) {
		return null;
	}

	@Override
	public List< WardDto > findAll() {
		List< WardDto > wardDots = new ArrayList<>();
		List< Ward > wardList = this.wardRepository.findAll();
		if( wardList.size() > 0 ) {
			List< WardDto > list = new ArrayList<>();
			for( Ward ward : wardList ) {
				WardDto wardDto = mapToDto(ward);
				list.add(wardDto);
			}
			wardDots = list;
			//			wardDots = wardList.stream().map(this::mapToDto).collect(Collectors.toList());
		}
		return wardDots;
	}

	@Override
	public WardDto findOne(Long id) {
		Optional< Ward > ward = this.wardRepository.findById(id);
		if( ! ward.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ward Not Found With ID: " + id);
		}
		return this.mapToDto(ward.get());
	}

	@Override
	public WardDto findByCode(String code) {
		Optional< Ward > ward = this.wardRepository.findByCode(code);
		if( ! ward.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ward Not Found With Code: " + code);
		}
		return this.mapToDto(ward.get());
	}

	@Override
	public List< WardDto > updateInBatch(List< WardDto > wardDtoList) {
		return null;
	}


	@Override
	public WardDto updateOne(WardDto wardDto) {
		if( wardDto.getId() != null ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide Ward ID");
		}
		Ward ward = new Ward();
		this.setWard(wardDto, ward);
		Ward save = this.wardRepository.save(ward);
		return this.mapToDto(save);
	}

	@Override
	public void activateOne(WardDto wardDto) {

	}

	@Override
	public void deactivateOne(WardDto wardDto) {

	}

	@Override
	public void activateInBatch(List< WardDto > wardDtoList) {

	}

	@Override
	public void deactivateInBatch(List< WardDto > wardDtoList) {

	}

	@Override
	public boolean isWardExist(Long wardId) {
		Optional< Ward > byId = this.wardRepository.findById(wardId);
		return byId.isPresent();
	}

	@Override
	public boolean isWardExistByDepartment(Department department) {
		Optional< Ward > byDepartment = this.wardRepository.findByDepartment(department);
		return byDepartment.isPresent();
	}

	@Override
	public boolean isWardEmpty(WardDto wardDto) {
		return false;
	}

	@Override
	public int findWardBedCount(long wardId) {
		return 0;
	}

	@Override
	public int findWardBedOccupiedCount(long wardId) {
		return 0;
	}

	@Override
	public int findBedUsagePercentage(long wardId) {
		double totalNumberOfBeds = findWardBedCount(wardId);
		double occupiedBedCount = findWardBedOccupiedCount(wardId);
		double percentage = occupiedBedCount * 100 / totalNumberOfBeds;
		return ( int ) percentage;
	}

	@Override
	public List< WardDto > searchWard(String search) {
		List< WardDto > wardDtoList = new ArrayList<>();
		List< Ward > wards = this.wardRepository.findAllByDepartmentNameContainsIgnoreCaseOrCodeContainsIgnoreCase(search, search);
		if( wards.size() > 0 ) {
			wardDtoList = wards.stream().map(this::mapToDto).collect(Collectors.toList());
		}
		return wardDtoList;
	}

	@Override
	public Ward findOneRaw(Long wardId) {
		Optional< Ward > ward = this.wardRepository.findById(wardId);
		if( ! ward.isPresent() ) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ward not found, ID: " + wardId);
		}
		return ward.get();
	}

	@Override
	public boolean updateWardBedCount(WardDto wardDto) {
		if( wardDto.getId() == null || wardDto.getType() == null ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide Ward & Type Before Updating Beds");
		}
		else if( wardDto.getNumberOfBedsToUpdate() == 0 ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of beds to update cannot be 0");
		}

		try {
			if( wardDto.getType() == WardUpdateTypeEnum.ADD_BED ) {
				return this.addBedCountToWard(wardDto.getId(), wardDto.getNumberOfBedsToUpdate());
			}
			else if( wardDto.getType() == WardUpdateTypeEnum.REMOVE_BED ) {
				return this.subtractBedCountFromWard(wardDto.getId(), wardDto.getNumberOfBedsToUpdate());
			}
			else {
				return false;
			}
		}
		catch( Exception e ) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	public WardDto mapToDto(Ward ward) {

		WardDto wardDto = new WardDto();

		double occupiedBedCount = 0;
		double totalNumberOfBeds = 0;


		if( ward.getId() != null ) {
			wardDto.setId(ward.getId());
		}
		if( ward.getCode() != null ) {
			wardDto.setCode(ward.getCode());
		}

		if( ward.getBeds() != null && ward.getBeds().size() > 0 ) {

			totalNumberOfBeds = ward.getBeds().size();

			wardDto.setBeds(this.bedService.mapModelListToDtoList(ward.getBeds()));
			wardDto.setTotalBedCount(ward.getBeds().size());
		}


		if( ward.getOccupiedCount() > 0 ) {
			occupiedBedCount = ward.getOccupiedCount();
			wardDto.setOccupiedBedCount(ward.getOccupiedCount());
		}


		if( ward.getDepartment() != null ) {
			wardDto.setTitle(ward.getDepartment().getName() + " [" + ward.getCode() + "]");
			wardDto.setDepartmentId(ward.getDepartment().getId());
			wardDto.setDepartment(new DepartmentDto(ward.getDepartment().getId(), ward.getDepartment().getName()));
		}


		double percentage = occupiedBedCount * 100 / totalNumberOfBeds;
		wardDto.setPercentageCount(( int ) percentage);
		return wardDto;
	}

	private boolean addBedCountToWard(Long wardId, int numberOfBeds) {
		Ward oneRaw = this.findOneRaw(wardId);
		return this.bedService.addBedCountToWard(oneRaw, numberOfBeds);
	}

	private boolean subtractBedCountFromWard(Long wardId, int numberOfBeds) {
		Ward oneRaw = this.findOneRaw(wardId);
		return this.bedService.subtractBedCountFromWard(oneRaw, numberOfBeds);
	}

	private void setWard(WardDto dto, Ward ward) {
		Department department;

		if( dto.getId() != null ) {
			ward.setId(dto.getId());
		}
		if( dto.getCode() != null ) {
			ward.setCode(dto.getCode());
		}

		if( dto.getOccupiedBedCount() > 0 ) {
			ward.setOccupiedCount(dto.getOccupiedBedCount());
		}

		if( dto.getBeds() != null && dto.getBeds().size() > 0 ) {
			ward.setBeds(this.bedService.mapDtoListToModelList(dto.getBeds()));
		}

		if( dto.getDepartmentId() != null ) {
			department = this.departmentService.findOneRaw(Optional.of(dto.getDepartmentId()));
			ward.setDepartment(department);

		}
		else if( dto.getDepartment() != null && dto.getDepartment().getId().isPresent() ) {
			department = this.departmentService.findOneRaw(Optional.of(dto.getDepartment().getId().get()));
			ward.setDepartment(department);
		}
	}

	private String generateWardCode() {
		GenerateCodeDto codeDto = new GenerateCodeDto();
		codeDto.setGlobalSettingKey(Optional.of(HmisGlobalSettingKeys.WARD_CODE_PREFIX));
		codeDto.setDefaultPrefix(HmisCodeDefaults.WARD_CODE_DEFAULT);
		codeDto.setLastGeneratedCode(wardRepository.findTopByOrderByIdDesc().map(Ward::getCode));
		return commonService.generateDataCode(codeDto);
	}

}

/*

    @Autowired
    WardBedsRepository wardRepository;

    @Autowired
    CommonService commonService;

    @Override
    public WardDto createOne(WardDto wardDto) {
        if (!wardDto.getId().isPresent())
            wardDto.setCode(Optional.of(generateWardCode()));

        return mapModelToDto(wardRepository.save(mapDtoToModel(wardDto)));
    }

    @Override
    public List<WardDto> createInBatch(List<WardDto> wardDtoList) {
        return null;
    }

    @Override
    public List<WardDto> findAll() {
        return mapModelListToDtoList(wardRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Ward::getCode))
                .collect(Collectors.toList()));
        return null;
    }

    @Override
    public List<WardDto> findByNameLike(WardDto wardDto) {
        return null;
    }

    @Override
    public WardDto findOne(WardDto wardDto) throws BadRequestException {
        if (wardDto.getId().isPresent()) {
            return mapModelToDto(wardRepository
                    .getOne(wardDto.getId().get()));
        } else throw new BadRequestException(HmisConstant.STATUS_400, "Cannot Find Ward");
	    return null;
    }

    @Override
    public WardDto findByCode(WardDto wardDto) {
        return null;
    }

    @Override
    public List<WardDto> updateInBatch(List<WardDto> wardDtoList) {
        return null;
    }

    @Override
    public WardDto updateOne(WardDto wardDto) {
        if (wardDto.getId().isPresent()) {
            Optional<Ward> optionalWard = wardRepository.findById(wardDto.getId().get());
            if (optionalWard.isPresent()) {
                return mapModelToDto(wardRepository
                        .save(setModelFromDtoToUpdate(optionalWard.get(), wardDto)));
            } else throw new HmisApplicationException("Cannot Find Ward.");

        } else throw new HmisApplicationException("Provide Ward ID Before Updating.");
	    return null;
    }

    @Override
    public void activateOne(WardDto wardDto) {

    }

    @Override
    public void deactivateOne(WardDto wardDto) {

    }

    @Override
    public void activateInBatch(List<WardDto> wardDtoList) {

    }

    @Override
    public void deactivateInBatch(List<WardDto> wardDtoList) {

    }

    @Override
    public boolean isWardExist(WardDto wardDto) throws BadRequestException {
        if (wardDto.getName().isPresent())
            return wardRepository.findAll()
                    .stream()
                    .anyMatch(ward -> ward.getName().compareToIgnoreCase(wardDto.getName().get()) == 0);
        else throw new BadRequestException(HmisConstant.STATUS_400, "Provide Ward Name");
        return  false;
    }

    @Override
    public boolean isWardEmpty(WardDto wardDto) throws EntityNotFoundException, BadRequestException {
        if (wardDto.getId().isPresent()) {
            Optional<Ward> ward = wardRepository.findById(wardDto.getId().get());
            if (ward.isPresent()) {
                return ward.get().getBeds().size() > 0;
            } else throw new EntityNotFoundException(HmisConstant.STATUS_404, "Cannot Find Ward");
        } else throw new BadRequestException(HmisConstant.STATUS_404, "Provide Ward Before Is Empty Check");
        return false;
    }

    @Override
    public int findWardBedCount(long wardId) {
        int count = 0;

        Ward ward = wardRepository.getOne(wardId);
        if (ward != null && ward.getBeds() != null) {
            count = ward.getBeds().size();
        }
        return count;

	    return count;

    }

    @Override
    public int findWardBedOccupiedCount(long wardId) {

        Ward ward = wardRepository.getOne(wardId);
        if (ward != null && ward.getBeds() != null) {
            return (int) ward.getBeds().stream().filter(Bed::getIsOccupied).count();
        }
        return 0;

    }




    public WardDto mapModelToDto(WardBeds ward) {
        WardDto wardDto = new WardDto();
        if (ward.getId() != null)
            wardDto.setId(Optional.of(ward.getId()));
        if (ward.getName() != null)
            wardDto.setName(Optional.of(ward.getName()));
        if (ward.getCode() != null) {
            wardDto.setCode(Optional.of(ward.getCode()));
        }
        wardDto.setCurrentBedCount(findWardBedCount(ward.getId()));
        wardDto.setOccupiedCount(findWardBedOccupiedCount(ward.getId()));
        wardDto.setPercentageCount(findBedUsagePercentage(ward.getId()));
        return wardDto;
    }

    public WardBeds mapDtoToModel(WardDto wardDto) {
	    WardBeds ward = new WardBeds();
        if (wardDto.getId().isPresent())
            ward.setId(wardDto.getId().get());
        if (wardDto.getName().isPresent())
            ward.setName(wardDto.getName().get());
        if (wardDto.getCode() != null && wardDto.getCode().isPresent())
            ward.setCode(wardDto.getCode().get());
        return ward;
    }

    private List<WardDto> mapModelListToDtoList(List<WardBeds> wardList) {
        List<WardDto> wardDtoList = new ArrayList<>();
        if (!wardList.isEmpty()) {
            wardList.forEach(ward -> {
                WardDto wardDto = new WardDto();
                if (ward.getId() != null)
                    wardDto.setId(Optional.ofNullable(ward.getId()));
                if (ward.getName() != null)
                    wardDto.setName(Optional.of(ward.getName()));

                wardDto.setCurrentBedCount(findWardBedCount(ward.getId()));

                wardDto.setOccupiedCount(findWardBedOccupiedCount(ward.getId()));

                wardDto.setPercentageCount(findBedUsagePercentage(ward.getId()));

                wardDtoList.add(wardDto);
            });
        }
        return wardDtoList;
    }

    private List<WardBeds> mapDtoListToModel(List<WardDto> wardDtoList) {
        List<WardBeds> wardList = new ArrayList<>();
        if (!wardDtoList.isEmpty()) {
            wardDtoList.forEach(wardDto -> {
	            WardBeds ward = new WardBeds();
                if (wardDto.getId().isPresent())
                    ward.setId(wardDto.getId().get());
                if (wardDto.getName().isPresent())
                    ward.setName(wardDto.getName().get());
                ward.setCurrentBedCount(wardDto.getCurrentBedCount());
                ward.setOccupiedCount(wardDto.getOccupiedCount());
                wardList.add(ward);
            });
        }
        return wardList;
    }


    private WardBeds setModelFromDtoToUpdate(WardBeds ward, WardDto wardDto) {
        if (wardDto.getName().isPresent() && !wardDto.getName().get().isEmpty() )
            ward.setName(wardDto.getName().get());
        if (wardDto.getCode().isPresent() && !wardDto.getCode().get().isEmpty())
            ward.setCode(wardDto.getCode().get());
        return ward;
    }


 */
