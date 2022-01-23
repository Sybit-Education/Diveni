package de.htwg.aume.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.htwg.aume.model.JiraRequestToken;
import de.htwg.aume.model.TokenIdentifier;
import de.htwg.aume.model.UserStory;
import de.htwg.aume.model.VerificationCode;
import de.htwg.aume.service.DatabaseService;
import de.htwg.aume.service.projectmanagementproviders.ProjectManagementProvider;
import de.htwg.aume.service.projectmanagementproviders.jiracloud.JiraCloudService;
import de.htwg.aume.service.projectmanagementproviders.jiraserver.JiraServerService;
import lombok.val;

@CrossOrigin(origins = { "http://localhost:8080", "https://pp.vnmz.de" })
@RestController
@RequestMapping("/jira")
public class ProjectManagementController {

	@Autowired
	DatabaseService databaseService;

	@Autowired
	JiraServerService jiraServerService;

	@Autowired
	JiraCloudService jiraCloudService;

	@GetMapping(value = "/oauth1/requestToken")
	public ResponseEntity<JiraRequestToken> getRequestToken() {
		return new ResponseEntity<>(jiraServerService.getRequestToken(), HttpStatus.OK);
	}

	@PostMapping(value = "/oauth1/verificationCode")
	public ResponseEntity<TokenIdentifier> getOauth1AccessToken(@RequestBody VerificationCode verificationCode) {
		return new ResponseEntity<>(
				jiraServerService.getAccessToken(verificationCode.getCode(), verificationCode.getToken()),
				HttpStatus.OK);
	}

	@PostMapping(value = "/oauth2/authorizationCode")
	public ResponseEntity<TokenIdentifier> getOAuth2AccessToken(@RequestHeader("Origin") String origin,
			@RequestBody VerificationCode authorizationCode) {
		return new ResponseEntity<>(
				jiraCloudService.getAccessToken(authorizationCode.getCode(), origin),
				HttpStatus.OK);
	}

	@GetMapping(value = "/projects")
	public ResponseEntity<List<String>> getProjects(@RequestHeader("X-Token-ID") String tokenIdentifier) {
		val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

		if (projectManagementProvider == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(projectManagementProvider.getProjects(tokenIdentifier), HttpStatus.OK);
	}

	@GetMapping(value = "/projects/{projectName}/issues")
	public ResponseEntity<List<UserStory>> getIssues(@RequestHeader("X-Token-ID") String tokenIdentifier,
			@PathVariable String projectName) {
		val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

		if (projectManagementProvider == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(projectManagementProvider.getIssues(tokenIdentifier, projectName), HttpStatus.OK);
	}

	// TODO: For testing with postman at the moment, will be changed when
	// implementing frontend for it
	/* @RequestParam("sessionID") sessionID, */
	@PutMapping(value = "/issue")
	public void updateIssue(@RequestHeader("X-Token-ID") String tokenIdentifier, @RequestBody UserStory userStory) {
		// val session = ControllerUtils.getSessionOrThrowResponse(databaseService,
		// sessionID);
		val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

		if (projectManagementProvider != null) {
			projectManagementProvider.updateIssue(tokenIdentifier, userStory);
		}
	}

	@DeleteMapping(value = "/issue/{jiraID}")
	public void deleteIssue(@RequestHeader("X-Token-ID") String tokenIdentifier, @PathVariable String jiraID) {
		val projectManagementProvider = getProjectManagementProvider(tokenIdentifier);

		if (projectManagementProvider != null) {
			projectManagementProvider.deleteIssue(tokenIdentifier, jiraID);
		}
	}

	private ProjectManagementProvider getProjectManagementProvider(String tokenIdentifier) {
		if (jiraServerService.containsToken(tokenIdentifier)) {
			return jiraServerService;
		} else if (jiraCloudService.containsToken(tokenIdentifier)) {
			return jiraCloudService;
		}
		// If a new project management provider should be implemented, it can just be
		// added here

		return null;
	}

}
