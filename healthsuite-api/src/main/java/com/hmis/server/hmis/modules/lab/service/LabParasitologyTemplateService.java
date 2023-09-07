package com.hmis.server.hmis.modules.lab.service;

import com.hmis.server.hmis.modules.lab.dto.parasitology.LabParasitologyTemplateDto;
import com.hmis.server.hmis.modules.lab.model.LabParasitologyResultTemplate;
import com.hmis.server.hmis.modules.lab.model.LabTestPreparation;
import com.hmis.server.hmis.modules.lab.repository.LabParasitologyResultTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class LabParasitologyTemplateService {
	private final LabParasitologyResultTemplateRepository parasitologyRepository;

	public LabParasitologyTemplateService( LabParasitologyResultTemplateRepository parasitologyRepository ) {
		this.parasitologyRepository = parasitologyRepository;
	}

	public LabParasitologyResultTemplate saveParasitologyResult( LabTestPreparation preparation, LabParasitologyTemplateDto dto ) {
		LabParasitologyResultTemplate template = new LabParasitologyResultTemplate();
		template.setCulture(dto.getCulture());
		template.setMacroscopy(dto.getMacroscopy());
		template.setMicroscopy(dto.getMicroscopy());
		template.setTestPreparation(preparation);
		template.setNewLabNote(dto.getNewLabNote());
		this.findByTestPreparation(preparation).ifPresent(rest -> template.setId(rest.getId()));
		return this.parasitologyRepository.save(template);
	}

	public Optional<LabParasitologyResultTemplate> findByTestPreparation( LabTestPreparation preparation ) {
		return this.parasitologyRepository.findByTestPreparation(preparation);
	}
}
