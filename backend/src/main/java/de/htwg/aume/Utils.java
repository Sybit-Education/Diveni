package de.htwg.aume;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class Utils {

    private Utils() {
    }

    public static Optional<UUID> getSessionIDfromUri(URI uri) {
        return getParamFromUri("sessionID", uri);
    }

    public static Optional<UUID> getAdminIDfromUri(URI uri) {
        return getParamFromUri("adminID", uri);
    }

    public static Optional<UUID> getMemberIDfromUri(URI uri) {
        return getParamFromUri("memberID", uri);
    }

    private static Optional<UUID> getParamFromUri(String queryParam, URI uri) {
        try {
            MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
            return Optional.of(UUID.fromString(parameters.get(queryParam).get(0)));
        } catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
