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
import java.util.Comparator;
import java.util.List;

import static io.diveni.backend.controller.NewsController.*;

@Service
public class GithubApiService {

  private static final List<String> VALID_STATES = List.of("open", "closed", "all");
  private static final int MAX_PER_PAGE = 100;

  private static final String API_VERSION_HEADER = "X-GitHub-Api-Version";

  @Value("${vsc.github.api.pr.get}")
  private String url;

  @Value("${vsc.github.access-token}")
  private String authToken;

  @Value("${vsc.github.api.version}")
  private String apiVersion;

  private static final Logger logger = LoggerFactory.getLogger(GithubApiService.class);
  private RestTemplate client;

  @Autowired
  public GithubApiService(RestTemplate client) {
    this.client = client;
  }

  public PullRequest[] getPullRequests(String state, String sort, String direction, boolean isMerged, int perPage, int page) {

    HttpHeaders headers = new HttpHeaders();
    if (authToken != null && !authToken.equals("null")) {
      headers.setBearerAuth(authToken);
    }
    headers.set(API_VERSION_HEADER, apiVersion);

    HttpEntity<Void> entity = new HttpEntity<>(null, headers);

    if (!VALID_STATES.contains(state)) {
      logger.warn("Bad request: {}", ErrorMessages.noPullRequestsMatchingCriteriaMessage);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.invalidPullRequestStateMessage);
    }

    if (perPage > MAX_PER_PAGE) {
      logger.warn("Bad request: {}", ErrorMessages.maxPullRequestsPerPageMessage);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessages.maxPullRequestsPerPageMessage);
    }

    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
      .queryParam(STATE_PARAM, state)
      .queryParam(PAGE_PARAM, page)
      .queryParam(PER_PAGE_PARAM, perPage)
      .queryParam(SORT, sort).queryParam(SORT_DIRECTION, direction);

    ResponseEntity<PullRequest[]> data;
    try {
      data = client.exchange(builder.toUriString(), HttpMethod.GET, entity, PullRequest[].class);
    } catch (Exception ex) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, ErrorMessages.serverLimitReachedMessage);
    }
    PullRequest[] body = data.getBody();

    if (isMerged) {
      body = Arrays.stream(body).filter(e -> e.getMergedAt() != null).toArray(PullRequest[]::new);
    }

    logger.debug("getPullRequests({},{},{})", state, perPage, page);

    return body;
  }
}
