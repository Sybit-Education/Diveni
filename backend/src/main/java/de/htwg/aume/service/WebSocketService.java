package de.htwg.aume.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import de.htwg.aume.model.MemberUpdateCommand;
import de.htwg.aume.model.Session;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import lombok.val;

@Service
public class WebSocketService {

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private DatabaseService databaseService;

	private Optional<AdminPrincipal> admin;

	private List<MemberPrincipal> members = new ArrayList<>();

	public synchronized void addMemberIfNew(MemberPrincipal member) {
		members = members.stream().filter(m -> !m.getName().equals(member.getName()))
				.collect(Collectors.toList());
		members.add(member);
	}

	public synchronized void removeMember(MemberPrincipal member) {
		members = members.stream().filter(m -> !m.getMemberID().equals(member.getMemberID())).collect(Collectors.toList());
	}

	public void setAdminUser(AdminPrincipal principal) {
		this.admin = Optional.of(principal);
	}

	public void sendMembersUpdate() {
		admin.ifPresent(admin -> {
			databaseService.getSessionByID(admin.getSessionID()).ifPresent(session -> {
				simpMessagingTemplate.convertAndSendToUser(admin.getName(),
						"/updates/membersUpdated", session.getMembers());
			});
		});
	}

	public void sendSessionStateToMembers() {
		members.stream()
				.forEach(member -> sendSessionStateToMember(member.getMemberID().toString()));
	}


	public void sendSessionStateToMember(String memberID) {
		admin.ifPresent(admin -> {
			databaseService.getSessionByID(admin.getSessionID()).ifPresent(session -> {
				simpMessagingTemplate.convertAndSendToUser(memberID, "/updates/member",
						session.getSessionState().toString());
			});
		});
	}

	public void closeSessionConnections(Session session) {
		val sessionMemberIDs = session.getMembers().stream().map(m -> m.getMemberID().toString()).collect(Collectors.toList());
		val sessionMembers = members.stream().filter(m -> sessionMemberIDs.contains(m.getName())).collect(Collectors.toList());
		for (val member : sessionMembers) {
			simpMessagingTemplate.convertAndSendToUser(member.getName(), "/updates/member", MemberUpdateCommand.SESSION_CLOSED.toString());
		}
		admin = Optional.empty();
		members.removeAll(sessionMembers);
	}
}
