package de.htwg.aume.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import de.htwg.aume.Utils;
import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.model.SessionConfig;
import de.htwg.aume.model.SessionState;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import de.htwg.aume.principals.SessionPrincipals;
import lombok.val;

public class WebSocketServiceTest {

        private static final String ADMIN_UPDATES = "/updates/membersUpdated";

        private static final String MEMBER_UPDATES = "/updates/member";

        private static final String USER_STORIES_UPDATES = "/updates/userStories";

        @Mock
        SimpMessagingTemplate simpMessagingTemplateMock;

        @InjectMocks
        private WebSocketService webSocketService;

        private AdminPrincipal defaultAdminPrincipal = new AdminPrincipal(Utils.generateRandomID(),
                        Utils.generateRandomID());

        private MemberPrincipal defaultMemberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(),
                        Utils.generateRandomID());

        @BeforeEach
        public void initEach() {
                MockitoAnnotations.openMocks(this);
        }

        void setDefaultAdminPrincipal(Set<MemberPrincipal> members) throws Exception {
                val sessionPrincipalsField = WebSocketService.class.getDeclaredField("sessionPrincipalList");
                sessionPrincipalsField.setAccessible(true);
                val sessionPrincipals = List.of(new SessionPrincipals(defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal, members));
                sessionPrincipalsField.set(webSocketService, sessionPrincipals);
        }

        @Test
        public void setAdmin_isAdded() throws Exception {
                val adminPrincipal = new AdminPrincipal(Utils.generateRandomID(), Utils.generateRandomID());

                webSocketService.setAdminUser(adminPrincipal);

                assertTrue(webSocketService.getSessionPrincipalList().stream()
                                .anyMatch(p -> p.adminPrincipal() == adminPrincipal));
        }

        @Test
        public void setExistingAdmin_isOverwritten() throws Exception {
                setDefaultAdminPrincipal(new HashSet<>());
                val adminPrincipal = new AdminPrincipal(defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal.getAdminID());

                webSocketService.setAdminUser(adminPrincipal);

                assertTrue(webSocketService.getSessionPrincipalList().stream()
                                .anyMatch(p -> p.adminPrincipal() == adminPrincipal));
        }

        @Test
        public void getSessionPrincipals_isCorrect() throws Exception {
                setDefaultAdminPrincipal(new HashSet<>());

                val sessionPrincipals = webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID());

                assertEquals(new SessionPrincipals(defaultAdminPrincipal.getSessionID(), defaultAdminPrincipal,
                                Set.of()), sessionPrincipals);
        }

        @Test
        public void getMissingSessionEntry_isError() throws Exception {
                assertThrows(ResponseStatusException.class,
                                () -> webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID()));
        }

        @Test
        public void addMember_isAdded() throws Exception {
                setDefaultAdminPrincipal(new HashSet<>());
                val memberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(),
                                Utils.generateRandomID());

                webSocketService.addMemberIfNew(memberPrincipal);

                assertTrue(webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID())
                                .memberPrincipals().contains(memberPrincipal));
        }

        @Test
        public void addExistingMember_notDuplicate() throws Exception {
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));

                webSocketService.addMemberIfNew(defaultMemberPrincipal);

                assertEquals(1, webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID())
                                .memberPrincipals().size());
        }

        @Test
        public void removeMember_isRemoved() throws Exception {
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));

                webSocketService.removeMember(defaultMemberPrincipal);

                assertTrue(webSocketService.getSessionPrincipals(defaultAdminPrincipal.getSessionID())
                                .memberPrincipals().isEmpty());
        }

        @Test
        public void sendMembersUpdate_sendsUpdate() throws Exception {
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
                val session = new Session(new ObjectId(), defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal.getAdminID(), null, null,
                                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
                                null);

                webSocketService.sendMembersUpdate(session);

                verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(defaultAdminPrincipal.getName(),
                                ADMIN_UPDATES, session.getMembers());
        }

        @Test
        public void sendSessionState_sendsState() throws Exception {
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
                val session = new Session(new ObjectId(), defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal.getAdminID(), null, null,
                                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
                                SessionState.WAITING_FOR_MEMBERS);

                webSocketService.sendSessionStateToMember(session, defaultMemberPrincipal.getMemberID().toString());

                verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                                defaultMemberPrincipal.getMemberID().toString(), MEMBER_UPDATES,
                                session.getSessionState().toString());
        }

        @Test
        public void sendSessionStates_sendsToAll() throws Exception {
                val memberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(),
                                Utils.generateRandomID());
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
                val session = new Session(new ObjectId(), defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal.getAdminID(), null, null,
                                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                                                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
                                SessionState.WAITING_FOR_MEMBERS);

                webSocketService.sendSessionStateToMembers(session);

                verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                                defaultMemberPrincipal.getMemberID().toString(), MEMBER_UPDATES,
                                session.getSessionState().toString());
                verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                                memberPrincipal.getMemberID().toString(), MEMBER_UPDATES,
                                session.getSessionState().toString());
        }

        @Test
        public void sendUpdatedUserStoriesToMembers_sendsToAll() throws Exception {
                val memberPrincipal = new MemberPrincipal(defaultAdminPrincipal.getSessionID(),
                                Utils.generateRandomID());
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal, memberPrincipal));
                val session = new Session(new ObjectId(), defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal.getAdminID(),
                                new SessionConfig(List.of(), List.of(), null, "password"), null,
                                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null),
                                                new Member(memberPrincipal.getMemberID(), null, null, null, null)),
                                SessionState.WAITING_FOR_MEMBERS);

                webSocketService.sendUpdatedUserStoriesToMembers(session);

                verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                                defaultMemberPrincipal.getMemberID().toString(), USER_STORIES_UPDATES,
                                session.getSessionConfig().getUserStories());
                verify(simpMessagingTemplateMock, times(1)).convertAndSendToUser(
                                defaultMemberPrincipal.getMemberID().toString(), USER_STORIES_UPDATES,
                                session.getSessionConfig().getUserStories());
        }

        @Test
        public void removeSession_isRemoved() throws Exception {
                setDefaultAdminPrincipal(Set.of(defaultMemberPrincipal));
                val session = new Session(new ObjectId(), defaultAdminPrincipal.getSessionID(),
                                defaultAdminPrincipal.getAdminID(), null, null,
                                List.of(new Member(defaultMemberPrincipal.getMemberID(), null, null, null, null)),
                                SessionState.WAITING_FOR_MEMBERS);

                webSocketService.removeSession(session);

                assertTrue(webSocketService.getSessionPrincipalList().isEmpty());
        }
}
