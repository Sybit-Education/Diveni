package de.htwg.aume.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import de.htwg.aume.model.Session;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import de.htwg.aume.repository.SessionRepository;
import lombok.val;

@Service
public class WebSocketService {

	@Autowired
	SessionRepository sessionRepo;

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private DatabaseService databaseService;

	private Optional<AdminPrincipal> admin;

	private List<MemberPrincipal> members = new ArrayList<>();

	public synchronized void addMemberIfNew(MemberPrincipal member) {
		members = members.stream().filter(m -> !m.getName().equals(member.getName())).collect(Collectors.toList());
		members.add(member);
	}

	public void setAdminUser(AdminPrincipal principal) {
		this.admin = Optional.of(principal);
	}

	public void sendAddedMemberMessage() {
		val session = getSessionFromID();
		if (admin.isPresent() && session.isPresent()) {
			simpMessagingTemplate.convertAndSendToUser(admin.get().getName(), "/updates/membersUpdated",
					session.get().getMembers());
		}
	}

	private Optional<Session> getSessionFromID() {
		return Optional.ofNullable(sessionRepo.findBySessionID(admin.get().getSessionID()));
	}

	public void sendStartEstimationMessages() {
		for (val member : members) {
			simpMessagingTemplate.convertAndSendToUser(member.getName(), "/updates/startEstimation", "");
		}
	}
}