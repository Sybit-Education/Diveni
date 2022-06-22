package io.diveni.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.val;

public class AvatarAnimalTest {

    @Test
    public void avatarAnimal_ToStringWorks() throws Exception {
        val animal = AvatarAnimal.WOLF;
        assertEquals(animal.toString(), "WOLF");
    }

}
