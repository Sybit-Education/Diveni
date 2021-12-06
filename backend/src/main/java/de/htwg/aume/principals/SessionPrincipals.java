package de.htwg.aume.principals;

import java.util.Set;

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

    private String sessionID;

    private AdminPrincipal adminPrincipal;

    private Set<MemberPrincipal> memberPrincipals;
}
