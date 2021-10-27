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

	private final Integer currentEstimation;

	// Override because MongoRepository has a problem with Optional<Integer> as property
	public Optional<Integer> getCurrentEstimation() {
		return Optional.ofNullable(this.currentEstimation);
	}

	public Member copyWith(UUID memberID, String name, String hexColor, AvatarAnimal avatarAnimal,
			Optional<Integer> currentEstimation) {
		var tmpMember = this;
		if (memberID != null) {
			tmpMember = new Member(memberID, tmpMember.name, tmpMember.hexColor,
					tmpMember.avatarAnimal, tmpMember.currentEstimation);
		}
		if (name != null) {
			tmpMember = new Member(tmpMember.memberID, name, tmpMember.hexColor,
					tmpMember.avatarAnimal, tmpMember.currentEstimation);
		}
		if (hexColor != null) {
			tmpMember = new Member(tmpMember.memberID, tmpMember.name, hexColor,
					tmpMember.avatarAnimal, tmpMember.currentEstimation);
		}
		if (avatarAnimal != null) {
			tmpMember = new Member(tmpMember.memberID, tmpMember.name, tmpMember.hexColor,
					avatarAnimal, tmpMember.currentEstimation);
		}
		if (currentEstimation != null) {
			tmpMember = new Member(tmpMember.memberID, tmpMember.name, tmpMember.hexColor,
					tmpMember.avatarAnimal, currentEstimation.orElse(null));
		}
		return tmpMember;
	}

	public Member updateEstimation(int estimation) {
		return this.copyWith(null, null, null, null, Optional.of(estimation));
	}

	public Member resetEstimation() {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, null);
	}

}
