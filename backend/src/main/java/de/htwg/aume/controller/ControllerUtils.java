package de.htwg.aume.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.model.Session;
import de.htwg.aume.service.DatabaseService;

public class ControllerUtils {

	private ControllerUtils() {
	}

	static Session getSessionByMemberIDOrThrowResponse(DatabaseService databaseService, String memberID) {
		return databaseService.getSessionByMemberID(memberID).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
	}

	static Session getSessionOrThrowResponse(DatabaseService databaseService, String sessionID) {
		return databaseService.getSessionByID(sessionID).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
	}

}
