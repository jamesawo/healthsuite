package com.hmis.server.hmis.modules.notification.service;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleEnum;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.common.service.GlobalSettingsImpl;
import com.hmis.server.hmis.common.common.service.HmisUtilService;
import com.hmis.server.hmis.common.socket.SockDto;
import com.hmis.server.hmis.common.socket.SockMessageService;
import com.hmis.server.hmis.modules.notification.model.Notification;
import com.hmis.server.hmis.modules.notification.model.NotificationTypeEnum;
import com.hmis.server.hmis.modules.notification.repository.NotificationRepository;
import com.hmis.server.hmis.modules.pharmacy.model.DrugRegister;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import static com.hmis.server.hmis.common.constant.HmisConfigConstants.DESTINATION_NOTIFICATION_WEB;
import static com.hmis.server.hmis.common.constant.HmisGlobalSettingKeys.*;
import static com.hmis.server.hmis.common.constant.HmisNotifMessageConstants.OUT_OF_STOCK_HINT;
import static com.hmis.server.hmis.common.constant.HmisNotifMessageConstants.OUT_OF_STOCK_TITLE;

@Service
public class NotificationServiceImpl implements INotificationService {
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private GlobalSettingsImpl globalSettings;
	@Autowired
	private HmisUtilService utilService;
	@Autowired
	private SockMessageService sockMessageService;

	@Override
	public void saveAndSendNotification(Notification notification){
		//check global setting notification values
		String enableNotificationCore = this.globalSettings.findValueByKey(NOTIF_ENABLE_NOTIFICATION_CORE_PREFIX);
		if(enableNotificationCore.equals("yes")){
			if( this.isDuplicateNotification(notification) ){
				this.sendNotification(notification);
			}else{
				this.saveNotification(notification);
				this.sendNotification(notification);
			}
		}
	}

	@Override
	public void saveNotification(Notification notification){
		this.notificationRepository.save(notification);
	}

	@Override
	public void sendNotification(Notification notification){
		if( ObjectUtils.isNotEmpty(notification.getId()) ){
			notification.setPublished(true);
			this.notificationRepository.save(notification);

			String enableNotificationWeb = this.globalSettings.findValueByKey(NOTIF_ENABLE_WEB_NOTIFICATION_PREFIX);
			String enableNotificationSMS = this.globalSettings.findValueByKey(NOTIF_ENABLE_SMS_NOTIFICATION_PREFIX);
			String enableNotificationEmail = this.globalSettings.findValueByKey(NOTIF_ENABLE_EMAIL_NOTIFICATION_PREFIX);

			if( enableNotificationWeb.equals(TRUE) ) {
				this.sendWebNotification(notification);
			}
			if( enableNotificationEmail.equals(TRUE) ) {
				this.sendEmailNotification(notification);
			}
			if( enableNotificationSMS.equals(TRUE) ) {
				this.sendSmsNotification(notification);
			}
		}

	}

	@Override
	public Notification findOne(Long id){
		Optional< Notification > optional = this.notificationRepository.findById(id);
		if( !optional.isPresent() )throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		return optional.get();
	}

	@Override
	public void removeOne(Long id){
		this.notificationRepository.deleteById(id);
	}

	@Override
	public List<SockDto> findByApplicationModule(ApplicationModuleEnum moduleEnum){
		List<SockDto> list = new ArrayList<>();
		List< Notification > notifications = this.notificationRepository.findAllByModuleEqualsAndDeletedAtIsNull(moduleEnum);
		if( notifications.size() > 0 ){
			list = notifications.stream().map(this::mapNotificationToSockDto).collect(Collectors.toList());
		}
		return list;
	}

	@Override
	public Notification prepareLowStockNotification(Department department, DrugRegister register){
		String outlet = department.getName();
		String drug = register.fullBrandName();

		Notification notification = new Notification();
		notification.setContent(String.format("%s in %s is low in stock balance.", drug, outlet));
		notification.setTitle(OUT_OF_STOCK_TITLE);
		notification.setModule(ApplicationModuleEnum.PHARMACY);
		notification.setType(NotificationTypeEnum.MODULE);
		notification.setShortText(String.format("%s is low or out of stock for %s",   drug, outlet));
		notification.setDepartment(department);
		notification.setHint(OUT_OF_STOCK_HINT +drug+" - "+outlet);

		return notification;
	}

	private boolean isDuplicateNotification(Notification notification){
		return this.notificationRepository.existsNotificationByModuleAndTypeAndHintEquals(notification.getModule(), notification.getType(), notification.getHint());
	}


	private void sendEmailNotification(Notification notification){
		System.out.println("Sending Email Notification...Feature Not Completed");
		this.notificationRepository.addNotificationEmailCount(notification.getId());
	}

	private void sendWebNotification(Notification notification){
		System.out.println("Sending WEB Notification...Feature Not Completed");
		this.notificationRepository.addNotificationWebCount(notification.getId());
		this.sockMessageService.sendMessage(DESTINATION_NOTIFICATION_WEB, new SockDto(notification.getContent(), notification.getTitle()));
	}

	private void sendSmsNotification(Notification notification){
		System.out.println("Sending SMS Notification...Feature Not Completed");
		this.notificationRepository.addNotificationSmsCount(notification.getId());
	}

	private SockDto mapNotificationToSockDto(Notification notification){
		SockDto sock = new SockDto();
		sock.setId(notification.getId());
		sock.setContent(notification.getContent());
		sock.setTitle(notification.getTitle());
		sock.setDateTime(this.utilService.transformToDateDto( notification.getDateTime()));
		return sock;
	}


}

