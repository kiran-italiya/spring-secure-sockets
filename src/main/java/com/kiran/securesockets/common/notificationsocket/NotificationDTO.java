package com.kiran.securesockets.common.notificationsocket;

public class NotificationDTO {

	private String notificationId;
	private String type;
	private String payload;

	public NotificationDTO() {
	}

	public NotificationDTO(String notificationId, String type, String payload) {
		this.notificationId = notificationId;
		this.type = type;
		this.payload = payload;
	}

	public String getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(String notificationId) {
		this.notificationId = notificationId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
