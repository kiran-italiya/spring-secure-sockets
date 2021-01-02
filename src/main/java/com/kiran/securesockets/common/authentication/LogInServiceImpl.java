package com.kiran.securesockets.common.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@Service
@RequestScope(proxyMode = ScopedProxyMode.INTERFACES)
public class LogInServiceImpl implements LogInService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LogInServiceImpl.class);

	@Autowired
	private HttpServletRequest request;

	@Value("${my-app.server.url}")
	private String serverUrl;

	@Override
	public String signInPage(Model model) {
		LOGGER.info("login page");
		if (request.getSession().getAttribute("errormsg") != null) {
			model.addAttribute("errormsg", request.getSession().getAttribute("errormsg"));
			request.getSession().removeAttribute("errormsg");
		}
		String serverurl = serverUrl;
		request.getSession(false).setAttribute("server_url", serverurl);
		return "login/index";
	}
}