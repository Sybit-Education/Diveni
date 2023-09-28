package io.diveni.backend.controller;


import io.diveni.backend.model.news.PullRequest;
import org.springframework.beans.factory.annotation.Value;
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

  private RestTemplate client;

  @Value("${vsc.github.api.pr.get}")
  private String url;

  public NewsController(RestTemplate client) {
    this.client = client;
  }

  @GetMapping("/pull-requests")
  public ResponseEntity<List<PullRequest>> getPullRequests(@RequestParam(name = "state", defaultValue = "closed") String state,
                                                           @RequestParam(name = "per_page", defaultValue = "6") Integer perPage,
                                                           @RequestParam(name = "page", defaultValue = "1") Integer page) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);

    builder.queryParam("state", state);
    builder.queryParam("page", page);
    builder.queryParam("per_page", perPage);

    ResponseEntity<PullRequest[]> data = this.client.getForEntity(builder.toUriString(), PullRequest[].class);
    List<PullRequest> result = Arrays.stream(data.getBody())
      .filter(e -> e.getMergedAt() != null)
      .sorted((o1, o2) -> Integer.compare(o2.getNumber(), o1.getNumber())).collect(Collectors.toList());
    return ResponseEntity.ok(result);
  }
}
