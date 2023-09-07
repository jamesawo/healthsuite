package com.hmis.server.hmis.modules.nurse.model;

import com.hmis.server.hmis.modules.emr.model.PatientDetail;
import com.hmis.server.hmis.modules.nurse.dto.WaitingStatusEnum;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Data
@Entity
@Table( name = "hmis_nurse_waiting_list_data")
@NoArgsConstructor
@ToString
@TypeDef( name = "jsonb", typeClass = JsonBinaryType.class )
//@TypeDefs({
//		          @TypeDef(name = "string-array", typeClass = StringArrayType.class),
//		          @TypeDef(name = "int-array", typeClass = IntArrayType.class),
//		          @TypeDef(name = "json", typeClass = JsonType.class)
//          })
public class NurseWaitingList  {
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn( name = "patient_detail", nullable = false, unique = true )
	private PatientDetail patientDetail;

	@Column(name = "waiting_status", nullable = false)
	private String waitingStatus = WaitingStatusEnum.WAITING.name();

	@Type( type = "jsonb" )
	@Column( columnDefinition = "jsonb" )
	private List< Long > clinics;

	@Column(name = "recorded_date_time")
	private LocalDateTime dateTime = LocalDateTime.now();

	@Column( name = "date" )
	private LocalDate date = LocalDate.now();

	@Column( name = "time" )
	private LocalTime time = LocalTime.now();

}
