package de.htwg.aume.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import de.htwg.aume.model.Session;
import de.htwg.aume.repository.SessionRepository;

@RestController
public class Controller {

	@Autowired
	SessionRepository sessionRepo;

	@PostMapping(value = "createSession")
	public ResponseEntity<Session> createSession() {
		Set<UUID> usedUuids = sessionRepo.findAll().stream().map(s -> s.getSessionID()).collect(Collectors.toSet());
		List<UUID> sessionUuids = Stream.generate(UUID::randomUUID).filter(s -> !usedUuids.contains(s)).limit(3)
				.collect(Collectors.toList());
		final var session = new Session(sessionUuids.get(0), sessionUuids.get(1), sessionUuids.get(2));
		sessionRepo.save(session);
		return new ResponseEntity<Session>(session, HttpStatus.CREATED);
	}

	@GetMapping(value = "getSession/{sessionID}")
	public @ResponseBody Session getSession(@PathVariable UUID sessionID) {
		Session session = sessionRepo.findBySessionID(sessionID);
		if (session == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found");
		}
		return session;
	}

}