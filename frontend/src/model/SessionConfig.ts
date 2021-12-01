import UserStory from "./UserStory";

interface SessionConfig {
    set: Array<String>;
    password?: string;
    userStories: Array<UserStory>;
}

export default SessionConfig;