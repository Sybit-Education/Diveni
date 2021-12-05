package de.htwg.aume.principals;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import de.htwg.aume.Utils;
import org.junit.jupiter.api.Test;

import lombok.val;

public class MemberPrincipalTest {

    @Test
    public void principal_creationWorks() {
        val sessionID = Utils.generateRandomID();
        val memberID = Utils.generateRandomID();
        val principal = new MemberPrincipal(sessionID, memberID);
        assertEquals(principal.getSessionID(), sessionID);
        assertEquals(principal.getMemberID(), memberID);
        assertEquals(principal.getName(), memberID.toString());
    }

    @Test
    public void principal_equalsAnother() {
        val sessionID = Utils.generateRandomID();
        val memberID = Utils.generateRandomID();

        val principal = new MemberPrincipal(sessionID, memberID);
        val samePrincipal = new MemberPrincipal(sessionID, memberID);

        val otherPrincipal = new MemberPrincipal(Utils.generateRandomID(), Utils.generateRandomID());

        assertNotEquals(principal, otherPrincipal);
        assertEquals(principal, samePrincipal);
        assertEquals(principal.hashCode(), samePrincipal.hashCode());
    }

}
