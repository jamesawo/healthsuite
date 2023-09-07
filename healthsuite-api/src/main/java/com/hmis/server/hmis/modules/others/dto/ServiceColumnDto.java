package com.hmis.server.hmis.modules.others.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonInclude( JsonInclude.Include.NON_NULL )
public class ServiceColumnDto {

	@NotEmpty
	@NotNull
	private ServiceColumnEnum column;

	@NotEmpty
	@NotNull
	private Long id;

	@NotEmpty
	@NotNull
	private String value;

}
