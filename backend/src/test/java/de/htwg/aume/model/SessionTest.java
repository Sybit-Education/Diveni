package de.htwg.aume.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import de.htwg.aume.Utils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import lombok.val;

public class SessionTest {

	@Test
	public void equal_works() {
		val adminIdBefore = Utils.generateRandomID();
		val membersIdBefore = Utils.generateRandomID();
		val sessionID = new ObjectId();
		val session = new Session(sessionID, adminIdBefore, membersIdBefore, null, new ArrayList<>(),
				SessionState.WAITING_FOR_MEMBERS);
		val sameSession = new Session(sessionID, adminIdBefore, membersIdBefore, null, new ArrayList<>(),
				SessionState.WAITING_FOR_MEMBERS);
		val otherSession = new Session(new ObjectId(), adminIdBefore, membersIdBefore, null, new ArrayList<>(),
				SessionState.WAITING_FOR_MEMBERS);

		assertEquals(session, sameSession);
		assertNotEquals(session, otherSession);
		assertEquals(session.hashCode(), sameSession.hashCode());
	}

	@Test
	public void updateEstimation_works() {
		val memberID1 = Utils.generateRandomID();
		val memberID2 = Utils.generateRandomID();
		val member1 = new Member(memberID1, null, null, null, null);
		val member2 = new Member(memberID2, null, null, null, null);
		val members = Arrays.asList(member1, member2);
		val vote = "5";

		val session = new Session(null, null, null, null, members, SessionState.WAITING_FOR_MEMBERS);
		val result = session.updateEstimation(member1.getMemberID(), vote);

		val resultMember = result.getMembers().stream().filter(m -> m.getMemberID().equals(member1.getMemberID()))
				.findFirst().get();
		assertTrue(resultMember.getCurrentEstimation().isPresent());
		assertEquals(resultMember.getCurrentEstimation().get(), vote);
	}

	@Test
	public void resetEstimations_works() {
		val memberID1 = Utils.generateRandomID();
		val memberID2 = Utils.generateRandomID();
		val member1 = new Member(memberID1, null, null, null, "3");
		val member2 = new Member(memberID2, null, null, null, "5");
		val members = Arrays.asList(member1, member2);
		val session = new Session(null, null, null, null, members, SessionState.WAITING_FOR_MEMBERS);

		val result = session.resetEstimations();

		assertTrue(result.getMembers().stream().allMatch(m -> m.getCurrentEstimation().isEmpty()));
	}

	@Test
	public void updateSessionState_works() {
		val oldSessionState = SessionState.WAITING_FOR_MEMBERS;
		val newSessionState = SessionState.START_VOTING;

		val session = new Session(null, null, null, null, new ArrayList<Member>(), oldSessionState);
		val result = session.updateSessionState(newSessionState);

		assertEquals(result.getSessionState(), newSessionState);
	}

	@Test
	public void addMember_works() {
		val memberID1 = Utils.generateRandomID();
		val member1 = new Member(memberID1, null, null, null, "3");
		val members = Arrays.asList(member1);
		val session = new Session(null, null, null, null, members, SessionState.WAITING_FOR_MEMBERS);
		val memberID2 = Utils.generateRandomID();
		val member2 = new Member(memberID2, null, null, null, "5");

		val result = session.addMember(member2);

		assertEquals(result.getMembers().size(), 2);
		assertTrue(result.getMembers().stream().anyMatch(m -> m.getMemberID().equals(memberID2)));
	}

	@Test
	public void removeMember_works() {
		val memberID1 = Utils.generateRandomID();
		val memberID2 = Utils.generateRandomID();
		val member1 = new Member(memberID1, null, null, null, "3");
		val member2 = new Member(memberID2, null, null, null, "5");
		val members = Arrays.asList(member1, member2);
		val session = new Session(null, null, null, null, members, SessionState.WAITING_FOR_MEMBERS);

		val result = session.removeMember(memberID1);

		assertTrue(result.getMembers().stream().noneMatch(m -> m.getMemberID().equals(memberID1)));
		assertEquals(result.getMembers().size(), 1);
	}

}
