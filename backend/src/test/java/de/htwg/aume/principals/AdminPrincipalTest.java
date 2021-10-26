package de.htwg.aume.principals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.val;

public class AdminPrincipalTest {

	@Test
	public void principal_creationWorks() throws Exception {
		val sessionID = UUID.randomUUID();
		val adminID = UUID.randomUUID();
		val principal = new AdminPrincipal(sessionID, adminID);
		assertEquals(principal.getSessionID(), sessionID);
		assertEquals(principal.getAdminID(), adminID);
		assertEquals(principal.getName(), adminID.toString());
	}

	@Test
	public void principal_equalsAnother() throws Exception {
		val sessionID = UUID.randomUUID();
		val adminID = UUID.randomUUID();

		val principal = new AdminPrincipal(sessionID, adminID);
		val samePrincipal = new AdminPrincipal(sessionID, adminID);

		val otherPrincipal = new AdminPrincipal(UUID.randomUUID(), UUID.randomUUID());

		assertNotEquals(principal, otherPrincipal);
		assertEquals(principal, samePrincipal);
		assertEquals(principal.hashCode(), samePrincipal.hashCode());
	}

}
