package de.htwg.aume.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import de.htwg.aume.model.Member;
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
	}

	@MessageMapping("/unregister")
	public void removeMember(Principal principal) {
		if (principal instanceof MemberPrincipal) {
			webSocketService.removeMember((MemberPrincipal)principal);
			val session = ControllerUtils
				.getSessionByMemberIDOrThrowResponse(databaseService, ((MemberPrincipal)principal).getMemberID())
				.removeMember(((MemberPrincipal)principal).getMemberID());
			databaseService.saveSession(session);
			webSocketService.sendMembersUpdate();
		} else {
			val session = ControllerUtils
				.getSessionOrThrowResponse(databaseService, ((AdminPrincipal)principal).getSessionID());
			webSocketService.closeSession();
			databaseService.deleteSession(session);
		}
	}

	@MessageMapping("/startEstimation")
	public void startEstimation(AdminPrincipal principal) {
		ControllerUtils.getSessionOrThrowResponse(databaseService, principal.getSessionID());
		webSocketService.sendStartEstimationMessages();
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
		val session = ControllerUtils
				.getSessionOrThrowResponse(databaseService, principal.getSessionID())
				.resetEstimations();
		databaseService.saveSession(session);
		webSocketService.sendMembersUpdate();
		webSocketService.sendStartEstimationMessages();
	}
}
