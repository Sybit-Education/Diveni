package io.diveni.backend.controller;


import io.diveni.backend.model.news.PullRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/news")
public class NewsController {

  private static final String STATE_PARAM = "state";
  private static final String PAGE_PARAM = "page";
  private static final String PER_PAGE_PARAM = "per_page";
  private static final String BOT_USER_TYPE = "Bot";

  private RestTemplate client;

  @Value("${vsc.github.api.pr.get}")
  private String url;

  @Value("${vsc.github.access-token}")
  private String authToken;

  public NewsController(RestTemplate client) {
    this.client = client;
  }

  @GetMapping("/pull-requests")
  public ResponseEntity<PullRequest[]> getPullRequests(@RequestParam(name = STATE_PARAM, defaultValue = "closed") String state,
                                                       @RequestParam(name = PER_PAGE_PARAM, defaultValue = "50") Integer perPage,
                                                       @RequestParam(name = PAGE_PARAM, defaultValue = "1") Integer page) {

    HttpHeaders headers = new HttpHeaders();
    if (authToken != null) {
      headers.setBearerAuth(authToken);
    }
    HttpEntity<Void> entity = new HttpEntity<>(null, headers);

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
      .queryParam(STATE_PARAM, state)
      .queryParam(PAGE_PARAM, page)
      .queryParam(PER_PAGE_PARAM, perPage);

    ResponseEntity<PullRequest[]> data = client.exchange(builder.toUriString(), HttpMethod.GET, entity, PullRequest[].class);

    Arrays.sort(data.getBody(), (o1, o2) -> Integer.compare(o2.getNumber(), o1.getNumber()));
    return ResponseEntity.ok(data.getBody());
  }
}
