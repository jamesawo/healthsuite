package com.hmis.server.hmis.common.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SockMessageService {
	@Autowired
	SimpMessagingTemplate messagingTemplate;

	public void sendMessage(String destination, SockDto data){
		this.messagingTemplate.convertAndSend(destination, data);
	}
}
