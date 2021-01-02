package com.kiran.securesockets.common.notificationsocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;

@Controller
public class NotificationController {

	@Autowired
	private NotificationService service;

	@Autowired
	private SimpMessagingTemplate template;

	@MessageMapping("/notification/get/unread")
	@SendToUser(destinations = "/queue/notification")
	public HashMap<String, Object> getUnreadNotifications(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("type", NotificationTypes.PUSH_NOTIFICATIONS);
		map.put("data", service.getUnreadNotifications());
		return map;
	}

	@MessageMapping("/notification/post/mark_read")
//	@SendToUser(destinations = "/queue/notification")
	public void markNotificationAsRead(@Payload String[] ids, MessageHeaders headers, Principal user){
		service.markAsRead(ids, user.getName());
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("type", NotificationTypes.PUSH_NOTIFICATIONS);
//		map.put("data", service.markAsRead(ids));
//		return map;
	}

}

