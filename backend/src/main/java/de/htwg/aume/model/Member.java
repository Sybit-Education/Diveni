package de.htwg.aume.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Member {

	private final UUID memberID;

	private final String name;

	private final String hexColor;

	private final AvatarAnimal avatarAnimal;

	private final String currentEstimation;

	// Override because MongoRepository has a problem with Optional<String> as
	// property
	public Optional<String> getCurrentEstimation() {
		return Optional.ofNullable(this.currentEstimation);
	}

	public Member updateEstimation(String estimation) {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, estimation);
	}

	public Member resetEstimation() {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, null);
	}
}
