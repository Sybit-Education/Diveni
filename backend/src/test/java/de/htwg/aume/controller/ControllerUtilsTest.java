package de.htwg.aume.controller;

import de.htwg.aume.Utils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.UUID;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.model.Session;
import de.htwg.aume.service.DatabaseService;

@ExtendWith(MockitoExtension.class)
public class ControllerUtilsTest {

    @Mock
    DatabaseService databaseServiceMock;

    @Test
    public void getExistingSession_isCorrect() {
        Session session = mock(Session.class);
        doReturn(Optional.of(session)).when(databaseServiceMock).getSessionByID(any());

        assertEquals(session, ControllerUtils.getSessionOrThrowResponse(databaseServiceMock, Utils.generateRandomID()));
    }

    @Test
    public void getEmptySession_isError() {
        doReturn(Optional.empty()).when(databaseServiceMock).getSessionByID(any());

        assertThrows(ResponseStatusException.class, () -> ControllerUtils.getSessionOrThrowResponse(databaseServiceMock, Utils.generateRandomID()));
    }

    @Test
    public void getExistingMemberSession_isCorrect() {
        Session session = mock(Session.class);
        doReturn(Optional.of(session)).when(databaseServiceMock).getSessionByMemberID(any());

        assertEquals(session, ControllerUtils.getSessionByMemberIDOrThrowResponse(databaseServiceMock, Utils.generateRandomID()));
    }

    @Test
    public void getEmptyMemberSession_isError() {
        doReturn(Optional.empty()).when(databaseServiceMock).getSessionByMemberID(any());

        assertThrows(ResponseStatusException.class, () -> ControllerUtils.getSessionByMemberIDOrThrowResponse(databaseServiceMock, Utils.generateRandomID()));
    }
}
