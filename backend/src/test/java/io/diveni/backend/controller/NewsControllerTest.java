package io.diveni.backend.controller;

import io.diveni.backend.model.news.PullRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class NewsControllerTest {

  private static final String path = "/news/pull-requests";

  @Value("${vsc.github.api.pr.get}")
  private String url;

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RestTemplate client;

  @Test
  public void getPRs_InvalidState_returnsException() throws Exception {
    String invalidState = "invalid";
    MockHttpServletRequestBuilder requestBuilder = get(path);
    requestBuilder.param(NewsController.STATE_PARAM, invalidState);
    requestBuilder.param(NewsController.PAGE_PARAM, "1");
    requestBuilder.param(NewsController.PER_PAGE_PARAM, "1");
    this.mockMvc.perform(requestBuilder)
      .andDo(print())
      .andExpect(status().isBadRequest())
      .andExpect(status().reason(ErrorMessages.invalidPullRequestStateMessage));
  }

  @Test
  public void getPRs_InvalidPerPage_returnsException() throws Exception {
    MockHttpServletRequestBuilder requestBuilder = get(path);
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
    PullRequest[] data = new PullRequest[]{
      new PullRequest(403, "test2.com", "test2", LocalDateTime.now(), LocalDateTime.now(), "user"),
      new PullRequest(404, "test.com", "test",  LocalDateTime.now(), LocalDateTime.now(), "user")
    };

    when(client.exchange(contains(url), eq(HttpMethod.GET), any(), eq(PullRequest[].class)))
      .thenReturn(ResponseEntity.ok(data.clone()));

    MockHttpServletRequestBuilder requestBuilder = get(path);
    requestBuilder.param(NewsController.STATE_PARAM, "closed");
    requestBuilder.param(NewsController.PAGE_PARAM, "1");
    requestBuilder.param(NewsController.PER_PAGE_PARAM, "10");

    this.mockMvc.perform(requestBuilder)
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.length()")
        .value(Matchers.is(2)))
      .andExpect(jsonPath("$[0].number").value(data[0].getNumber()))
      .andExpect(jsonPath("$[0]['html_url']").value(data[0].getHtmlUrl()))
      .andExpect(jsonPath("$[0]['title']").value(data[0].getTitle()))
      .andExpect(jsonPath("$[0]['user_type']").value(data[0].getUserType()));

  }

  @Test
  public void getPRs_Valid_returnsServiceUnavailable() throws Exception {

    when(client.exchange(contains(url), eq(HttpMethod.GET), any(), eq(PullRequest[].class)))
      .thenThrow(new RestClientException("Some error"));

    MockHttpServletRequestBuilder requestBuilder = get(path);
    requestBuilder.param(NewsController.STATE_PARAM, "closed");
    requestBuilder.param(NewsController.PAGE_PARAM, "1");
    requestBuilder.param(NewsController.PER_PAGE_PARAM, "10");

    this.mockMvc.perform(requestBuilder)
      .andDo(print())
      .andExpect(status().isServiceUnavailable())
      .andExpect(status().reason(ErrorMessages.serverLimitReachedMessage));
  }
}
