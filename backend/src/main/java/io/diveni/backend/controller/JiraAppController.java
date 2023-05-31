package io.diveni.backend.controller;

import io.opencensus.contrib.http.util.HttpPropagationUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

@RestController
@RequestMapping("/jira-app")
@CrossOrigin("*")
public class JiraAppController {

  private static final Logger LOGGER = LoggerFactory.getLogger(JiraAppController.class);

  private static final String[] IP_HEADERS = {
    "X-Forwarded-For",
    "Proxy-Client-IP",
    "WL-Proxy-Client-IP",
    "HTTP_X_FORWARDED_FOR",
    "HTTP_X_FORWARDED",
    "HTTP_X_CLUSTER_CLIENT_IP",
    "HTTP_CLIENT_IP",
    "HTTP_FORWARDED_FOR",
    "HTTP_FORWARDED",
    "HTTP_VIA",
    "REMOTE_ADDR"
    // you can add more matching headers here ...
  };

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
    LOGGER.info("---------");
    LOGGER.info("RequestURL:" + request.getRequestURL().toString());
    LOGGER.info("RequestURI:" + request.getRequestURI());
    LOGGER.info("QueryString:" + request.getQueryString());
    LOGGER.info("Remote Addr:" + request.getRemoteAddr());
    for (String header : IP_HEADERS) {
      LOGGER.info("Header " + header + ": " + request.getHeader(header));
    }
    return "ping";
  }
}
