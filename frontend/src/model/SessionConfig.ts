import UserStory from './UserStory';

interface SessionConfig {
    set: Array<string>;
    password?: string;
    userStories: Array<UserStory>;
}

export default SessionConfig;
