package de.htwg.aume.principals;

import java.security.Principal;
import java.util.UUID;

public class AdminPrincipal implements Principal {

	private final UUID sessionID;

	private final UUID adminID;

	public AdminPrincipal(UUID sessionID, UUID adminID) {
		this.adminID = adminID;
		this.sessionID = sessionID;
	}

	@Override
	public String getName() {
		return adminID.toString();
	}

	public UUID getSessionID() {
		return sessionID;
	}

}
