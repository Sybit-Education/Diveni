package io.diveni.backend.controller;

import io.diveni.backend.Utils;
import io.diveni.backend.model.Session;
import io.diveni.backend.service.DatabaseService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class ControllerUtilsTest {

  @Mock DatabaseService databaseServiceMock;

  @Test
  public void getExistingSession_isCorrect() {
    Session session = mock(Session.class);
    doReturn(Optional.of(session)).when(databaseServiceMock).getSessionByID(any());

    Assertions.assertEquals(
        session,
        ControllerUtils.getSessionOrThrowResponse(databaseServiceMock, Utils.generateRandomID()));
  }

  @Test
  public void getEmptySession_isError() {
    doReturn(Optional.empty()).when(databaseServiceMock).getSessionByID(any());

    assertThrows(
        ResponseStatusException.class,
        () ->
            ControllerUtils.getSessionOrThrowResponse(
                databaseServiceMock, Utils.generateRandomID()));
  }

  @Test
  public void getExistingMemberSession_isCorrect() {
    Session session = mock(Session.class);
    doReturn(Optional.of(session)).when(databaseServiceMock).getSessionByMemberID(any());

    Assertions.assertEquals(
        session,
        ControllerUtils.getSessionByMemberIDOrThrowResponse(
            databaseServiceMock, Utils.generateRandomID()));
  }

  @Test
  public void getEmptyMemberSession_isError() {
    doReturn(Optional.empty()).when(databaseServiceMock).getSessionByMemberID(any());

    assertThrows(
        ResponseStatusException.class,
        () ->
            ControllerUtils.getSessionByMemberIDOrThrowResponse(
                databaseServiceMock, Utils.generateRandomID()));
  }
}
