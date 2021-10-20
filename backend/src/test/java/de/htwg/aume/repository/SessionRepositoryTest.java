package de.htwg.aume.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import de.htwg.aume.model.Session;
import lombok.val;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class SessionRepositoryTest {

	@Autowired
	private SessionRepository sessionRepo;

	@Test
	public void saveSession_returnsSession() {
		val sessionID = UUID.randomUUID();
		val adminID = UUID.randomUUID();
		val membersID = UUID.randomUUID();
		val session = new Session(sessionID, adminID, membersID);

		assertEquals(session, sessionRepo.save(session));
	}

}
