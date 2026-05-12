class Constants {
  backendURL = import.meta.env.VITE_SERVER_API_URL;

  i18nDefaultLocale = import.meta.env.VITE_I18N_DEFAULT_LOCALE;
  i18nFallbackLocale = import.meta.env.VITE_I18N_FALLBACK_LOCALE;

  createSessionRoute = "/sessions";

  getDiveniAnalytics = "/analytics/All";

  webSocketRegisterAdminUserRoute = "/ws/registerAdminUser";

  webSocketRegisterMemberRoute = "/ws/registerMember";

  webSocketUnregisterRoute = "/ws/unregister";

  webSocketKickMemberRoute = "/ws/kick-member";

  webSocketCloseSessionRoute = "/ws/closeSession";

  webSocketMemberListenHostVotingRoute = "/users/updates/hostVoting";

  webSocketGetMemberUpdateRoute = "/ws/memberUpdate";

  webSocketMembersUpdatedRoute = "/users/updates/membersUpdated";

  webSocketStartPlanningRoute = "/ws/startVoting";

  webSocketVotingFinishedRoute = "/ws/votingFinished";

  webSocketRestartPlanningRoute = "/ws/restart";

  webSocketVoteRoute = "/ws/vote";

  webSocketVoteRouteAdmin = "/ws/vote/admin";

  webSocketMembersUpdatedHostEstimation = "/users/updates/hostEstimation";

  webSocketAdminUpdatedUserStoriesRoute = "/ws/adminUpdatedUserStories";

  webSocketAdminSelectedUserStoryRoute = "/ws/adminSelectedUserStory";

  webSocketSelectedUserStoryRoute = "/users/updates/userStorySelected";

  webSocketMemberListenUserStoriesRoute = "/users/updates/userStories";

  webSocketTimerStartRoute = "/users/updates/startTimer";

  webSocketMemberListenRoute = "/users/updates/member";

  webSocketMemberAutoRevealListenRoute = "/users/updates/member/autoreveal";

  websocketNotification = "/users/updates/notifications";

  webSocketErrorRoute = "/users/updates/error";

  memberUpdateCommandStartVoting = "START_VOTING";

  memberUpdateCommandVotingFinished = "VOTING_FINISHED";

  memberUpdateCloseSession = "SESSION_CLOSED";

  newsPageSize = 9;

  botUserType = "Bot";

  public joinSessionRoute(sessionID: string) {
    return `/sessions/${sessionID}/join`;
  }

  public getRandomPastelColor() {
    const l2 = (85 + 10 * Math.random()) / 100;
    const a = ((25 + 70 * Math.random()) * Math.min(l2, 1 - l2)) / 100;
    const f = (n: number) => {
      const k = (n + (360 * Math.random()) / 30) % 12;
      const color = l2 - a * Math.max(Math.min(k - 3, 9 - k, 1), -1);
      return Math.round(255 * color)
        .toString(16)
        .padStart(2, "0");
    };
    return `#${f(0)}${f(8)}${f(4)}`;
  }

  public hexToRgb(hex: string) {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return {
      r: parseInt(result![1], 16),
      g: parseInt(result![2], 16),
      b: parseInt(result![3], 16),
    };
  }

  public getRandomAvatarAnimalAssetName() {
    const animals = [
      "bull",
      "camel",
      "cat",
      "cow",
      "dog",
      "duck",
      "elephant",
      "fish",
      "giraffe",
      "goose",
      "horse",
      "lion",
      "monkey",
      "mouse",
      "pigeon",
      "rabbit",
      "tiger",
      "turtle",
      "wolf",
      "zebra",
    ];
    const num = Math.random() * (animals.length - 0);
    return animals[parseInt(num.toString(), 10)];
  }

  public avatarAnimalAssetNameToBackendEnum(assetName: string) {
    return assetName.split(".")[0].toUpperCase();
  }
}

export default new Constants();
