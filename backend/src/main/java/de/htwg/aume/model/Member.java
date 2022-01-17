package de.htwg.aume.model;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Member {

	private String memberID;

	private String name;

	private String hexColor;

	private AvatarAnimal avatarAnimal;

	private String currentEstimation;

	public Member updateEstimation(String estimation) {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, estimation);
	}

	public Member resetEstimation() {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, null);
	}
}
