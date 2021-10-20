package de.htwg.aume.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.*;

import de.htwg.aume.model.Session;

public interface SessionRepository extends MongoRepository<Session, String> {

	Session findBySessionID(UUID sessionID);

}