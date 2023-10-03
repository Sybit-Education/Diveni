package io.diveni.backend.model.news;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class PullRequestTest {

  @Test
  public void equal_works(){
    LocalDate now  = LocalDate.now();
    PullRequest first = new PullRequest(1,"test.com","First",now,"Admin");
    PullRequest second = new PullRequest(1,"test.com","First",now,"Admin");
    PullRequest third = new PullRequest(2,"test.com","Second",LocalDate.now(),"Usr");
    Assertions.assertEquals(first,second);
    Assertions.assertNotEquals(first,third);
  }
}
