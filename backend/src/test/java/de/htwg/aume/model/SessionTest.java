package de.htwg.aume.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lombok.val;

public class SessionTest {

	@Test
	public void equal_works() {
		val sessionIdBefore = UUID.randomUUID();
		val adminIdBefore = UUID.randomUUID();
		val membersIdBefore = UUID.randomUUID();
		val session = new Session(sessionIdBefore, adminIdBefore, membersIdBefore,
				new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS);
		val sameSession = new Session(sessionIdBefore, adminIdBefore, membersIdBefore,
				new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS);
		val otherSession = new Session(UUID.randomUUID(), adminIdBefore, membersIdBefore,
				new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS);

		assertEquals(session, sameSession);
		assertNotEquals(session, otherSession);
		assertEquals(session.hashCode(), sameSession.hashCode());
	}

	@Test
	public void updateEstimation_works() {
		val memberID1 = UUID.randomUUID();
		val memberID2 = UUID.randomUUID();
		val member1 = new Member(memberID1, null, null, null, null);
		val member2 = new Member(memberID2, null, null, null, null);
		val members = Arrays.asList(member1, member2);
		val vote = 5;

		val session = new Session(null, null, null, members, SessionState.WAITING_FOR_MEMBERS);
		val result = session.updateEstimation(member1.getMemberID(), vote);

		val resultMember = result.getMembers().stream()
				.filter(m -> m.getMemberID().equals(member1.getMemberID())).findFirst().get();
		assertTrue(resultMember.getCurrentEstimation().isPresent());
		assertEquals(resultMember.getCurrentEstimation().get(), vote);
	}

	@Test
	public void resetEstimations_works() {
		val memberID1 = UUID.randomUUID();
		val memberID2 = UUID.randomUUID();
		val member1 = new Member(memberID1, null, null, null, 3);
		val member2 = new Member(memberID2, null, null, null, 5);
		val members = Arrays.asList(member1, member2);
		val session = new Session(null, null, null, members, SessionState.WAITING_FOR_MEMBERS);

		val result = session.resetEstimations();

		assertTrue(result.getMembers().stream().allMatch(m -> m.getCurrentEstimation().isEmpty()));
	}

}
