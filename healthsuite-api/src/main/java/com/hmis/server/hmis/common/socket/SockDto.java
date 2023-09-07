package com.hmis.server.hmis.common.socket;

import com.hmis.server.hmis.common.common.dto.DateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SockDto {
	private Long id;
	private String content;
	private String title;
	private DateDto dateTime;

	public SockDto(String content) {
		this.content = content;
	}

	public SockDto(String content, String title) {
		this.content = content;
		this.title = title;
	}
}
