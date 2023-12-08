import SessionConfig from "./SessionConfig";

interface Session {
  sessionID: string;

  adminID: string;

  sessionConfig: SessionConfig;

  sessionState: string;

  hostVoting: boolean;
}

export default Session;
