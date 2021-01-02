package com.kiran.securesockets.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private SessionRegistry sessionRegistry;

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
	                            HttpServletResponse response,
	                            Authentication authentication)
			throws IOException, ServletException {
		if (authentication != null && authentication.getDetails() != null) {
			sessionRegistry.removeSessionInformation(request.getSession().getId());
			request.getSession().invalidate();
			response.sendRedirect(request.getContextPath() + "/login");
		}
	}
}