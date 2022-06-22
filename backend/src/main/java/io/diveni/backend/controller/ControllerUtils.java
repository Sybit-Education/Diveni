package io.diveni.backend.controller;

import io.diveni.backend.model.Session;
import io.diveni.backend.service.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
