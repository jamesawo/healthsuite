package com.hmis.server.hmis.modules.notification.controller;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleEnum;
import com.hmis.server.hmis.common.constant.HmisConstant;
import com.hmis.server.hmis.common.socket.SockDto;
import com.hmis.server.hmis.modules.notification.model.NotificationTypeEnum;
import com.hmis.server.hmis.modules.notification.service.NotificationServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( HmisConstant.API_PREFIX +"/notifications")
public class NotificationController {
	@Autowired
	private NotificationServiceImpl notificationService;

	@GetMapping(value = "by-module")
	public List< SockDto > getAllModuleNotification(@RequestParam( value = "module") ApplicationModuleEnum module, @RequestParam( value = "type", required = false) NotificationTypeEnum type){
		return this.notificationService.findByApplicationModule(module);
	}
}
