package com.hmis.server.hmis.modules.emr.dto;

public enum PatientAppointmentStatusEnum {

	CLOSED( "CLOSED" ),
	OPEN( "OPEN" ),
	PAST( "PAST" ),
	RESCHEDULE( "RESCHEDULE" );

	public final String label;

	private PatientAppointmentStatusEnum( String label ) {
		this.label = label;
	}
}
