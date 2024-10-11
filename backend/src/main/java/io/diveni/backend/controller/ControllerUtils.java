/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend.controller;

import io.diveni.backend.model.Session;
import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import io.diveni.backend.service.DatabaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

public class ControllerUtils {

  private ControllerUtils() {}

  static Session getSessionByMemberIDOrThrowResponse(
      DatabaseService databaseService, String memberID) {
    return databaseService
        .getSessionByMemberID(memberID)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
  }

  static Session getSessionOrThrowResponse(DatabaseService databaseService, String sessionID) {
    return databaseService
        .getSessionByID(sessionID)
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND, ErrorMessages.sessionNotFoundErrorMessage));
  }

  static Session getSessionByPrincipalTypeOrThrowResponse(DatabaseService databaseService, Principal principal) {
    if (principal instanceof AdminPrincipal) {
      return getSessionOrThrowResponse(databaseService, ((AdminPrincipal) principal).getSessionID());
    } else if (principal instanceof MemberPrincipal) {
      return getSessionByMemberIDOrThrowResponse(databaseService, ((MemberPrincipal) principal).getMemberID());
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessages.principalNotFoundErrorMessage);
  }

}
