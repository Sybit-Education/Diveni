import Member from './Member';
import SessionConfig from './SessionConfig';

interface Session {
    sessionID: string;

    adminID: string;

    sessionConfig: SessionConfig;

    sessionState: string;
}

export default Session;
