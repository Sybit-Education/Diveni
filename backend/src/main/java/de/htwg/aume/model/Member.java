package de.htwg.aume.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private UUID memberCookie;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private boolean isActive;

	public Member updateEstimation(String estimation) {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, estimation, this.memberCookie,
				this.isActive);
	}

	public Member resetEstimation() {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, null, this.memberCookie,
				this.isActive);
	}

	public Member setMemberCookie(UUID cookie) {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, this.currentEstimation, cookie,
				this.isActive);
	}

	public Member toggleActive(boolean isActive) {
		return new Member(this.memberID, this.name, this.hexColor, this.avatarAnimal, this.currentEstimation,
				this.memberCookie, isActive);
	}
}
