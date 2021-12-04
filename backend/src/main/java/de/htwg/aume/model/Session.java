package de.htwg.aume.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@Document("sessions")
public class Session {

	@Id
	private final ObjectId databaseID;

	private final String sessionID;

	private final String adminID;

	private final SessionConfig sessionConfig;

	private final List<Member> members;

	private final SessionState sessionState;

	public Session updateEstimation(String memberID, String vote) {
		val updatedMembers = members.stream().map(m -> m.getMemberID().equals(memberID) ? m.updateEstimation(vote) : m)
				.collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, updatedMembers, sessionState);
	}
	public Session updateUserStories(List<UserStory> userStories) {
		val updatedSessionConfig = new SessionConfig(sessionConfig.getSet(), userStories,sessionConfig.getTimerSeconds().orElse(null), sessionConfig.getPassword());
		return new Session(databaseID, sessionID, adminID, updatedSessionConfig, members, sessionState);
	}

	public Session resetEstimations() {
		val updatedMembers = members.stream().map(Member::resetEstimation).collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, updatedMembers, sessionState);
	}

	public Session updateMembers(List<Member> updatedMembers) {
		return new Session(databaseID, sessionID, adminID, sessionConfig, updatedMembers, sessionState);
	}

	public Session updateSessionState(SessionState updatedSessionState) {
		return new Session(databaseID, sessionID, adminID, sessionConfig, members, updatedSessionState);
	}

	public Session addMember(Member member) {
		var updatedMembers = new ArrayList<>(members);
		updatedMembers.add(member);
		return new Session(databaseID, sessionID, adminID, sessionConfig, updatedMembers, sessionState);
	}

	public Session removeMember(String memberID) {
		val updatedMembers = members.stream().filter(m -> !m.getMemberID().equals(memberID))
				.collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, updatedMembers, sessionState);
	}

}
