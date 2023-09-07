package com.hmis.server.hmis.modules.lab.dto.parasitology;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class LabCultureLineOrganism {
	private String organism;
	private Boolean selected;
	private List<LabCultureAntibiotics> antibiotics;
	private JRBeanCollectionDataSource antibioticsBeans;

}
