package de.htwg.aume.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.htwg.aume.Utils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.model.JoinInfo;
import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.model.SessionConfig;
import de.htwg.aume.model.SessionState;
import de.htwg.aume.service.DatabaseService;
import de.htwg.aume.service.projectmanagementproviders.jiraserver.JiraServerService;
import lombok.val;

@CrossOrigin(origins = { "http://localhost:8080", "https://pp.vnmz.de" })
@RestController
public class RoutesController {

	@Autowired
	JiraServerService jiraServerService;

	@Autowired
	DatabaseService databaseService;

	@PostMapping(value = "/sessions")
	public ResponseEntity<Map<String, Object>> createSession(
			@RequestParam("tokenIdentifier") Optional<String> tokenIdentifier,
			@RequestBody SessionConfig sessionConfig) {
		val usedSessionIDs = databaseService.getSessions().stream().map(Session::getSessionID)
				.collect(Collectors.toSet());
		val usedDatabaseIDs = databaseService.getSessions().stream().map(Session::getDatabaseID)
				.collect(Collectors.toSet());
		ObjectId databaseID = Stream.generate(ObjectId::new).filter(s -> !usedDatabaseIDs.contains(s)).findFirst()
				.get();
		List<String> sessionIds = Stream.generate(Utils::generateRandomID).filter(s -> !usedSessionIDs.contains(s))
				.limit(2).collect(Collectors.toList());
		val accessToken = tokenIdentifier.map(token -> jiraServerService.getAccessTokens().remove(token)).orElse(null);
		val session = new Session(databaseID, sessionIds.get(0), sessionIds.get(1), sessionConfig, UUID.randomUUID(),
				new ArrayList<>(), new HashMap<>(), new ArrayList<>(), SessionState.WAITING_FOR_MEMBERS, null,
				accessToken, null);
		databaseService.saveSession(session);
		val responseMap = Map.of("session", session, "adminCookie", session.getAdminCookie());
		return new ResponseEntity<>(responseMap, HttpStatus.CREATED);
	}

	@GetMapping(value = "/sessions")
	public ResponseEntity<Session> getExistingSession(@RequestParam("adminCookie") UUID adminCookie) {
		val session = databaseService.getSessionByAdminCookie(adminCookie);
		if (session.isPresent()) {
			return new ResponseEntity<>(session.get(), HttpStatus.OK);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage);
		}
	}

	@PostMapping(value = "/sessions/{sessionID}/join")
	public ResponseEntity<Map<String, Object>> joinSession(@PathVariable String sessionID,
			@RequestBody JoinInfo joinInfo) {
		val memberCookie = UUID.randomUUID();
		val session = addMemberToSession(sessionID,
				joinInfo.getMember().setMemberCookie(memberCookie).toggleActive(true),
				joinInfo.getPassword());
		val responseMap = Map.of("sessionConfig", session.getSessionConfig(), "memberCookie", memberCookie);
		return new ResponseEntity<>(responseMap, HttpStatus.OK);
	}

	@GetMapping(value = "/rejoinSession")
	public ResponseEntity<Map<String, Object>> rejoinSession(@RequestParam("memberCookie") UUID memberCookie) {
		val session = databaseService.getSessionByMemberCookie(memberCookie);
		if (session.isPresent()) {
			val member = session.get().getMemberByCookie(memberCookie).orElseThrow(
					() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.memberNotExistsErrorMessage));
			val updatedSession = session.get().setMemberActive(member.getMemberID());
			databaseService.saveSession(updatedSession);
			val responseMap = Map.of("sessionID", updatedSession.getSessionID(), "sessionConfig",
					updatedSession.getSessionConfig(), "member", updatedSession.getMemberByCookie(memberCookie).get());
			return new ResponseEntity<>(responseMap, HttpStatus.OK);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage);
		}
	}

	@GetMapping(value = "/sessions/{sessionID}")
	public @ResponseBody Session getSession(@PathVariable String sessionID) {
		return ControllerUtils.getSessionOrThrowResponse(databaseService, sessionID);
	}

	private synchronized Session addMemberToSession(String sessionID, Member member, Optional<String> password) {
		val session = databaseService.getSessionByID(sessionID).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
		List<Member> members = session.getMembers().stream().map(m -> m).collect(Collectors.toList());
		if (members.stream().anyMatch(m -> m.getMemberID().equals(member.getMemberID()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.memberExistsErrorMessage);
		}
		if (session.getSessionConfig().getPassword() != null) {
			if (!password.isPresent() || !password.get().equals(session.getSessionConfig().getPassword())) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessages.wrongPasswordMessage);
			}
		}
		members.add(member);
		databaseService.saveSession(session.updateMembers(members));
		return session;
	}
}
