interface JoinCommand {
  sessionID: string;
  name: string;
  password: string | null;
  jobTitle: string | null;
}

export default JoinCommand;
