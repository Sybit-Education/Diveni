package de.htwg.aume.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.controller.ErrorMessages;
import de.htwg.aume.model.Session;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import lombok.Getter;
import lombok.val;

@Service
public class WebSocketService {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Getter
	private Map<AdminPrincipal, Set<MemberPrincipal>> memberMap = new HashMap<>();

	Entry<AdminPrincipal, Set<MemberPrincipal>> getSessionEntry(UUID sessionID) {
		return memberMap.entrySet().stream().filter(e -> e.getKey().getSessionID().equals(sessionID)).findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						ErrorMessages.sessionNotFoundErrorMessage));
	}

	public synchronized void addMemberIfNew(MemberPrincipal member) {
		val sessionEntry = getSessionEntry(member.getSessionID());
		val members = sessionEntry.getValue().stream().filter(m -> !m.getName().equals(member.getName()))
				.collect(Collectors.toSet());
		members.add(member);
		memberMap.put(sessionEntry.getKey(), members);
	}

	public synchronized void removeMember(MemberPrincipal member) {
		val sessionEntry = getSessionEntry(member.getSessionID());
		val members = sessionEntry.getValue().stream().filter(m -> !m.getMemberID().equals(member.getMemberID()))
				.collect(Collectors.toSet());
		memberMap.put(sessionEntry.getKey(), members);
	}

	public void setAdminUser(AdminPrincipal principal) {
		memberMap.putIfAbsent(principal, new HashSet<>());
	}

	public void sendMembersUpdate(Session session) {
		val sessionEntry = getSessionEntry(session.getSessionID());
		simpMessagingTemplate.convertAndSendToUser(sessionEntry.getKey().getName(), "/updates/membersUpdated",
				session.getMembers());
		sendMembersUpdateToMembers(session);
	}

	public void sendMembersUpdateToMembers(Session session) {
		getSessionEntry(session.getSessionID()).getValue().stream()
				.forEach(member -> simpMessagingTemplate.convertAndSendToUser(
						member.getMemberID().toString(), "/updates/membersUpdated",
						session.getMembers())
				);
	}

	public void sendSessionStateToMembers(Session session) {
		getSessionEntry(session.getSessionID()).getValue().stream()
				.forEach(member -> sendSessionStateToMember(session, member.getMemberID().toString()));
	}

	public void sendUpdatedUserStoriesToMembers(Session session) {
		getSessionEntry(session.getSessionID()).getValue().stream()
				.forEach(member -> sendUpdatedUserStoriesToMember(session, member.getMemberID().toString()));
	}

	public void sendSessionStateToMember(Session session, String memberID) {
		simpMessagingTemplate.convertAndSendToUser(memberID, "/updates/member", session.getSessionState().toString());
	}

	public void sendUpdatedUserStoriesToMember(Session session, String memberID) {
		simpMessagingTemplate.convertAndSendToUser(memberID, "/updates/userStories", session.getSessionConfig().getUserStories());
	}

	public void removeSession(Session session) {
		memberMap.remove(getSessionEntry(session.getSessionID()).getKey());
	}
}
