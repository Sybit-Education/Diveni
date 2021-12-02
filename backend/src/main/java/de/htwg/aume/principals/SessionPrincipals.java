package de.htwg.aume.principals;

import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@EqualsAndHashCode
public class SessionPrincipals {

    private UUID sessionID;

    private AdminPrincipal adminPrincipal;

    private Set<MemberPrincipal> memberPrincipals;
}
