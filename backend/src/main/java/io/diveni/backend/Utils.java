/*
  SPDX-License-Identifier: AGPL-3.0-or-later
  Diveni - The Planing-Poker App
  Copyright (C) 2022 Diveni Team, AUME-Team 21/22, HTWG Konstanz
*/
package io.diveni.backend;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class Utils {

  private static final DateFormat dateFormatISO8601 = getDateFormatISO();

  private Utils() {}

  public static String generateRandomID() {
    Random random = ThreadLocalRandom.current();
    byte[] randomBytes = new byte[64];
    random.nextBytes(randomBytes);
    return Base64.getUrlEncoder()
        .encodeToString(randomBytes)
        .replace("-", "")
        .replace("_", "")
        .substring(0, 8);
  }

  public static Optional<String> getSessionIDfromUri(URI uri) {
    return getParamFromUri("sessionID", uri);
  }

  public static Optional<String> getAdminIDfromUri(URI uri) {
    return getParamFromUri("adminID", uri);
  }

  public static Optional<String> getMemberIDfromUri(URI uri) {
    return getParamFromUri("memberID", uri);
  }

  private static Optional<String> getParamFromUri(String queryParam, URI uri) {
    try {
      MultiValueMap<String, String> parameters =
          UriComponentsBuilder.fromUri(uri).build().getQueryParams();
      return Optional.of(parameters.get(queryParam).get(0));
    } catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
      return Optional.empty();
    }
  }

  private static final DateFormat getDateFormatISO() {
    TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    df.setTimeZone(tz);
    return df;
  }

  public static String getTimestampISO8601(Date date) {
    return dateFormatISO8601.format(date);
  }
}
