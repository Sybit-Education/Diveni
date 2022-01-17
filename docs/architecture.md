Software architecture
---------------------

##Content Overview

    1 Context diagram
    2 Component diagram
    3 Database
    4 Distribution diagram
    5 Testing
    6 Deployment

1 Context diagram
----------------------

The diagram below is a context diagram. It serves to illustrate the system from a black box perspective. Furthermore, interfaces to other systems are also depicted here. 
A connection to a Jira instance is required.

![context-diagram](./assets/diagram/context-diagram.png)

2 Component diagram
--------------------------------------

Context delineation provided insight of the system from a black box perspective. In the building block view
the internal structure and the components to be developed are now considered.

![components-diagram](./assets/diagram/components-diagram.png)

## Component Overview

### Frontend

| Component | Description   |
| --------- | -----------   |
| CardSetComponent |  |
| CopySessionIdPopup |  |
| Estimatetimer |  |
| JiraComponent |  |
| JoinPageCard |  |
| LandingPageCard |  |
| LocaleDropdown |  |
| MemberVoteCard |  |
| RoundedAvatar |  |
| SessionMemberCard |  |
| SessionMemberCircle |  |
| StoryPointsComponent |  |
| UserStoriesSidebar |  |
| UserStoryComponent |  |
| ----------- | |
| JoinCommand |  |
| Member |  |
| Session |  |
| SessionConfig |  |
| UserStory |  |
| ----------- | |
| JoinPage |  |
| LandingPage |  |
| MemberVotePage |  |
| PrepareSessionPage |  |
| ResultPage |  |
| SessionPage |  |
| ----------- | |
| Router |  |
| ----------- | |
| Store |  |

### Backend

| Component | Description   |
| --------- | -----------   |
| ControllerUtils |  |
| ErrrorMessages | All error messages which can be thrown in exceptions |
| RoutesController | REST Controller |
| WebsocketController | WebSocket Controller |
| ----------- | |
| PrincipalWebSocketHandler | Set anonymous user (Principal) in WebSocket messages by using uri query UUIDs. This is necessary to avoid broadcasting messages but sending them to specific user sessions |
| SessionDisconnectedListener | Listener when a user disconnects from a session |
| ----------- | |
| AvatarAnimal | Enum for the avatar animals |
| JoinInfo | DTO to join a session |
| Member | Member model |
| Session | Session model |
| SessionConfig | Configuration for session (e.g. set, timer) |
| SessionState | Enum for the current state of the session |
| UserStory | User Story model |
| ----------- | |
| AdminPrincipal | Principal for admin |
| MemberPrincipal | Principal for member |
| SessionPrincipals | Principal for session |
| ----------- | |
| SessionRepository | Repository to save session data in the database |
| ----------- | |
| DatabaseService | Service to call the session repository |
| WebSockerService | Business logic for the WebSocket controller |


3 Database
----------




4 Distribution diagram
-------------------

The distribution of the product during development is shown in the following diagram:

![distribution-diagram](./assets/diagram/distribution-diagram.png)

5 Testing
---------

Functionalities of the application must be tested sufficiently to ensure quality. 
Backend methods are tested by Java unit tests. The test coverage is at least 75%.
Front-end components are tested using smoke tests on a test server.
Github runners are also used to automatically trigger and verify the tests.

6 Deployment
-------------

