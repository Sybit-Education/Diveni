package de.htwg.aume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.model.SessionState;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import lombok.val;

public class WebSocketServiceTest {

    private static final String ADMIN_UPDATES = "/updates/membersUpdated";

    private static final String MEMBER_UPDATES = "/updates/member";

    @Mock
    SimpMessagingTemplate simpMessagingTemplateMock;

    @InjectMocks
    private WebSocketService webSocketService;

    private AdminPrincipal defaultAdminPrincipal = new AdminPrincipal(UUID.randomUUID(), UUID.randomUUID());

    private MemberPrincipal defaultMemberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(),
            UUID.randomUUID());

    @BeforeEach
    public void initEach() {
        MockitoAnnotations.openMocks(this);
    }

    void setDefaultAdminPrincipal(Set<MemberPrincipal> members) throws Exception {
        val memberMapField = WebSocketService.class.getDeclaredField("memberMap");
        memberMapField.setAccessible(true);
        val memberMap = new HashMap<AdminPrincipal, Set<MemberPrincipal>>();
        memberMap.put(defaultAdminPrincipal, members);
        memberMapField.set(webSocketService, memberMap);
    }

    @Test
    public void setAdmin_isAdded() throws Exception {
        val adminPrincipal = new AdminPrincipal(UUID.randomUUID(), UUID.randomUUID());

        webSocketService.setAdminUser(adminPrincipal);

        assertTrue(webSocketService.getMemberMap().containsKey(adminPrincipal));
    }

    @Test
    public void getSessionEntry_isCorrect() throws Exception {
        setDefaultAdminPrincipal(new HashSet<>());

        val entry = webSocketService.getSessionEntry(defaultAdminPrincipal.getSessionID());

        assertEquals(Map.entry(defaultAdminPrincipal, Set.of()), entry);
    }

    @Test
    public void getMissingSessionEntry_isError() throws Exception {
        assertThrows(ResponseStatusException.class,
                () -> webSocketService.getSessionEntry(defaultAdminPrincipal.getSessionID()));
    }

    @Test
    public void addMember_isAdded() throws Exception {
        setDefaultAdminPrincipal(new HashSet<>());
        val memberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(), UUID.randomUUID());

        webSocketService.addMemberIfNew(memberPrincipal);

        assertTrue(webSocketService.getMemberMap().get(defaultAdminPrincipal).contains(memberPrincipal));
    }

    @Test
    public void addExistingMember_notDuplicate() throws Exception {
        setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));

        webSocketService.addMemberIfNew(defaultMemberPrincipal);

        assertEquals(1, webSocketService.getMemberMap().size());
    }

    @Test
    public void removeMember_isRemoved() throws Exception {
        setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));

        webSocketService.removeMember(defaultMemberPrincipal);

        assertTrue(webSocketService.getMemberMap().get(defaultAdminPrincipal).isEmpty());
    }

    @Test
    public void sendMembersUpdate_sendsUpdate() throws Exception {
        setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
        val session = new Session(defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal.getAdminID(), null, null,
                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)), null);

        webSocketService.sendMembersUpdate(session);

        verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(defaultAdminPrincipal.getName(), ADMIN_UPDATES,
                session.getMembers());
    }

    @Test
    public void sendSessionState_sendsState() throws Exception {
        setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
        val session = new Session(defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal.getAdminID(), null, null,
                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
                SessionState.WAITING_FOR_MEMBERS);

        webSocketService.sendSessionStateToMember(session, defaultMemberPrincipal.getMemberID().toString());

        verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                defaultMemberPrincipal.getMemberID().toString(), MEMBER_UPDATES, session.getSessionState());
    }

    @Test
    public void sendSessionStates_sendsToAll() throws Exception {
        val memberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(), UUID.randomUUID());
        setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
        val session = new Session(defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal.getAdminID(), null, null,
                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                        new Member(memberPrincipal.getMemberID(), null, null, null, null)),
                SessionState.WAITING_FOR_MEMBERS);

        webSocketService.sendSessionStateToMembers(session);

        verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                defaultMemberPrincipal.getMemberID().toString(), MEMBER_UPDATES, session.getSessionState());
        verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(memberPrincipal.getMemberID().toString(),
                MEMBER_UPDATES, session.getSessionState());
    }

    @Test
    public void removeSession_isRemoved() throws Exception {
        setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
        val session = new Session(defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal.getAdminID(), null, null,
                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
                SessionState.WAITING_FOR_MEMBERS);

        webSocketService.removeSession(session);

        assertTrue(webSocketService.getMemberMap().isEmpty());
    }
}
