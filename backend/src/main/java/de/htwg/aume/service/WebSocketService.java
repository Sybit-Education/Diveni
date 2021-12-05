package de.htwg.aume.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.controller.ErrorMessages;
import de.htwg.aume.model.Session;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import de.htwg.aume.principals.SessionPrincipals;
import lombok.Getter;
import lombok.val;

@Service
public class WebSocketService {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Getter
	private List<SessionPrincipals> sessionPrincipalList = List.of();

	public SessionPrincipals getSessionPrincipals(String sessionID) {
		return sessionPrincipalList.stream().filter(s -> s.sessionID().equals(sessionID)).findFirst().orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
	}

	public synchronized void addMemberIfNew(MemberPrincipal member) {
		val sessionPrincipals = getSessionPrincipals(member.getSessionID());
		val updatedMembers = Stream
				.concat(sessionPrincipals.memberPrincipals().stream()
						.filter(m -> !m.getName().equals(member.getName())), Stream.of(member))
				.collect(Collectors.toSet());
		sessionPrincipalList = sessionPrincipalList.stream().map(p -> {
			if (p == sessionPrincipals)
				return p.memberPrincipals(updatedMembers);
			else
				return p;
		}).collect(Collectors.toList());
	}

	public synchronized void removeMember(MemberPrincipal member) {
		val sessionPrincipals = getSessionPrincipals(member.getSessionID());
		val updatedMembers = sessionPrincipals.memberPrincipals().stream()
				.filter(m -> !m.getMemberID().equals(member.getMemberID())).collect(Collectors.toSet());
		sessionPrincipalList = sessionPrincipalList.stream().map(p -> {
			if (p == sessionPrincipals)
				return p.memberPrincipals(updatedMembers);
			else
				return p;
		}).collect(Collectors.toList());
	}

	public synchronized void removeAdmin(AdminPrincipal admin) {
		sessionPrincipalList = sessionPrincipalList.stream().map(p -> {
			if (p.adminPrincipal() == admin) {
				return p.adminPrincipal(null);
			} else {
				return p;
			}
		}).collect(Collectors.toList());
	}

	public synchronized void setAdminUser(AdminPrincipal principal) {
		if (sessionPrincipalList.stream().anyMatch(p -> p.sessionID().equals(principal.getSessionID()))) {
			sessionPrincipalList = sessionPrincipalList.stream().map(p -> {
				if (p.sessionID().equals(principal.getSessionID()))
					return p.adminPrincipal(principal);
				else
					return p;
			}).collect(Collectors.toList());
		} else {
			sessionPrincipalList = Stream
					.concat(sessionPrincipalList.stream(),
							Stream.of(new SessionPrincipals(principal.getSessionID(), principal, Set.of())))
					.collect(Collectors.toList());
		}
	}

	public void sendMembersUpdate(Session session) {
		val sessionPrincipals = getSessionPrincipals(session.getSessionID());
		simpMessagingTemplate.convertAndSendToUser(sessionPrincipals.adminPrincipal().getName(),
				"/updates/membersUpdated", session.getMembers());
		sendMembersUpdateToMembers(session);
	}

	public void sendMembersUpdateToMembers(Session session) {
		getSessionPrincipals(session.getSessionID()).memberPrincipals()
				.forEach(member -> simpMessagingTemplate.convertAndSendToUser(
						member.getMemberID().toString(), "/updates/membersUpdated",
						session.getMembers())
				);
	}

	public void sendSessionStateToMembers(Session session) {
		getSessionPrincipals(session.getSessionID()).memberPrincipals().stream()
				.forEach(member -> sendSessionStateToMember(session, member.getMemberID().toString()));
	}

	public void sendUpdatedUserStoriesToMembers(Session session) {
		getSessionPrincipals(session.getSessionID()).memberPrincipals()
				.forEach(member -> sendUpdatedUserStoriesToMember(session, member.getMemberID().toString()));
	}

	public void sendSessionStateToMember(Session session, String memberID) {
		simpMessagingTemplate.convertAndSendToUser(memberID, "/updates/member", session.getSessionState().toString());
	}

	public void sendUpdatedUserStoriesToMember(Session session, String memberID) {
		simpMessagingTemplate.convertAndSendToUser(memberID, "/updates/userStories",
				session.getSessionConfig().getUserStories());
	}

	public void removeSession(Session session) {
		sessionPrincipalList = sessionPrincipalList.stream().filter(p -> !p.sessionID().equals(session.getSessionID()))
				.collect(Collectors.toList());
	}
}
