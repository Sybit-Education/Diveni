package io.diveni.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.val;

public class AdminVoteTest {

  @Test
  public void setHostEstimation_works() {
    val adminVote = new AdminVote("");
    adminVote.setHostEstimation("XL");

    assertEquals(adminVote.getHostEstimation(), "XL");
  }
}
