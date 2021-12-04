package de.htwg.aume;

import java.net.URI;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import lombok.val;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class Utils {

	private Utils() {
	}

	public static String generateRandomID() {
		Random random = ThreadLocalRandom.current();
		byte[] randomBytes = new byte[64];
		random.nextBytes(randomBytes);
		return Base64.getUrlEncoder()
				.encodeToString(randomBytes)
				.replace("-", "")
				.replace("_", "").substring(0,8);
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
			MultiValueMap<String, String> parameters = UriComponentsBuilder.fromUri(uri).build().getQueryParams();
			return Optional.of(parameters.get(queryParam).get(0));
		}
		catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
			return Optional.empty();
		}
	}

}
