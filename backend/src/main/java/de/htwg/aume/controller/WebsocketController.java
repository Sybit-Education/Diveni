package de.htwg.aume.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.htwg.aume.model.HelloMessage;
import de.htwg.aume.model.Member;
import de.htwg.aume.model.MemberVote;
import de.htwg.aume.model.Session;
import de.htwg.aume.repository.SessionRepository;
import lombok.val;

@Controller
public class WebsocketController {

	@Autowired
	SessionRepository sessionRepo;

	// TODO: Uncomment when implementing voting mechanism
	// @MessageMapping("/vote")
	// @SendTo("/admin/update")
	// public List<Member> vote(MemberVote memberVote) {
	// val session = sessionRepo.findByMemberID(memberVote.getMember().getMemberID())
	// .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "session not
	// found"));
	// var members = new ArrayList<Member>(session.getMembers());
	// val memberToReplace = members.stream().filter(m ->
	// m.getMemberID().equals(memberVote.getMember().getMemberID()))
	// .findFirst().get();
	// members.set(members.indexOf(memberToReplace),
	// memberToReplace.copyWith(null, null, null, null,
	// Optional.of(memberVote.getVote())));
	// sessionRepo.save(session.copyWith(null, null, null, members));
	// return members;
	// }

	@MessageMapping("/hello")
	@SendTo("/admin/update")
	public String hello(HelloMessage message) {
		return "Hello " + message.getName();
	}

}
