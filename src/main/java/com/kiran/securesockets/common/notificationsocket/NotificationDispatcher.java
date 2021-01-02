package com.kiran.securesockets.common.notificationsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class NotificationDispatcher {

	@Autowired
	private SimpMessagingTemplate template;

	public void toUser(String userName, Object payload) {
		template.convertAndSendToUser(userName, "/queue/notification", payload);
	}

	public void toCurrentUser(Object payload) {
		toUser(SecurityContextHolder.getContext().getAuthentication().getName(), payload);
	}

	public void broadcast(Object payload) {
		template.convertAndSend("/topic/notification", payload);
	}

}
