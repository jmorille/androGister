package eu.ttbox.androgister.security.cas;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.util.CommonUtils;
import org.springframework.security.cas.ServiceProperties;

public class CasAuthenticationEntryPoint extends org.springframework.security.cas.web.CasAuthenticationEntryPoint {

	@Override
    protected String createServiceUrl(final HttpServletRequest request, final HttpServletResponse response) {
		ServiceProperties sp = getServiceProperties(); 
		StringBuilder serverName = new StringBuilder().append(request.getServerName());
		if ( request.getServerPort()!= 80) {
			serverName.append(':').append(request.getServerPort());
		}
        return CommonUtils.constructServiceUrl(request, response,
        		sp.getService(), serverName.toString(), sp.getArtifactParameter(), getEncodeServiceUrlWithSessionId());
    }
 

}
