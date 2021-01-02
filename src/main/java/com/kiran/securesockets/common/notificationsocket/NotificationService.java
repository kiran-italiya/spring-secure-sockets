package com.kiran.securesockets.common.notificationsocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiran.securesockets.common.notificationsocket.entity.Notification;
import com.kiran.securesockets.common.notificationsocket.entity.UserNotification;
import com.kiran.securesockets.common.utils.GetIpmac;
import com.kiran.securesockets.common.utils.TextEncryptDecrypt;
import com.kiran.securesockets.security.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Service
public class NotificationService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private HttpSession session;

	@Autowired
	private SecurityUtil securityUtil;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private ApplicationContext context;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private TextEncryptDecrypt textEncryptDecrypt;

	public ArrayList<NotificationDTO> getUnreadNotifications() {
		EntityManager manager = null;
		ArrayList<NotificationDTO> notifications = new ArrayList<>();
		try {
			manager = entityManagerFactory.createEntityManager();
			List<Object[]> rows = ((List<Object>) manager.createQuery("select spn.notificationId, spn.type, spn.payload from UserNotification sun join Notification spn on spn.notificationId=sun.notificationId and sun.read=0 where sun.userId=:userId")
					.setParameter("userId", securityUtil.getUserId(session))
					.getResultList()).stream().map(row -> (Object[]) row).collect(Collectors.toList());
			NotificationDTO notification;
			for (Object[] row : rows) {
				notification = new NotificationDTO(textEncryptDecrypt.encrypt(row[0].toString()), row[1].toString(), row[2].toString());
				notifications.add(notification);
			}
			return notifications;
		} catch (Exception e) {
			logger.error("{}", e);
		} finally {
			if (null != manager) {
				manager.close();
			}
		}
		return notifications;
	}

	public String[] markAsRead(String[] notificationIds, String userName) {
		EntityManager manager = null;
		EntityTransaction transaction = null;
		try {
			if (notificationIds.length > 0) {
				StringJoiner joiner = new StringJoiner(",", "(", ")");
				for (int i = 0; i < notificationIds.length; i++) {
					joiner.add("'" + textEncryptDecrypt.decrypt(notificationIds[i]) + "'");
				}
				manager = entityManagerFactory.createEntityManager();
				transaction = manager.getTransaction();
				transaction.begin();
				manager.createNativeQuery("update user_notifications sun join users um on um.userid=sun.user_id and um.active=1 set sun.read=1, sun.read_time=current_timestamp() where sun.read=0 and um.username=:userName and sun.notification_id in " + joiner.toString()).setParameter("userName", userName).executeUpdate();
				transaction.commit();
			}
			return notificationIds;
		} catch (Exception e) {
			logger.error("{}", e);
		} finally {
			if (null != manager) {
				manager.close();
			}
		}
		return new String[]{};
	}

	public void saveNotificationForUsers(String type, Object payload, List<Long> userIds, EntityManager manager) throws Exception {

		Notification pushNotification = context.getBean(Notification.class);
		pushNotification.setType(type);
		pushNotification.setPayload(mapper.writeValueAsString(payload));
		pushNotification.setCreatedBy(securityUtil.getUserId(session));
		pushNotification.setCreatedOn(new Timestamp(System.currentTimeMillis()));
		pushNotification.setIpAddress(request.getRemoteAddr());
		pushNotification.setMacAddress(GetIpmac.getClientMACAddress(request.getRemoteAddr()));

		pushNotification = manager.merge(pushNotification);
		manager.flush();

		UserNotification userNotification;
		for (long userId : userIds) {
			userNotification = context.getBean(UserNotification.class);
			userNotification.setNotificationId(pushNotification.getNotificationId());
			userNotification.setRead(false);
			userNotification.setUserId(userId);
			userNotification = manager.merge(userNotification);
		}
	}

	public void saveBroadcastNotification(String type, Object payload, EntityManager manager) throws Exception {

		List<Long> userIds = ((List<Object>) manager.createQuery("select distinct u.userId from User u where u.active=1").getResultList()).stream().map(row -> ((Long) row)).collect(Collectors.toList());

		if (userIds.size() > 0) {
			saveNotificationForUsers(type, payload, userIds, manager);
		}

	}

	public void saveNotificationForUsersType(String type, Object payload, List<String> userTypeIds, EntityManager manager) throws Exception {
//		StringJoiner stringJoiner = new StringJoiner(",");
//		userTypeIds.forEach(stringJoiner::add);
		List<Long> userIds = ((List<Object>) manager.createQuery("select distinct u.userId from User u where u.userType in (:userTypeIds) and u.active=1").setParameter("userTypeIds", userTypeIds).getResultList()).stream().map(row -> ((Long) row)).collect(Collectors.toList());

		if (userIds.size() > 0) {
			saveNotificationForUsers(type, payload, userIds, manager);
		}

	}


}
