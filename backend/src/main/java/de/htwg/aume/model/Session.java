package de.htwg.aume.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Document("sessions")
public class Session {

	@Id
	private final UUID sessionID;

	private final UUID adminID;

	private final UUID membersID;

}
