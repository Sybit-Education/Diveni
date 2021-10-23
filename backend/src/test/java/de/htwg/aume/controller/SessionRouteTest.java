package de.htwg.aume.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.repository.SessionRepository;
import lombok.val;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionRouteTest {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	SessionRepository sessionRepo;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void createSession_returnsSession() throws Exception {
		this.mockMvc.perform(post("/sessions")).andDo(print()).andExpect(status().isCreated())
				.andExpect(jsonPath("$.sessionID").isNotEmpty()).andExpect(jsonPath("$.adminID").isNotEmpty())
				.andExpect(jsonPath("$.membersID").isNotEmpty());
	}

	@Test
	public void getSession_isNotFound() throws Exception {
		this.mockMvc.perform(get("/sessions/{sessionID}", UUID.randomUUID())).andExpect(status().isNotFound());
	}

	@Test
	public void joinMember_addsMemberToSession() throws Exception {
		// val member = new Member(UUID.randomUUID(), "John", "0x0a0a0a",
		// AvatarAnimal.CAMEL, Optional.empty());
		val sessionUUID = UUID.randomUUID();

		sessionRepo.save(new Session(sessionUUID, UUID.randomUUID(), UUID.randomUUID(), new ArrayList<Member>()));

		// @formatter:off
		var memberAsJson =
		"{" +
		"'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," +
		"'name': 'Julian'," +
		"'hexColor': '0xababab'," +
		"'avatarAnimal': 'LION'," +
		"'currentEstimation': null" +
		"}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andDo(print()).andExpect(status().isOk());
	}

	@Test
	public void joinMember_failsToAddMemberDueToFalseAvatarAnimal() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo.save(new Session(sessionUUID, UUID.randomUUID(), UUID.randomUUID(), new ArrayList<Member>()));

		// @formatter:off
		var memberAsJson =
		"{" +
		"'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," +
		"'name': 'Julian'," +
		"'hexColor': '0xababab'," +
		"'avatarAnimal': 'NON_EXISTING_ANIMAL'," +
		"'currentEstimation': null" +
		"}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void joinMember_failsToAddMemberDueToFalseEstimation() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo.save(new Session(sessionUUID, UUID.randomUUID(), UUID.randomUUID(), new ArrayList<Member>()));

		// @formatter:off
		var memberAsJson =
		"{" +
		"'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," +
		"'name': 'Julian'," +
		"'hexColor': '0xababab'," +
		"'avatarAnimal': 'NON_EXISTING_ANIMAL'," +
		"'currentEstimation': 'test'" +
		"}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andDo(print()).andExpect(status().isBadRequest());
	}

	@Test
	public void joinMember_givesErrorWhenSessionNotExists() throws Exception {

		// @formatter:off
		var memberAsJson =
		"{" +
		"'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," +
		"'name': 'Julian'," +
		"'hexColor': '0xababab'," +
		"'avatarAnimal': 'LION'," +
		"'currentEstimation': null" +
		"}";
		// @formatter:on

		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(
				post("/join/{sessionID}", UUID.randomUUID()).contentType(APPLICATION_JSON_UTF8).content(memberAsJson))
				.andDo(print()).andExpect(status().isNotFound());
	}

}
