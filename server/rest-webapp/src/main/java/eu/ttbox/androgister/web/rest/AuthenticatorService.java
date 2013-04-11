package eu.ttbox.androgister.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthenticatorService {

	@RequestMapping("/auth")
	@ResponseBody
	public String authentificate() {
		return "kokeoa";
	}
	
	
}
