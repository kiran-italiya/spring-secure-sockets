package com.kiran.securesockets.common.entity;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Entity
@Table(name = "users")
public class User implements java.io.Serializable {

	private static final long serialVersionUID = -1694497762144469655L;
	private long userId;
	private String userName;
	private int userType;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String secretKey;
	private String customSalt;
	private int active;
	private long createdBy;
	private Date createdOn;

	@Id
	@Column(name = "userId", unique = true, nullable = false)
	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Column(name = "type", nullable = false)
	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	@Column(name = "firstName", nullable = false, length = 64)
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "lastName", nullable = false, length = 64)
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "email", nullable = false, length = 256)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "password", length = 256)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "secretKey", length = 256)
	public String getSecretKey() {
		return this.secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	@Column(name = "customSalt", length = 256)
	public String getCustomSalt() {
		return this.customSalt;
	}

	public void setCustomSalt(String customSalt) {
		this.customSalt = customSalt;
	}

	@Column(name = "active", nullable = false)
	public int getActive() {
		return this.active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	@Column(name = "createdBy", nullable = false)
	public long getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdOn", nullable = false, length = 19)
	public Date getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "userName", length = 32)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User)) return false;

		User user = (User) o;

		if (userId != user.userId) return false;
		if (userType != user.userType) return false;
		if (active != user.active) return false;
		if (createdBy != user.createdBy) return false;
		if (userName != null ? !userName.equals(user.userName) : user.userName != null) return false;
		if (firstName != null ? !firstName.equals(user.firstName) : user.firstName != null) return false;
		if (lastName != null ? !lastName.equals(user.lastName) : user.lastName != null) return false;
		if (email != null ? !email.equals(user.email) : user.email != null) return false;
		if (password != null ? !password.equals(user.password) : user.password != null) return false;
		if (secretKey != null ? !secretKey.equals(user.secretKey) : user.secretKey != null) return false;
		if (customSalt != null ? !customSalt.equals(user.customSalt) : user.customSalt != null) return false;
		return createdOn != null ? createdOn.equals(user.createdOn) : user.createdOn == null;

	}

	@Override
	public int hashCode() {
		int result = (int) (userId ^ (userId >>> 32));
		result = 31 * result + (userName != null ? userName.hashCode() : 0);
		result = 31 * result + userType;
		result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		result = 31 * result + (email != null ? email.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (secretKey != null ? secretKey.hashCode() : 0);
		result = 31 * result + (customSalt != null ? customSalt.hashCode() : 0);
		result = 31 * result + active;
		result = 31 * result + (int) (createdBy ^ (createdBy >>> 32));
		result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
		return result;
	}
}
