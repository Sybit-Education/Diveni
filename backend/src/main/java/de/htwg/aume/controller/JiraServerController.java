package de.htwg.aume.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.Project;
import de.htwg.aume.model.TokenIdentifier;
import de.htwg.aume.model.UserStory;
import de.htwg.aume.model.VerificationCode;
import de.htwg.aume.service.DatabaseService;
import de.htwg.aume.service.jira.JiraServerService;

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
	public ResponseEntity<TokenIdentifier> getAccessToken(@RequestBody VerificationCode verificationCode) {
		return new ResponseEntity<>(jiraServerService.getAccessToken(verificationCode.getCode(), verificationCode.getToken()), HttpStatus.OK);
	}

	@GetMapping(value = "/jira/projects")
	public ResponseEntity<List<Project>> getProjects(@RequestHeader("X-Token-ID") String tokenIdentifier) {
		return new ResponseEntity<>(jiraServerService.getProjects(tokenIdentifier), HttpStatus.OK);
	}
	
	@GetMapping(value = "/jira/projects/{projectName}/issues")
	public ResponseEntity<List<UserStory>> getIssues(@RequestHeader("X-Token-ID") String tokenIdentifier, @PathVariable String projectName) {
		return new ResponseEntity<>(jiraServerService.getIssues(tokenIdentifier, projectName), HttpStatus.OK);
	}

}
