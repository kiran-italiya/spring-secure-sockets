package com.kiran.securesockets.common.authentication;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public interface LogInService {
	@GetMapping(value = {"login*", "signin*"})
	String signInPage(Model model);
}