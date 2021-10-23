package de.htwg.aume.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import de.htwg.aume.repository.SessionRepository;
import de.htwg.aume.service.WebSocketService;

@Controller
public class WebsocketController {

	@Autowired
	SessionRepository sessionRepo;

	private final WebSocketService webSocketService;

	WebsocketController(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	@MessageMapping("/registerAdminUser")
	public void registerAdminUser(Principal principal) {
		if (!(principal instanceof AdminPrincipal)) {
			new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not an admin");
		}
		Optional.ofNullable(sessionRepo.findBySessionID(((AdminPrincipal) principal).getSessionID()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found"));
		webSocketService.setAdminUser((AdminPrincipal) principal);
	}

	@MessageMapping("/registerMember")
	public void joinMember(Principal principal) {
		if (!(principal instanceof MemberPrincipal)) {
			new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "not an admin");
		}
		Optional.ofNullable(sessionRepo.findBySessionID(((MemberPrincipal) principal).getSessionID()))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not found"));
		webSocketService.addMemberIfNew((MemberPrincipal) principal);
		webSocketService.sendAddedMemberMessage();
	}

	// @MessageMapping("/startEstimation")
	// public void startEstimation(Principal principal) {
	// System.out.println("Called start Esimtaion");
	// if (!(principal instanceof AdminPrincipal)) {
	// new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not an admin");
	// }
	// val session =
	// Optional.ofNullable(sessionRepo.findBySessionID(((AdminPrincipal)
	// principal).getSessionID()))
	// .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session
	// not found"));
	// webSocketService.addAdminUser((AdminPrincipal) principal);
	// webSocketService.sendStartEstimationMessages();
	// }

}
