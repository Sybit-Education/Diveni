package de.htwg.aume.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.repository.SessionRepository;
import lombok.val;

@CrossOrigin(origins = "http://localhost:8080/")
@RestController
public class RoutesController {

	public static String sessionNotFoundErrorMessage = "session not found";

	public static String memberExistsErrorMessage = "member already exists";

	@Autowired
	SessionRepository sessionRepo;

	@PostMapping(value = "/sessions")
	public ResponseEntity<Session> createSession() {
		val usedUuids = sessionRepo.findAll().stream().map(s -> s.getSessionID()).collect(Collectors.toSet());
		val sessionUuids = Stream.generate(UUID::randomUUID).filter(s -> !usedUuids.contains(s)).limit(3)
				.collect(Collectors.toList());
		val session = new Session(sessionUuids.get(0), sessionUuids.get(1), sessionUuids.get(2),
				new ArrayList<Member>());
		sessionRepo.save(session);
		return new ResponseEntity<Session>(session, HttpStatus.CREATED);
	}

	@PostMapping(value = "/sessions/{sessionID}/join")
	public ResponseEntity<String> joinSession(@PathVariable UUID sessionID, @RequestBody Member member) {
		val successful = addMemberToSession(sessionID, member);
		if (!successful) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, sessionNotFoundErrorMessage);
		}
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@GetMapping(value = "/sessions/{sessionID}")
	public @ResponseBody Session getSession(@PathVariable UUID sessionID) {
		val session = sessionRepo.findBySessionID(sessionID);
		if (session == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, sessionNotFoundErrorMessage);
		}
		return session;
	}

	private synchronized boolean addMemberToSession(UUID sessionID, Member member) {
		val session = sessionRepo.findBySessionID(sessionID);
		if (session == null) {
			return false;
		}
		List<Member> members = session.getMembers().stream().map(m -> m).collect(Collectors.toList());
		if (members.stream().anyMatch(m -> m.getMemberID().equals(member.getMemberID()))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, memberExistsErrorMessage);
		}
		members.add(member);
		sessionRepo.save(session.copyWith(null, null, null, members));
		return true;
	}

}