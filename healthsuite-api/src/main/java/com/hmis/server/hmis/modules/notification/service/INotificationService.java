package com.hmis.server.hmis.modules.notification.service;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleEnum;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.socket.SockDto;
import com.hmis.server.hmis.modules.notification.model.Notification;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import java.util.List;

public interface INotificationService {
	void saveAndSendNotification(Notification notification);

	void saveNotification(Notification notification);

	void sendNotification(Notification notification);

	Notification findOne(Long id);

	void removeOne(Long id);

	List< SockDto > findByApplicationModule(ApplicationModuleEnum moduleEnum);

	Notification prepareLowStockNotification(Department department, DrugRegister register);
}
