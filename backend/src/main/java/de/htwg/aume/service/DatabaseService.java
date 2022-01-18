package de.htwg.aume.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.htwg.aume.model.Session;
import de.htwg.aume.repository.SessionRepository;

@Service
public class DatabaseService {

    @Autowired
    SessionRepository sessionRepo;

    public Optional<Session> getSessionByID(String sessionID) {
        return Optional.ofNullable(sessionRepo.findBySessionID(sessionID));
    }

    public Optional<Session> getSessionByAdminCookie(UUID adminCookie) {
        return Optional.ofNullable(sessionRepo.findByAdminCookie(adminCookie));
    }

    public Optional<Session> getSessionByMemberCookie(UUID memberCookie) {
        return sessionRepo.findByMemberCookie(memberCookie);
    }

    public Optional<Session> getSessionByMemberID(String memberID) {
        return sessionRepo.findByMemberID(memberID);
    }

    public List<Session> getSessions() {
        return sessionRepo.findAll();
    }

    public Session saveSession(Session session) {
        return sessionRepo.save(session.setLastModified(new Date()));
    }

    public void deleteSession(Session session) {
        sessionRepo.delete(session);
    }

}
