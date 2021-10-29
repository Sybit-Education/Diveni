package de.htwg.aume.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import de.htwg.aume.model.SessionState;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
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
	public void registerAdminUser(AdminPrincipal principal) {
		ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
		webSocketService.setAdminUser((AdminPrincipal) principal);
	}

	@MessageMapping("/registerMember")
	public void joinMember(MemberPrincipal principal) {
		ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
		webSocketService.addMemberIfNew((MemberPrincipal) principal);
		webSocketService.sendMembersUpdate();
		webSocketService.sendSessionStateToMember(principal.getName());
	}

	@MessageMapping("/startVoting")
	public void startEstimation(AdminPrincipal principal) {
		ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
		val session = ControllerUtils
				.getSessionOrThrowResponse(databaseService, principal.getSessionID())
				.updateSessionState(SessionState.START_VOTING );
		databaseService.saveSession(session);
		webSocketService.sendSessionStateToMembers();
	}

	@MessageMapping("/vote")
	public synchronized void processVote(@Payload int vote, MemberPrincipal member) {
		val session = ControllerUtils
				.getSessionByMemberIDOrThrowResponse(databaseService, member.getMemberID())
				.updateEstimation(member.getMemberID(), vote);
		databaseService.saveSession(session);
		webSocketService.sendMembersUpdate();
	}

	@MessageMapping("/restart")
	public synchronized void restartVote(AdminPrincipal principal) {
		val session =
				ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID())
						.resetEstimations();
		databaseService.saveSession(session);
		webSocketService.sendMembersUpdate();
		webSocketService.sendSessionStateToMembers();
	}

}
