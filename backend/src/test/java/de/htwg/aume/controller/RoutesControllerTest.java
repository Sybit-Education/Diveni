package de.htwg.aume.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import de.htwg.aume.model.Member;
import de.htwg.aume.model.Session;
import de.htwg.aume.model.SessionConfig;
import de.htwg.aume.model.SessionState;
import de.htwg.aume.repository.SessionRepository;
import lombok.val;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RoutesControllerTest {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Autowired
	SessionRepository sessionRepo;

	@Autowired
	private MockMvc mockMvc;

	private static String sessionConfigToJson(SessionConfig config) {
		val set = config.getSet().stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
		val password = config.getPassword() == null ? "null" : "\"" + config.getPassword() + "\"";
		return String.format("{ \"set\": [%s], \"password\": %s }", set, password);
	}

	@Test
	public void createSession_returnsSession() throws Exception {
		val sessionConfigJson = sessionConfigToJson(new SessionConfig(Arrays.asList("1", "2", "3"), List.of(), null));
		this.mockMvc.perform(post("/sessions").contentType(APPLICATION_JSON_UTF8).content(sessionConfigJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.sessionID").isNotEmpty())
				.andExpect(jsonPath("$.adminID").isNotEmpty()).andExpect(jsonPath("$.sessionConfig").isNotEmpty());
	}

	@Test
	public void createProtectedSession_returnsSession() throws Exception {
		val sessionConfigJson = sessionConfigToJson(
				new SessionConfig(Arrays.asList("1", "2", "3"), List.of(), "testPassword"));
		this.mockMvc.perform(post("/sessions").contentType(APPLICATION_JSON_UTF8).content(sessionConfigJson))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.sessionID").isNotEmpty())
				.andExpect(jsonPath("$.adminID").isNotEmpty()).andExpect(jsonPath("$.sessionConfig").isNotEmpty());
	}

	@Test
	public void getSession_isNotFound() throws Exception {
		this.mockMvc.perform(get("/sessions/{sessionID}", UUID.randomUUID())).andExpect(status().isNotFound());
	}

	@Test
	public void joinMember_addsMemberToSession() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'member': {" + "'memberID': '365eef59-931d-0000-0000-2ba016cb523b',"
				+ "'name': 'Julian'," + "'hexColor': '0xababab'," + "'avatarAnimal': 'LION',"
				+ "'currentEstimation': null" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andExpect(status().isOk());
	}

	@Test
	public void joinMember_addsMemberToProtectedSession() throws Exception {
		val sessionUUID = UUID.randomUUID();
		val password = "testPassword";
		SessionConfig sessionConfig = new SessionConfig(new ArrayList<>(), List.of(), password);
		sessionRepo.save(new Session(sessionUUID, UUID.randomUUID(), sessionConfig, new ArrayList<Member>(),
				SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'password': '" + password + "'," + "'member': {"
				+ "'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," + "'name': 'Julian',"
				+ "'hexColor': '0xababab'," + "'avatarAnimal': 'LION'," + "'currentEstimation': null" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andExpect(status().isOk());
	}

	@Test
	public void joinMember_failsToAddMemberToProtectedSessionWrongPassword() throws Exception {
		val sessionUUID = UUID.randomUUID();
		val password = "testPassword";
		SessionConfig sessionConfig = new SessionConfig(new ArrayList<>(), List.of(), password);
		sessionRepo.save(new Session(sessionUUID, UUID.randomUUID(), sessionConfig, new ArrayList<Member>(),
				SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'password': '" + "wrongPassword" + "'," + "'member': {"
				+ "'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," + "'name': 'Julian',"
				+ "'hexColor': '0xababab'," + "'avatarAnimal': 'LION'," + "'currentEstimation': null" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc
				.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
						.content(memberAsJson))
				.andExpect(status().isUnauthorized()).andExpect(status().reason(ErrorMessages.wrongPasswordMessage));
	}

	@Test
	public void joinMember_failsToAddMemberToProtectedSessionNullPassword() throws Exception {
		val sessionUUID = UUID.randomUUID();
		val password = "testPassword";
		SessionConfig sessionConfig = new SessionConfig(new ArrayList<>(), List.of(), password);
		sessionRepo.save(new Session(sessionUUID, UUID.randomUUID(), sessionConfig, new ArrayList<Member>(),
				SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'password': " + "null" + "," + "'member': {"
				+ "'memberID': '365eef59-931d-0000-0000-2ba016cb523b'," + "'name': 'Julian',"
				+ "'hexColor': '0xababab'," + "'avatarAnimal': 'LION'," + "'currentEstimation': null" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc
				.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
						.content(memberAsJson))
				.andExpect(status().isUnauthorized()).andExpect(status().reason(ErrorMessages.wrongPasswordMessage));
	}

	@Test
	public void joinMember_failsToAddMemberDueToFalseAvatarAnimal() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'member': {" + "'memberID': '365eef59-931d-0000-0000-2ba016cb523b',"
				+ "'name': 'Julian'," + "'hexColor': '0xababab'," + "'avatarAnimal': 'NON_EXISTING_ANIMAL',"
				+ "'currentEstimation': null" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andExpect(status().isBadRequest());
	}

	@Test
	public void joinMember_failsToAddMemberDueToFalseAvatarAnimal2() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'member': {" + "'memberID': '365eef59-931d-0000-0000-2ba016cb523b',"
				+ "'name': 'Julian'," + "'hexColor': '0xababab'," + "'avatarAnimal': 'wolf.png',"
				+ "'currentEstimation': null" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andExpect(status().isBadRequest());
	}

	@Test
	public void joinMember_failsToAddMemberDueToFalseEstimation() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'member': {" + "'memberID': '365eef59-931d-0000-0000-2ba016cb523b',"
				+ "'name': 'Julian'," + "'hexColor': '0xababab'," + "'avatarAnimal': 'NON_EXISTING_ANIMAL',"
				+ "'currentEstimation': 'test'" + "}" + "}";
		// @formatter:on
		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andExpect(status().isBadRequest());
	}

	@Test
	public void joinMember_givesErrorWhenSessionNotExists() throws Exception {

		// @formatter:off
		var memberAsJson = "{" + "'member': {" + "'memberID': '365eef59-931d-0000-0000-2ba016cb523b',"
				+ "'name': 'Julian'," + "'hexColor': '0xababab'," + "'avatarAnimal': 'LION',"
				+ "'currentEstimation': null" + "}" + "}";
		// @formatter:on

		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc
				.perform(post("/sessions/{sessionID}/join", UUID.randomUUID()).contentType(APPLICATION_JSON_UTF8)
						.content(memberAsJson))
				.andExpect(status().isNotFound()).andExpect(status().reason(ErrorMessages.sessionNotFoundErrorMessage));
	}

	@Test
	public void joinMember_addsMemberNotIfAlreadyExisting() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		// @formatter:off
		var memberAsJson = "{" + "'member': {" + "'memberID': '365eef59-931d-0000-0000-2ba016cb523b',"
				+ "'name': 'Julian'," + "'hexColor': '0xababab'," + "'avatarAnimal': 'LION',"
				+ "'currentEstimation': null" + "}" + "}";
		// @formatter:on

		memberAsJson = memberAsJson.replaceAll("'", "\"");

		this.mockMvc.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
				.content(memberAsJson)).andExpect(status().isOk());

		this.mockMvc
				.perform(post("/sessions/{sessionID}/join", sessionUUID).contentType(APPLICATION_JSON_UTF8)
						.content(memberAsJson))
				.andExpect(status().isBadRequest()).andExpect(status().reason(ErrorMessages.memberExistsErrorMessage));
	}

	@Test
	public void getSession_returnsSession() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		this.mockMvc.perform(get("/sessions/{sessionID}", sessionUUID)).andExpect(status().isOk())
				.andExpect(jsonPath("$.sessionID").isNotEmpty()).andExpect(jsonPath("$.adminID").isNotEmpty());
	}

	@Test
	public void getSession_failsWrongID() throws Exception {
		val sessionUUID = UUID.randomUUID();
		sessionRepo
				.save(new Session(sessionUUID, UUID.randomUUID(), new SessionConfig(new ArrayList<>(), List.of(), null),
						new ArrayList<Member>(), SessionState.WAITING_FOR_MEMBERS));

		this.mockMvc.perform(get("/sessions/{sessionID}", UUID.randomUUID())).andExpect(status().isNotFound())
				.andExpect(status().reason(ErrorMessages.sessionNotFoundErrorMessage));
	}

}
