package de.htwg.aume.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.util.UriComponentsBuilder;

import ch.qos.logback.classic.pattern.Util;
import de.htwg.aume.Utils;
import de.htwg.aume.principals.AdminPrincipal;
import de.htwg.aume.principals.MemberPrincipal;
import lombok.val;

/**
 * Set anonymous user (Principal) in WebSocket messages by using uri query UUIDs. This is
 * necessary to avoid broadcasting messages but sending them to specific user sessions
 */
public class PrincipalWebSocketHandler extends DefaultHandshakeHandler {

	@Override
	protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		val adminID = Utils.getAdminIDfromUri(request.getURI());
		val sessionID = Utils.getSessionIDfromUri(request.getURI());
		val memberID = Utils.getMemberIDfromUri(request.getURI());
		if (sessionID.isPresent() && adminID.isPresent()) {
			return new AdminPrincipal(sessionID.get(), adminID.get());
		}
		if (sessionID.isPresent() && memberID.isPresent()) {
			return new MemberPrincipal(sessionID.get(), memberID.get());
		}
		return null;
	}

}