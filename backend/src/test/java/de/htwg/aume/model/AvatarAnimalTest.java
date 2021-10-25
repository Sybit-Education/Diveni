package de.htwg.aume.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.val;

@SpringBootTest
public class AvatarAnimalTest {

	@Test
	public void avatarAnimal_ToStringWorks() throws Exception {
		val animal = AvatarAnimal.WOLF;
		assertEquals(animal.toString(), "WOLF");
	}

}
