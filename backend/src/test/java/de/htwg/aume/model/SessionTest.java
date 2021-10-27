package de.htwg.aume.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lombok.val;

public class SessionTest {

	@Test
	public void equal_works() {
		val sessionIdBefore = UUID.randomUUID();
		val adminIdBefore = UUID.randomUUID();
		val membersIdBefore = UUID.randomUUID();
		val session = new Session(sessionIdBefore, adminIdBefore, membersIdBefore, new ArrayList<Member>());
		val sameSession = new Session(sessionIdBefore, adminIdBefore, membersIdBefore, new ArrayList<Member>());
		val otherSession = new Session(UUID.randomUUID(), adminIdBefore, membersIdBefore, new ArrayList<Member>());

		assertEquals(session, sameSession);
		assertNotEquals(session, otherSession);
		assertEquals(session.hashCode(), sameSession.hashCode());
	}

	@Test
	public void copyWithSessionID_works() {
		val uuidBefore = UUID.randomUUID();
		val uuidAfter = UUID.randomUUID();
		val session = new Session(uuidBefore, UUID.randomUUID(), UUID.randomUUID(), new ArrayList<Member>());

		val result = session.copyWith(uuidAfter, null, null, null);

		assertNotEquals(session.getSessionID(), result.getSessionID());
		assertEquals(session.getSessionID(), uuidBefore);
		assertEquals(result.getSessionID(), uuidAfter);
		assertEquals(session.getAdminID(), result.getAdminID());
		assertEquals(session.getMembersID(), result.getMembersID());
		assertEquals(session.getMembers(), result.getMembers());
	}

	@Test
	public void copyWithAdminID_works() {
		val uuidBefore = UUID.randomUUID();
		val uuidAfter = UUID.randomUUID();
		val session = new Session(UUID.randomUUID(), uuidBefore, UUID.randomUUID(), new ArrayList<Member>());

		val result = session.copyWith(null, uuidAfter, null, null);

		assertEquals(session.getSessionID(), result.getSessionID());
		assertNotEquals(session.getAdminID(), result.getAdminID());
		assertEquals(session.getMembersID(), result.getMembersID());
		assertEquals(session.getMembers(), result.getMembers());
	}

	@Test
	public void copyWithMembersID_works() {
		val uuidBefore = UUID.randomUUID();
		val uuidAfter = UUID.randomUUID();
		val session = new Session(UUID.randomUUID(), UUID.randomUUID(), uuidBefore, new ArrayList<Member>());

		val result = session.copyWith(null, null, uuidAfter, null);

		assertEquals(session.getSessionID(), result.getSessionID());
		assertEquals(session.getAdminID(), result.getAdminID());
		assertNotEquals(session.getMembersID(), uuidAfter);
		assertEquals(result.getMembersID(), uuidAfter);
		assertEquals(session.getMembers(), result.getMembers());
	}

	@Test
	public void copyWithMultiple_works() {
		val adminIdBefore = UUID.randomUUID();
		val adminIdAfter = UUID.randomUUID();
		val sessionIdBefore = UUID.randomUUID();
		val sessionIdAfter = UUID.randomUUID();
		val session = new Session(sessionIdBefore, adminIdBefore, UUID.randomUUID(), new ArrayList<Member>());

		val result = session.copyWith(sessionIdAfter, adminIdAfter, null, null);

		assertEquals(sessionIdAfter, result.getSessionID());
		assertEquals(adminIdAfter, result.getAdminID());
		assertEquals(session.getMembersID(), result.getMembersID());
		assertEquals(session.getMembers(), result.getMembers());
	}

	@Test
	public void updateEstimation_works() {
		val memberID1 = UUID.randomUUID();
		val memberID2 = UUID.randomUUID();
		val member1 = new Member(memberID1, null, null, null, null);
		val member2 = new Member(memberID2, null, null, null, null);
		val members = Arrays.asList(member1, member2);
		val vote = 5;

		val session = new Session(null, null, null, members);
		val result = session.updateEstimation(member1.getMemberID(), vote);

		val resultMember = result.getMembers().stream().filter(m -> m.getMemberID().equals(member1.getMemberID())).findFirst().get();
		assertTrue(resultMember.getCurrentEstimation().isPresent());
		assertEquals(resultMember.getCurrentEstimation().get(), vote);
	}

}
