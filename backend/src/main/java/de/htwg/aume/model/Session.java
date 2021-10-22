package de.htwg.aume.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Document("sessions")
public class Session {

	@Id
	private final UUID sessionID;

	private final UUID adminID;

	private final UUID membersID;

	private final List<Member> members;

	public Session copyWith(UUID sessionID, UUID adminID, UUID membersID, List<Member> members) {
		var tmpSession = this;
		if (sessionID != null) {
			tmpSession = new Session(sessionID, this.adminID, this.membersID, this.members);
		}
		if (adminID != null) {
			tmpSession = new Session(this.sessionID, adminID, this.membersID, this.members);
		}
		if (membersID != null) {
			tmpSession = new Session(this.sessionID, this.adminID, membersID, this.members);
		}
		if (members != null) {
			tmpSession = new Session(this.sessionID, this.adminID, this.membersID, members);
		}
		return tmpSession;
	}
}
