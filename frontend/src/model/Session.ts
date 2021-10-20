import UUID from './UUID';

export default class Session {
    sessionID: UUID;

    adminID: UUID;

    membersID: UUID;

    constructor(sessionID: UUID, adminID:UUID, membersID: UUID) {
      this.sessionID = sessionID;
      this.adminID = adminID;
      this.membersID = membersID;
    }

    static fromJson(object:Record<string, unknown>): Session | null {
      try {
        return new Session(
          UUID.fromString(object.sessionID as string)!,
          UUID.fromString(object.sessionID as string)!,
          UUID.fromString(object.sessionID as string)!,
        );
      } catch (e) {
        return null;
      }
    }
}
