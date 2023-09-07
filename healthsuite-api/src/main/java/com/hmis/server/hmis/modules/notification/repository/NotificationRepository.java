package com.hmis.server.hmis.modules.notification.repository;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleEnum;
import com.hmis.server.hmis.modules.notification.model.Notification;
import com.hmis.server.hmis.modules.notification.model.NotificationTypeEnum;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository< Notification, Long > {
	List< Notification > findAllByModuleEqualsAndDeletedAtIsNull(ApplicationModuleEnum moduleEnum);

	boolean existsNotificationByModuleAndTypeAndHintEquals(ApplicationModuleEnum module, NotificationTypeEnum type, String hint);

	@Transactional
	@Modifying
	@Query(value = "update Notification n set n.deliveryCount = n.deliveryCount + 1, n.webCount = n.webCount + 1 where n.id = :id")
	void addNotificationWebCount(@Param(value = "id") Long id);

	@Transactional
	@Modifying
	@Query(value = "update Notification n set n.deliveryCount = n.deliveryCount + 1, n.emailCount = n.emailCount + 1 where n.id = :id")
	void addNotificationEmailCount(@Param(value = "id") Long id);

	@Transactional
	@Modifying
	@Query(value = "update Notification n set n.deliveryCount = n.deliveryCount + 1, n.smsCount = n.smsCount + 1 where n.id = :id")
	void addNotificationSmsCount(@Param(value = "id") Long id);
}
