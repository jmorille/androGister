package eu.ttbox.androgister.web.rest;

import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AuthenticatorService {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticatorService.class);

	@RequestMapping("/auth")
	@ResponseBody
	public String authentificate(HttpServletRequest request) {
		// NOTE: The CasAuthenticationToken can also be obtained using
		// SecurityContextHolder.getContext().getAuthentication()
		Enumeration<String> enumSet = request.getHeaderNames();
		while (enumSet.hasMoreElements()) {
			String headerName = enumSet.nextElement();
			String headerValue = request.getHeader(headerName);
			LOG.debug("Header {} : {}", headerName, headerValue);
		}
		if (request.getCookies() == null) {
			LOG.debug("No Cookie" );

		} else {
			for (Cookie cookie : request.getCookies()) {
				LOG.debug("Cookie {} : {}", cookie.getName(), cookie.getValue());
			}
		}

		final CasAuthenticationToken token = (CasAuthenticationToken) request.getUserPrincipal();
		LOG.info("CasAuthenticationToken token  : {}", token);

		final CasAuthenticationToken tokenb = (CasAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		LOG.info("CasAuthenticationToken token  bis : {}", tokenb);

		// proxyTicket could be reused to make calls to the CAS service even if
		// the
		// target url differs
		String targetUrl = "http//GF219896:8080/app/rest/auth/";
		// String casServiceHost = System.getProperty("cas.service.host",
		// "localhost:8443");
		// targetUrl = "https://"+casServiceHost+"/cas-sample/secure/";

		final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(targetUrl);

		LOG.info("token.getAssertion() : {}", token.getAssertion());
		LOG.info("proxyTicket : {}", proxyTicket);
		// Make a remote call using the proxy ticket

		return proxyTicket;
	}

}
