package de.htwg.aume.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Member {

	private UUID memberID;

	private String name;

	private String hexColor;

	private AvatarAnimal avatarAnimal;

	private String currentEstimation;

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
