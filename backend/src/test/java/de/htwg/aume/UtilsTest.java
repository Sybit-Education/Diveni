package de.htwg.aume;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import lombok.val;

public class UtilsTest {

    @Test
    public void getISOTimestamp_works() {
        val date = Date.from(Instant.parse("2022-01-18T16:52:50Z"));

        val result = Utils.getTimestampISO8601(date);

        assertEquals(result, "2022-01-18T16:52:50Z");
    }
}
