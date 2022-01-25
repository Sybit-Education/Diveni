interface UserStory {
  jiraId: number | null;
  title: string;
  description: string;
  estimation: string | null;
  isActive: boolean;
}

export default UserStory;
