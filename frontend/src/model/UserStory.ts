interface UserStory {
  id: string | null;
  title: string;
  description: string;
  estimation: string | null;
  isActive: boolean;
}

export default UserStory;
