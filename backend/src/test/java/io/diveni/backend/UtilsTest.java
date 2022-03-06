package io.diveni.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.Test;

import lombok.val;

public class UtilsTest {

    @Test
    public void getISOTimestamp_works() {
        val timestamp = "2022-01-18T16:52:50.823Z";
        val date = Date.from(Instant.parse(timestamp));

        val result = Utils.getTimestampISO8601(date);

        assertEquals(result, timestamp);
    }
}
