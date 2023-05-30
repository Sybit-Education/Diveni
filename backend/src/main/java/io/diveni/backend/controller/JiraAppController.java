package io.diveni.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jira-app")
@CrossOrigin("*")
public class JiraAppController {

  private static final Logger LOGGER = LoggerFactory.getLogger(JiraAppController.class);

  @GetMapping("/get")
  public String logGetRequest() {
    return "success";
  }

  @PostMapping("/post")
  public String logPostRequest() {
    return "success";
  }

  @GetMapping("/ping")
  public String ping(HttpServletRequest request){
    LOGGER.info("RequestURL:" + request.getRequestURL().toString());
    LOGGER.info("RequestURI:" + request.getRequestURI());
    LOGGER.info("RequestURL:" + request.getQueryString());
    return "ping";
  }
}
