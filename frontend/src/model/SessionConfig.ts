import UserStory from "./UserStory";
interface SessionConfig {
  set: Array<string>;
  setName: string;
  password?: string;
  timerSeconds: number;
  userStories: Array<UserStory>;
  userStoryMode: string;
  team: string;
}

export default SessionConfig;
