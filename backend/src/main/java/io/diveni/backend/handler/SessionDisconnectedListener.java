package io.diveni.backend.handler;

import io.diveni.backend.controller.WebsocketController;
import io.diveni.backend.principals.MemberPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class SessionDisconnectedListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    WebsocketController controller;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        var principal = event.getUser();
        if (principal instanceof MemberPrincipal) {
            controller.removeMember(principal);
        }
    }
}
