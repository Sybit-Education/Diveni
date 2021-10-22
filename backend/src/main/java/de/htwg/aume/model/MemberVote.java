package de.htwg.aume.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberVote {

	final int vote;

	final Member member;

}
