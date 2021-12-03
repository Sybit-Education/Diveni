package de.htwg.aume.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import de.htwg.aume.Utils;
import org.junit.jupiter.api.Test;

import lombok.val;

public class MemberTest {

	@Test
	public void equal_works() {
		val memberID = Utils.generateRandomID();
		val name = "John";
		val hexColor = "#ffffff";
		val avatar = AvatarAnimal.WOLF;
		String estimation = null;
		val member = new Member(memberID, name, hexColor, avatar, estimation);
		val sameMember = new Member(memberID, name, hexColor, avatar, estimation);
		val otherMember = new Member(Utils.generateRandomID(), name, hexColor, avatar, estimation);

		assertEquals(member, sameMember);
		assertNotEquals(member, otherMember);
		assertEquals(member.hashCode(), sameMember.hashCode());
	}

	@Test
	public void updateEstimation_works() {
		val vote = "5";

		val member = new Member(null, null, null, null, null);
		val result = member.updateEstimation(vote);

		assertEquals(result.getCurrentEstimation(), Optional.of(vote));
		assertEquals(member.getCurrentEstimation(), Optional.empty());
	}

	@Test
	public void resetEstimation_works() {
		val vote = "5";
		val member = new Member(null, null, null, null, vote);

		val result = member.resetEstimation();

		assertEquals(member.getCurrentEstimation(), Optional.of(vote));
		assertTrue(result.getCurrentEstimation().isEmpty());
	}

}
