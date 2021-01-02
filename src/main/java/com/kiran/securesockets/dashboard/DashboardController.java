package com.kiran.securesockets.dashboard;

import com.kiran.securesockets.common.notificationsocket.NotificationService;
import com.kiran.securesockets.security.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Controller
@RequestMapping("dashboard")
public class DashboardController {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private HttpSession session;

	@Autowired
	private SecurityUtil securityUtil;

	@GetMapping()
	public String dashboardPage(Model model) {
		return "dashboard/dashboard";
	}

	@GetMapping("/event/user")
	public ResponseEntity<Object> userEvent(@RequestParam("type") String type, @RequestParam("message") String message) {
		EntityManager manager = null;
		EntityTransaction transaction = null;
		try {
			manager = entityManagerFactory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			notificationService.saveNotificationForUsers("toast", message + " " + new Date().toString(), new ArrayList<>(Arrays.asList(securityUtil.getUserId(session))), manager);
			transaction.commit();
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			e.printStackTrace();
			if(transaction!=null){
				transaction.rollback();
			}
		} finally {
			if (manager != null) {
				manager.clear();
				manager.close();
			}
		}
		return ResponseEntity.badRequest().body("Error");
	}

	@GetMapping("/event/broadcast")
	public ResponseEntity<Object> broadcastEvent(@RequestParam("type") String type, @RequestParam("message") String message) {
		EntityManager manager = null;
		EntityTransaction transaction = null;
		try {
			manager = entityManagerFactory.createEntityManager();
			transaction = manager.getTransaction();
			transaction.begin();
			notificationService.saveBroadcastNotification("toast", message + " " + new Date().toString(), manager);
			transaction.commit();
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			e.printStackTrace();
			if(transaction!=null){
				transaction.rollback();
			}
		} finally {
			if (manager != null) {
				manager.clear();
				manager.close();
			}
		}
		return ResponseEntity.badRequest().body("Error");
	}

}
