package com.hmis.server.hmis.common.constant;

public class HmisConfigConstants {
    public static final int JWT_DEFAULT_EXPIRE_HOURS = 6;
    //    public static final String SOCKET_CLIENT = "http://localhost:4200";
    public static final String SOCKET_CLIENT = "http://192.168.43.204:4200";
    public static final String SOCKET_ENDPOINT = "/socket";
    public static final String SOCKET_DESTINATION_PREFIX = "app";
    public static final String SOCKET_TOPIC = "/topic";
    //test user destination
    public static final String DESTINATION_USER = "/topic/user";
    public static final String CONTENT_USER = "Message sent from server ";
    // global setting update destination
    public static final String DESTINATION_GLOBAL_SETTING_UPDATED = SOCKET_TOPIC +"/global-setting";
    public static final String CONTENT_GLOBAL_SETTING_UPDATED = "Global Setting Updated";
	// web notification destination
	public static final String DESTINATION_NOTIFICATION_WEB = SOCKET_TOPIC + "/notification";
	//nursing waiting list
	public static final String DESTINATION_NURSE_WAITING_LIST = SOCKET_TOPIC + "/nurse-waiting-list";
    // doctor waiting list
    public static final String DESTINATION_DOCTOR_WAITING_LIST = SOCKET_TOPIC + "/doctor-waiting-list";
}
