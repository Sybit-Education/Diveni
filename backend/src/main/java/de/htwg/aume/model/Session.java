package de.htwg.aume.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.val;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Document("sessions")
public class Session {

	@Id
	private final UUID sessionID;

	private final UUID adminID;

	private final UUID membersID;

	private final List<Member> members;

	private final SessionState sessionState;


	public Session updateEstimation(UUID memberID, int vote) {
		val updatedMembers = members.stream()
				.map(m -> m.getMemberID().equals(memberID) ? m.updateEstimation(vote) : m)
				.collect(Collectors.toList());
		return new Session(sessionID, adminID, membersID, updatedMembers, sessionState);
	}

	public Session resetEstimations() {
		val updatedMembers =
				members.stream().map(m -> m.resetEstimation()).collect(Collectors.toList());
		return new Session(sessionID, adminID, membersID, updatedMembers, sessionState);
	}
	
    public Session updateMembers(List<Member> updatedMembers) {
		return new Session(sessionID, adminID, membersID, updatedMembers, sessionState);
    }
	
	public Session updateSessionState(SessionState updatedSessionState) {
		return new Session(sessionID, adminID, membersID, members, updatedSessionState);
	}

	public Session addMember(Member member) {
		var updatedMembers = new ArrayList<>(members);
		updatedMembers.add(member);
		return new Session(sessionID, adminID, membersID, updatedMembers, sessionState);
	}

	public Session removeMember(UUID memberID) {
		val updatedMembers = members.stream()
			.filter(m -> !m.getMemberID().equals(memberID))
			.collect(Collectors.toList());
			return new Session(sessionID, adminID, membersID, updatedMembers, sessionState);
		}

}
