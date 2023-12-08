package artie.pedagogicalintervention.webservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger logger;

	@Autowired
	public SecurityService(RestTemplateBuilder builder){this.restTemplate = builder.build();}

	@PostConstruct
	public void setUp(){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add("apiKey", this.apiKey);
		this.entity = new HttpEntity<String>("parameters", headers);
		logger = LoggerFactory.getLogger(SecurityService.class);
	}

	/**
	 * Login service
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean login(String user, String password) {
		logger.info("Logging an user");
		ResponseEntity<Boolean> wsResponse = this.restTemplate.exchange(this.loginUrl + "?userName=" + user + "&password=" + password, HttpMethod.GET, this.entity, Boolean.class);
		logger.trace("Result from logging: " + wsResponse.getBody().booleanValue());
		return wsResponse.getBody().booleanValue();
	}
	
}
