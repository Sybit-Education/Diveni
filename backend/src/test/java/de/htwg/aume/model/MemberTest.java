package de.htwg.aume.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import lombok.val;

public class MemberTest {

	@Test
	public void equal_works() {
		val memberID = UUID.randomUUID();
		val name = "John";
		val hexColor = "#ffffff";
		val avatar = AvatarAnimal.WOLF;
		Integer estimation = null;
		val member = new Member(memberID, name, hexColor, avatar, estimation);
		val sameMember = new Member(memberID, name, hexColor, avatar, estimation);
		val otherMember = new Member(UUID.randomUUID(), name, hexColor, avatar, estimation);

		assertEquals(member, sameMember);
		assertNotEquals(member, otherMember);
		assertEquals(member.hashCode(), sameMember.hashCode());
	}

	@Test
	public void copyWithMemberID_works() {
		val memberIdBefore = UUID.randomUUID();
		val memberIdAfter = UUID.randomUUID();
		val nameBefore = "John";
		val nameAfter = "Johnathan";
		val hexColor = "#ffffff";
		val hexColorAfter = "#000000";
		val avatar = AvatarAnimal.WOLF;
		Integer estimation = null;
		Optional<Integer> estimationAfter = Optional.of(3);

		val member = new Member(memberIdBefore, nameBefore, hexColor, avatar, estimation);
		val result =
				member.copyWith(memberIdAfter, nameAfter, hexColorAfter, avatar, estimationAfter);

		assertNotEquals(member.getMemberID(), result.getMemberID());
		assertEquals(result.getMemberID(), memberIdAfter);
		assertEquals(result.getName(), nameAfter);
		assertEquals(result.getHexColor(), hexColorAfter);
		assertEquals(result.getCurrentEstimation(), estimationAfter);
	}

	@Test
	public void updateEstimation_works() {
		val vote = 5;

		val member = new Member(null, null, null, null, null);
		val result = member.updateEstimation(vote);

		assertEquals(result.getCurrentEstimation(), Optional.of(vote));
		assertEquals(member.getCurrentEstimation(), Optional.empty());
	}

}
