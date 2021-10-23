package de.htwg.aume.principals;

import java.security.Principal;
import java.util.UUID;

public class MemberPrincipal implements Principal {

	private final UUID sessionID;

	private final UUID memberID;

	public MemberPrincipal(UUID sessionID, UUID memberID) {
		this.memberID = memberID;
		this.sessionID = sessionID;
	}

	@Override
	public String getName() {
		return memberID.toString();
	}

	public UUID getSessionID() {
		return sessionID;
	}

}
