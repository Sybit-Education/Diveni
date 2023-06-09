interface UserStory {
  id: number | null;
  title: string;
  description: string;
  estimation: string | null;
  isActive: boolean;
  position: number;
}

export default UserStory;
