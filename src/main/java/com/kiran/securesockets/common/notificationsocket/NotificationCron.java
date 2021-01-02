package com.kiran.securesockets.common.notificationsocket;

import com.kiran.securesockets.common.authentication.CustomUserDetails;
import com.kiran.securesockets.common.utils.TextEncryptDecrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationCron {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private NotificationDispatcher dispatcher;

	@Autowired
	private SessionRegistry sessionRegistry;

	@Autowired
	private TextEncryptDecrypt textEncryptDecrypt;

	@Scheduled(fixedDelay = 5000)
	public void pushNotifications() {
		EntityManager manager = null;
		try {

			List<Object> principals = sessionRegistry.getAllPrincipals();
			for (Object principal : principals) {
				if (!sessionRegistry.getAllSessions(principal, false).isEmpty()) {
					String userName = ((CustomUserDetails) principal).getUsername();
					manager = entityManagerFactory.createEntityManager();

					ArrayList<NotificationDTO> notifications = new ArrayList<>();
					List<Object[]> rows = ((List<Object>) manager.createQuery("select u.userId, n.notificationId,n.type,n.payload from UserNotification un join User u on un.userId=u.userId and u.active=1 join Notification n on un.notificationId=n.notificationId where un.read=0 and u.userName=:userName")
							.setParameter("userName", userName).getResultList()).stream().map(i -> ((Object[]) i)).collect(Collectors.toList());

					NotificationDTO notification;
					for (Object[] row : rows) {
						notification = new NotificationDTO(textEncryptDecrypt.encrypt(String.valueOf(row[1])), String.valueOf(row[2]), String.valueOf(row[3]));
						notifications.add(notification);
					}

					HashMap<String, Object> map = new HashMap<>();
					map.put("type", NotificationTypes.PUSH_NOTIFICATIONS);
					map.put("data", notifications);
					dispatcher.toUser(userName, map);
					System.out.println("For user:"+userName);
				}
			}
		} catch (
				Exception e) {
			logger.error("{}", e);
		} finally {
			if (null != manager) {
				manager.close();
			}
		}
		logger.info("Notification Cron Finished...");
	}

}
