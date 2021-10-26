package de.htwg.aume.controller;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.model.Session;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import de.htwg.aume.repository.SessionRepository;
import de.htwg.aume.service.DatabaseService;
import de.htwg.aume.service.WebSocketService;

import lombok.val;

@Controller
public class WebsocketController {

	@Autowired
	private WebSocketService webSocketService;

	@Autowired
	DatabaseService databaseService;

	@MessageMapping("/registerAdminUser")
	public void registerAdminUser(Principal principal) {
		if (!(principal instanceof AdminPrincipal)) {
			new ResponseStatusException(HttpStatus.UNAUTHORIZED, "not an admin");
		}
		ControllerUtils.getSessionOrThrowResponse(databaseService, ((AdminPrincipal) principal).getSessionID());
		webSocketService.setAdminUser((AdminPrincipal) principal);
	}

	@MessageMapping("/registerMember")
	public void joinMember(Principal principal) {
		if (!(principal instanceof MemberPrincipal)) {
			new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "not an admin");
		}
		ControllerUtils.getSessionByMemberIDOrThrowResponse(databaseService, ((MemberPrincipal) principal).getSessionID());
		webSocketService.addMemberIfNew((MemberPrincipal) principal);
		webSocketService.sendMembersUpdate();
	}

	@MessageMapping("/startEstimation")
	public void startEstimation(Principal principal) {
		System.out.println("Called start Estimation");
		if (!(principal instanceof AdminPrincipal)) {
			new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorMessages.notAnAdminErrorMessage);
		}
		ControllerUtils.getSessionOrThrowResponse(databaseService, ((AdminPrincipal) principal).getSessionID());
		webSocketService.sendStartEstimationMessages();
	}

	@MessageMapping("/vote")
	public synchronized void processVote(@RequestParam int vote, MemberPrincipal member) {
		val session = ControllerUtils.getSessionByMemberIDOrThrowResponse(databaseService, member.getMemberID()).updateEstimation(member.getMemberID(), vote);
		databaseService.saveSession(session);
		webSocketService.sendMembersUpdate();
	}

}
