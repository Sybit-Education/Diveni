package io.diveni.backend.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NewsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void getPRs_InvalidState_returnsException() throws Exception {
    MockHttpServletRequestBuilder requestBuilder = get("/news/pull-requests");
    requestBuilder.param(NewsController.STATE_PARAM, "invalid");
    requestBuilder.param(NewsController.PAGE_PARAM, "1");
    requestBuilder.param(NewsController.PER_PAGE_PARAM, "1");
    this.mockMvc.perform(requestBuilder)
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(status().reason(ErrorMessages.invalidPullRequestStateMessage));
  }

  @Test
  public void getPRs_InvalidPerPage_returnsException() throws Exception {
    MockHttpServletRequestBuilder requestBuilder = get("/news/pull-requests");
    requestBuilder.param(NewsController.STATE_PARAM, "closed");
    requestBuilder.param(NewsController.PAGE_PARAM, "1");
    requestBuilder.param(NewsController.PER_PAGE_PARAM, "5000");
    this.mockMvc.perform(requestBuilder)
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(status().reason(ErrorMessages.maxPullRequestsPerPageMessage));
  }


  @Test
  public void getPRs_Valid_returnsPrs() throws Exception {
    MockHttpServletRequestBuilder requestBuilder = get("/news/pull-requests");
    requestBuilder.param(NewsController.STATE_PARAM, "closed");
    requestBuilder.param(NewsController.PAGE_PARAM, "1");
    requestBuilder.param(NewsController.PER_PAGE_PARAM, "10");
    this.mockMvc.perform(requestBuilder)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(Matchers.not(0)));

  }

}
