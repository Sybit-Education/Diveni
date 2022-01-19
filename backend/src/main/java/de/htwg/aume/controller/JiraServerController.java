package de.htwg.aume.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.VerificationCode;
import de.htwg.aume.service.DatabaseService;
import de.htwg.aume.service.jira.JiraServerService;
import lombok.val;

@CrossOrigin(origins = { "http://localhost:8080", "https://pp.vnmz.de" })
@RestController
public class JiraServerController {

	@Autowired
	DatabaseService databaseService;

	@Autowired
	JiraServerService jiraServerService;

	@GetMapping(value = "/jira/oauth1/requestToken")
	public ResponseEntity<JiraRequestToken> getRequestToken() {
		return new ResponseEntity<>(jiraServerService.getRequestToken(), HttpStatus.OK);
	}

	@PostMapping(value = "/jira/oauth1/verificationCode")
	public void getAccessToken(@RequestBody VerificationCode verificationCode) {
		jiraServerService.getAccessToken(verificationCode.getCode(), verificationCode.getToken());
	}
}
