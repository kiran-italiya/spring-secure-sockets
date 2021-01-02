package com.kiran.securesockets.security.session;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomSessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {

	private String expiredUrl = "";

	public CustomSessionInformationExpiredStrategy(String expiredUrl) {
		this.expiredUrl = expiredUrl;
	}

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent sessionInformationExpiredEvent) throws IOException, ServletException {

		HttpServletRequest request = sessionInformationExpiredEvent.getRequest();
		HttpServletResponse response = sessionInformationExpiredEvent.getResponse();
		request.getSession();// creates a new session
		response.sendRedirect(request.getContextPath() + expiredUrl);
	}

}
