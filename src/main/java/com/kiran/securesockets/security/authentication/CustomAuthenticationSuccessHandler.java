package com.kiran.securesockets.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Value("${my-app.server.url}")
	private String serverUrl;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                                    Authentication authentication) throws IOException, ServletException {
		try {

			setDataInSession(request, authentication.getName());
			clearAuthenticationAttributes(request);
			handle(request, response, authentication);

		} catch (Exception e) {
			LOGGER.error("{}", e);
			authentication.setAuthenticated(false);
			SecurityContextHolder.getContext().setAuthentication(null);
			throw new AuthenticationException("Internal authentication error!");
		}
	}

	//TODO: modify query
	private void setDataInSession(HttpServletRequest request, String username) throws Exception {
		EntityManager manager = null;
		try {
			manager = entityManagerFactory.createEntityManager();

			HttpSession session = request.getSession(false);

			List<Object[]> userId = ((List<?>) manager.createNativeQuery("SELECT u.userid, concat(u.firstname, ' ', u.lastname) AS fullname FROM users u WHERE u.userid = :username")
					.setParameter("username", username)
					.getResultList())
					.stream()
					.map(id -> (Object[]) id)
					.collect(Collectors.toList());

			session.setAttribute("server_url", serverUrl);
			session.setAttribute("auth_username", username);
			session.setAttribute("auth_user_id", userId.get(0)[0]);
			session.setAttribute("auth_display_name", userId.get(0)[1]);

		} catch (Exception e) {
			throw e;
		} finally {
			if (null != manager) {
				manager.close();
			}
		}
	}

	/**
	 * @param request Clears the previous authentication exception from session if active.
	 */
	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}

	/**
	 * @param request
	 * @param response
	 * @param authentication
	 * @throws IOException Redirects the response to the supplied URL.
	 */
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
		String targetUrl = "/dashboard";

		if (response.isCommitted()) {
			LOGGER.error("Response has already been committed. Unable to redirect to {}.", targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

}
