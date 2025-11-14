package io.diveni.backend.controller;

import io.diveni.backend.model.news.PullRequest;
import io.diveni.backend.service.news.GithubApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/news")
public class NewsController {

  public static final String STATE_PARAM = "state";
    public static final String PAGE_PARAM = "page";
  public static final String PER_PAGE_PARAM = "per_page";
  public static final String IS_MERGED = "is_merged";
    public static final String SORT = "sort";
  public static final String SORT_DIRECTION = "direction";

  private GithubApiService service;

  @Autowired
  public NewsController(GithubApiService service) {
    this.service = service;
  }

  @GetMapping("/pull-requests")
  public ResponseEntity<PullRequest[]> getPullRequests(
      @RequestParam(name = STATE_PARAM, defaultValue = "closed") String state,
      @RequestParam(name = SORT, defaultValue = "created") String sort,
      @RequestParam(name = SORT_DIRECTION, defaultValue = "asc") String direction,
      @RequestParam(name = IS_MERGED, defaultValue = "false") Boolean isMerged,
      @RequestParam(name = PER_PAGE_PARAM, defaultValue = "50") Integer perPage,
      @RequestParam(name = PAGE_PARAM, defaultValue = "1") Integer page) {

    return new ResponseEntity<>(
        service.getPullRequests(state, sort, direction, isMerged, perPage, page), HttpStatus.OK);
  }
}
