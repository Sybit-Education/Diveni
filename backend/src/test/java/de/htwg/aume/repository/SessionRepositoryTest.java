package de.htwg.aume.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import de.htwg.aume.Utils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import de.htwg.aume.model.AvatarAnimal;
import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.model.SessionState;
import lombok.val;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class SessionRepositoryTest {

	@Autowired
	private SessionRepository sessionRepo;

	@Test
	public void saveSession_returnsSession() {
		val adminID = Utils.generateRandomID();
		val membersID = Utils.generateRandomID();
		val session = new Session(new ObjectId(), adminID, membersID, null, null, new ArrayList<Member>(),
				new HashMap<>(), new ArrayList<>(), SessionState.WAITING_FOR_MEMBERS);

		assertEquals(session, sessionRepo.save(session));
	}

	@Test
	public void addMemberToSession_addsMember() {
		val adminID = Utils.generateRandomID();
		val membersID = Utils.generateRandomID();
		val member = new Member(Utils.generateRandomID(), "John", "0x0a0a0a", AvatarAnimal.CAMEL, null, null);
		val members = new ArrayList<Member>();
		members.add(member);
		val session = new Session(new ObjectId(), adminID, membersID, null, null, members, new HashMap<>(),
				new ArrayList<>(), SessionState.WAITING_FOR_MEMBERS);

		assertEquals(session, sessionRepo.save(session));
	}

}
