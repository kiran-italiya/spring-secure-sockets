package com.kiran.securesockets.common.notificationsocket.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigInteger;

public class UserNotificationPK implements Serializable {

	private static final long serialVersionUID = 4104627745567589463L;
	@Id
	@Column(name = "notification_id", nullable = false)
	private BigInteger notificationId;
	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;

	public UserNotificationPK() {
	}

	public UserNotificationPK(BigInteger notificationId, Long userId) {
		this.notificationId = notificationId;
		this.userId = userId;
	}

	public BigInteger getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(BigInteger notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
