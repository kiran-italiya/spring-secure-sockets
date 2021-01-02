package com.kiran.securesockets.common.notificationsocket.entity;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;

@Entity
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Table(name = "user_notifications")
@IdClass(UserNotificationPK.class)
public class UserNotification implements Serializable {

	private static final long serialVersionUID = 4086356766930698395L;

	@Id
	@Column(name = "notification_id", nullable = false)
	private BigInteger notificationId;
	@Id
	@Column(name = "user_id", nullable = false)
	private Long userId;
	@Column(name = "\"read\"")
	private Boolean read;
	@Column(name = "read_time")
	private Timestamp readTime;

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

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Timestamp getReadTime() {
		return readTime;
	}

	public void setReadTime(Timestamp readTime) {
		this.readTime = readTime;
	}
}
