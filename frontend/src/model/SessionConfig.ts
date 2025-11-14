import UserStory from "./UserStory";

interface SessionConfig {
  cardSetType: string;
  set: Array<string>;
  password?: string;
  timerSeconds: number;
  userStories: Array<UserStory>;
  userStoryMode: string;
}

export default SessionConfig;
