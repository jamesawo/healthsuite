package com.hmis.server.hmis.common.exception;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data  @JsonAutoDetect @NoArgsConstructor
public class ExceptionResponse {

	private int status;
	private String message;
	private Date date = new Date(  );
	private List<String> errors = new ArrayList<>(  );

}
