package artie.pedagogicalintervention.webservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SecurityService {
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Value("${artie.webservices.login.url}")
	private String loginUrl;

	/**
	 * Login service
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean login(String user, String password) {
		boolean result = false;
		
		ResponseEntity<Boolean> wsResponse = restTemplate.getForEntity(this.loginUrl, Boolean.class, user, password);
		
		return wsResponse.getBody().booleanValue();
	}
	
}
