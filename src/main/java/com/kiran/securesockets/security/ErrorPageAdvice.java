package com.kiran.securesockets.security;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@ControllerAdvice
public class ErrorPageAdvice implements ErrorViewResolver {

	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
		final Object requestStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		
		if (requestStatus != null) {
			Integer statusCode = Integer.parseInt(requestStatus.toString());
			ModelAndView httpStatusView = new ModelAndView();
			httpStatusView.addObject("resource_url", System.getProperty("resource_url"));
			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				httpStatusView.setViewName("error/404");
				return httpStatusView;
			} else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
				httpStatusView.setView(new RedirectView("/login", true));
				httpStatusView.addObject("errormsg", request.getAttribute("errormsg"));
				return httpStatusView;
			} else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
				httpStatusView.setViewName("error/400");
				return httpStatusView;
			} else if (statusCode == HttpStatus.FORBIDDEN.value()) {
				httpStatusView.setViewName("error/403");
				return httpStatusView;
			}else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				httpStatusView.setViewName("error/500");
				return httpStatusView;
			}
		}
		
		return null;
	}

}
