package artie.pedagogicalintervention.webservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Service
public class SecurityService {
	
	@Value("${artie.webservices.login.url}")
	private String loginUrl;

	@Value("${artie.api.key}")
	private String apiKey;
	private RestTemplate restTemplate;
	private HttpEntity<String> entity;

	@Autowired
	public SecurityService(RestTemplateBuilder builder){this.restTemplate = builder.build();}

	@PostConstruct
	public void setUp(){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("apiKey", this.apiKey);
		this.entity = new HttpEntity<String>("parameters", headers);
	}

	/**
	 * Login service
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean login(String user, String password) {
		ResponseEntity<Boolean> wsResponse = this.restTemplate.exchange(this.loginUrl + "?userName=" + user + "&password=" + password, HttpMethod.GET, this.entity, Boolean.class);
		return wsResponse.getBody().booleanValue();
	}
	
}
