package com.hmis.server.hmis.modules.notification.model;

import com.hmis.server.hmis.common.common.dto.ApplicationModuleEnum;
import com.hmis.server.hmis.common.common.model.Department;
import com.hmis.server.hmis.common.user.model.User;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table( name = "hmis_notification_data")
@NoArgsConstructor
@ToString
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "module")
	private ApplicationModuleEnum module;

	@Column(name = "title", nullable =  false)
	private String title;

	@Column(name = "content", nullable =  false)
	private String content;

	@Column(name = "date_time")
	private LocalDateTime dateTime = LocalDateTime.now();

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "delivery_count")
	private Integer deliveryCount = 0;

	@Column(name = "notification_type", nullable = false)
	private NotificationTypeEnum type;

	@Column(name = "short_text")
	private String shortText;

	@Column(name = "email_count")
	private Integer emailCount = 0;

	@Column(name="sms_count")
	private Integer smsCount = 0;

	@Column(name="web_count")
	private Integer webCount = 0;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

	@OneToOne
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "published", nullable = false )
	private Boolean published = false;

	@Column(name = "can_publish")
	private Boolean canPublish = true;

	@Column(name = "hint", nullable =  false)
	private String hint; //used to check notification possible duplicate, used with module & type
}
