package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude( JsonInclude.Include.NON_NULL )
@JsonPropertyOrder( value = { "message", "httpStatusCode", "httpStatusText", "date", "data" } )
public class ResponseDto< T > {

	private String message;

	@Temporal( TemporalType.TIMESTAMP )
	@JsonFormat( shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm" )
	private Date date = new Date();

	private int httpStatusCode;

	private String httpStatusText;

	private Object data;

	private T result;

	public ResponseDto() {
	}

	public ResponseDto(Object data) {
		this.data = data;
		this.setHttpStatusCode(200);
	}

}
