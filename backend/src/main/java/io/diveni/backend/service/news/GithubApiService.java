package io.diveni.backend.service.news;

import io.diveni.backend.controller.ErrorMessages;
import io.diveni.backend.model.news.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

import static io.diveni.backend.controller.NewsController.*;

@Service
public class GithubApiService {

  private static final List<String> VALID_STATES = List.of("open", "closed", "all");
  private static final int MAX_PER_PAGE = 100;

  @Value("${vsc.github.api.pr.get}")
  private String url;

  @Value("${vsc.github.access-token}")
  private String authToken;

  private static final Logger logger = LoggerFactory.getLogger(GithubApiService.class);
  private RestTemplate client;

  @Autowired
  public GithubApiService(RestTemplate client) {
    this.client = client;
  }

  public PullRequest[] getPullRequests(String state, int perPage, int page) {

    HttpHeaders headers = new HttpHeaders();
    if (authToken != null && !authToken.equals("null")) {
      headers.setBearerAuth(authToken);
    }

    HttpEntity<Void> entity = new HttpEntity<>(null, headers);

    if (!VALID_STATES.contains(state)) {
      logger.warn("Bad request: {}", ErrorMessages.noPullRequestsMatchingCriteriaMessage);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.invalidPullRequestStateMessage);
    }

    if (perPage > MAX_PER_PAGE) {
      logger.warn("Bad request: {}", ErrorMessages.maxPullRequestsPerPage);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.maxPullRequestsPerPage);
    }

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
      .queryParam(STATE_PARAM, state)
      .queryParam(PAGE_PARAM, page)
      .queryParam(PER_PAGE_PARAM, perPage);

    ResponseEntity<PullRequest[]> data = client.exchange(builder.toUriString(), HttpMethod.GET, entity, PullRequest[].class);
    PullRequest[] body = data.getBody();

    Arrays.sort(body, (o1, o2) -> Integer.compare(o2.getNumber(), o1.getNumber()));
    logger.debug("getPullRequests({},{},{})", state, perPage, page);

    return body;
  }
}
