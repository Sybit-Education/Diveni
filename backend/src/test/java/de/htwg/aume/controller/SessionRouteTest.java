package de.htwg.aume.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SessionRouteTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void createSession_returnsSession() throws Exception {
		this.mockMvc.perform(post("/createSession")).andDo(print()).andExpect(status().isCreated())
				.andExpect(jsonPath("$.sessionID").isNotEmpty()).andExpect(jsonPath("$.adminID").isNotEmpty())
				.andExpect(jsonPath("$.membersID").isNotEmpty());
	}

	@Test
	public void getSession_isNotFound() throws Exception {
		this.mockMvc.perform(get("/getSession/{sessionID}", UUID.randomUUID())).andExpect(status().isNotFound());
	}

}
