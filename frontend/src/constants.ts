class Constants {
  backendURL = process.env.NODE_ENV === 'development' ? 'http://localhost:9090' : '/api';

  createSessionRoute = '/sessions';

  webSocketRegisterAdminUserRoute = '/ws/registerAdminUser';

  webSocketRegisterMemberRoute = '/ws/registerMember';

  webSocketMembersUpdatedRoute = '/users/updates/membersUpdated';

  webSocketStartPlanningRoute = '/ws/startVoting';

  webSocketRestartPlanningRoute = '/ws/restart';

  webSocketVoteRoute = '/ws/vote';

  webSocketMemberListenRoute = '/users/updates/member';

  memberUpdateCommandStartVoting = 'START_VOTING';

  // eslint-disable-next-line class-methods-use-this
  public joinSessionRoute(sessionID: string) {
    return `/sessions/${sessionID}/join`;
  }

  // eslint-disable-next-line class-methods-use-this
  public getRandomPastelColor() {
    const l2 = (85 + 10 * Math.random()) / 100;
    const a = ((25 + 70 * Math.random()) * Math.min(l2, 1 - l2)) / 100;
    const f = (n:number) => {
      const k = (n + (360 * Math.random()) / 30) % 12;
      const color = l2 - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);
      return Math.round(255 * color).toString(16).padStart(2, '0');
    };
    return `#${f(0)}${f(8)}${f(4)}`;
  }

  // eslint-disable-next-line class-methods-use-this
  public hexToRgb(hex: string) {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return {
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      r: parseInt(result![1], 16),
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      g: parseInt(result![2], 16),
      // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
      b: parseInt(result![3], 16),
    };
  }

  // eslint-disable-next-line class-methods-use-this
  public getRandomAvatarAnimalAssetName() {
    // TODO implement;
    return 'wolf.png';
  }

  // eslint-disable-next-line class-methods-use-this
  public avatarAnimalAssetNameToBackendEnum(assetName:string) {
    return assetName.split('.')[0].toUpperCase();
  }

  // eslint-disable-next-line class-methods-use-this
  public avatarAnimalToAssetName(animal:string) {
    return `${animal.toLowerCase()}.png`;
  }
}

export default new Constants();
