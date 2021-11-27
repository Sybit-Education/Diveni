import SessionConfig from './SessionConfig';

interface Session {
    sessionID: string;

    adminID: string;

    membersID: string;

    sessionConfig: SessionConfig;
}

export default Session;
