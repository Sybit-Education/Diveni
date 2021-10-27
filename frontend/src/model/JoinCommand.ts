interface JoinCommand {
    sessionID: string,
    name: string,
    password: string | null,
}

export default JoinCommand;
