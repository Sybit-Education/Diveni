package io.diveni.backend.handler;

import java.security.Principal;
import java.util.Map;

import io.diveni.backend.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import io.diveni.backend.principals.AdminPrincipal;
import io.diveni.backend.principals.MemberPrincipal;
import lombok.val;

/**
 * Set anonymous user (Principal) in WebSocket messages by using uri query UUIDs. This is
 * necessary to avoid broadcasting messages but sending them to specific user sessions
 */
public class PrincipalWebSocketHandler extends DefaultHandshakeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalWebSocketHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        LOGGER.debug("--> determineUser()");
        val adminID = Utils.getAdminIDfromUri(request.getURI());
        val sessionID = Utils.getSessionIDfromUri(request.getURI());
        val memberID = Utils.getMemberIDfromUri(request.getURI());
        if (sessionID.isPresent() && adminID.isPresent()) {
            LOGGER.debug("<-- determineUser(), admin is present!");
            return new AdminPrincipal(sessionID.get(), adminID.get());
        }
        if (sessionID.isPresent() && memberID.isPresent()) {
            LOGGER.debug("<-- determineUser(), member is present!");
            return new MemberPrincipal(sessionID.get(), memberID.get());
        }
        LOGGER.error("<-- determineUser(), nether admin nor member is present!");
        return null;
    }

}
