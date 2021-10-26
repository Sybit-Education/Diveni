package de.htwg.aume.principals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import lombok.val;

public class MemberPrincipalTest {

	@Test
	public void principal_creationWorks() throws Exception {
		val sessionID = UUID.randomUUID();
		val memberID = UUID.randomUUID();
		val principal = new MemberPrincipal(sessionID, memberID);
		assertEquals(principal.getSessionID(), sessionID);
		assertEquals(principal.getMemberID(), memberID);
		assertEquals(principal.getName(), memberID.toString());
	}

	@Test
	public void principal_equalsAnother() throws Exception {
		val sessionID = UUID.randomUUID();
		val memberID = UUID.randomUUID();

		val principal = new MemberPrincipal(sessionID, memberID);
		val samePrincipal = new MemberPrincipal(sessionID, memberID);

		val otherPrincipal = new MemberPrincipal(UUID.randomUUID(), UUID.randomUUID());

		assertNotEquals(principal, otherPrincipal);
		assertEquals(principal, samePrincipal);
		assertEquals(principal.hashCode(), samePrincipal.hashCode());
	}

}
