package de.htwg.aume.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private final UUID adminCookie;

	private final List<Member> members;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private final Map<String, Integer> memberVoted;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private final List<String> currentHighlights;

	private final SessionState sessionState;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Date lastModified;

	static Comparator<String> estimationByIndex(List<String> set) {
		return Comparator.comparingInt((str) -> set.indexOf(str));
	}

	public Session updateEstimation(String memberID, String vote) {
		val updatedMembers = members.stream().map(m -> m.getMemberID().equals(memberID) ? m.updateEstimation(vote) : m)
				.collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, updatedMembers, memberVoted,
				currentHighlights, sessionState);
	}

	static Stream<String> getFilteredEstimationStream(List<Member> members) {
		return members.stream().map(Member::getCurrentEstimation).filter((estimation) -> estimation != null);
	}

	public Session selectHighlightedMembers() {
		for (Member member : this.members) {
			memberVoted.putIfAbsent(member.getMemberID(), 0);
		}
		Optional<String> maxEstimation = getFilteredEstimationStream(this.members)
				.max(estimationByIndex(sessionConfig.getSet()));
		Optional<String> minEstimation = getFilteredEstimationStream(this.members)
				.min(estimationByIndex(sessionConfig.getSet()));
		if (maxEstimation.equals(minEstimation)) {
			return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, members, memberVoted,
					new ArrayList<>(), sessionState);
		}
		val maxEstimationMembers = this.members.stream().filter((member) -> member.getCurrentEstimation() != null
				&& member.getCurrentEstimation().equals(maxEstimation.get())).collect(Collectors.toList());
		val maxOptions = memberVoted.entrySet().stream()
				.filter((entry) -> maxEstimationMembers.stream()
						.anyMatch((member) -> member.getMemberID().equals(entry.getKey())))
				.collect(Collectors.toList());
		val maxMemberID = Collections.max(maxOptions, Comparator.comparingInt(Map.Entry::getValue)).getKey();

		val minEstimationMembers = this.members.stream().filter((member) -> member.getCurrentEstimation() != null
				&& member.getCurrentEstimation().equals(minEstimation.get())).collect(Collectors.toList());
		val minOptions = memberVoted.entrySet().stream()
				.filter((entry) -> minEstimationMembers.stream()
						.anyMatch((member) -> member.getMemberID().equals(entry.getKey())))
				.collect(Collectors.toList());
		val minMemberID = Collections.max(minOptions, Comparator.comparingInt(Map.Entry::getValue)).getKey();

		val newVoted = memberVoted.entrySet().stream().map((entry) -> {
			if (entry.getKey().equals(maxMemberID) || entry.getKey().equals(minMemberID)) {
				return Map.entry(entry.getKey(), 0);
			} else {
				return Map.entry(entry.getKey(), entry.getValue() + 1);
			}
		}).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		val newHighlighted = List.of(minMemberID, maxMemberID);
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, members, newVoted,
				newHighlighted, sessionState);
	}

	public Session resetCurrentHighlights() {
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, members, memberVoted,
				new ArrayList<>(), sessionState);
	}

	public Session updateUserStories(List<UserStory> userStories) {
		val updatedSessionConfig = new SessionConfig(sessionConfig.getSet(), userStories,
				sessionConfig.getTimerSeconds().orElse(null), sessionConfig.getPassword());
		return new Session(databaseID, sessionID, adminID, updatedSessionConfig, adminCookie, members, memberVoted,
				currentHighlights, sessionState);
	}

	public Session resetEstimations() {
		val updatedMembers = members.stream().map(m -> m.resetEstimation()).collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, updatedMembers, memberVoted,
				currentHighlights, sessionState);
	}

	public Session updateMembers(List<Member> updatedMembers) {
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, updatedMembers, memberVoted,
				currentHighlights, sessionState);
	}

	public Session updateSessionState(SessionState updatedSessionState) {
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, members, memberVoted,
				currentHighlights, updatedSessionState);
	}

	public Session addMember(Member member) {
		var updatedMembers = new ArrayList<>(members);
		updatedMembers.add(member);
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, updatedMembers, memberVoted,
				currentHighlights, sessionState);
	}

	public Session setMemberInactive(String memberID) {
		val updatedMembers = members.stream().map(m -> m.getMemberID().equals(memberID) ? m.toggleActive(false) : m)
				.collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, updatedMembers, memberVoted,
				currentHighlights, sessionState);
	}

	public Session setMemberActive(String memberID) {
		val updatedMembers = members.stream().map(m -> m.getMemberID().equals(memberID) ? m.toggleActive(true) : m)
				.collect(Collectors.toList());
		return new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, updatedMembers, memberVoted,
				currentHighlights, sessionState);
	}

	public Session setLastModified(Date lastModified) {
		val session = new Session(databaseID, sessionID, adminID, sessionConfig, adminCookie, members, memberVoted,
				currentHighlights, sessionState);
		session.lastModified = lastModified;
		return session;
	}
}
