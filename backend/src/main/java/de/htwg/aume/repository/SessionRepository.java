package de.htwg.aume.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.mongodb.repository.*;

import de.htwg.aume.model.Session;

public interface SessionRepository extends MongoRepository<Session, String> {

	Session findBySessionID(UUID sessionID);

	Session findByAdminCookie(UUID adminCookie);

	default Optional<Session> findByMemberID(UUID memberID) {
		return findAll().stream().filter(s -> s.getMembers().stream().anyMatch(m -> m.getMemberID().equals(memberID)))
				.findFirst();
	}
}