package com.hmis.server.hmis.common.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude( JsonInclude.Include.NON_NULL)
public class ValidationResponse {
	private Boolean status;
	private List<String> messages = new ArrayList<>();

	public ValidationResponse(boolean status){
		this.status = status;
		this.messages = new ArrayList<>();
	}

	public void addMessage(String message){
		this.messages.add(message);
	}

}
