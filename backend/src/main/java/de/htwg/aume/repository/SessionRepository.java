package de.htwg.aume.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.mongodb.repository.*;

import de.htwg.aume.model.Session;

public interface SessionRepository extends MongoRepository<Session, String> {

	Session findBySessionID(String sessionID);

	Session findByAdminCookie(UUID adminCookie);

	default Optional<Session> findByMemberID(String memberID) {
		return findAll().stream().filter(s -> s.getMembers().stream().anyMatch(m -> m.getMemberID().equals(memberID)))
				.findFirst();
	}
}