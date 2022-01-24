<br />
<div align="center">
  <img src="docs/assets/diveni_banner.png" width="560" />
  <img src="docs/assets/voting.png" />
  <img src="docs/assets/estimation_result.png" />
</div>
<br />
<p align="center">
  <img src=".github/badges/jacoco.svg" />
  <img src=".github/badges/branches.svg" />
</p>

---

## Usage

Create a meeting:

1. Click on the left "GO" button to create a new meeting.
2. You will be sent to the Prepare session page where you can customize the upcoming voting session for your team.
    - 1. In the first row you can decide which poker card set will be used for the upcoming voting session.
    You can chose between 3 sets, Fibonacci, T-shirt sizes and Hours. You can also select individually the values of each card set.
    - 2. In the second row, you can specify a timer for the voting session. The timer will automaticly be initialized when you start the voting session. The value of the timer can be changed by clicking on the two buttons on the left and right.
    The voting will automaticly be stoped and the results will be shown when the timer hits zero.
    - 3. In the third row, you can secure your meeting through an optional password.

Befor you start the session by clicking on the "Start session" button, you can add your User Stories. Although the Prepare session is a good place to prepare and add your User Stories for the upcoming vote session,
the host of the meeting can at any given time add new stories for the Planning-Poker meeting by clicking on the blue button at the upper right corner of the application.

    After clicking on the blue "Stories" button
        - add new stories with a title by clicking the "Add".
        - add a description for your stories by clicking on the drop down triangle.
        - click on the green arrow on the left to mark a spezifik story, so the members know which story they are estimating.
        - switch into the edit mode to edit your stories by clicking on the "Eddit" button
            - while being in the edit mode, you can delete stories or add the estimated result.
            - while being in the edit mode, click on the yellow "Save" button to save your changes.
    
    After you prepared your session, start the meeting.

3. You will be sent to a waiting room where you can see which members already joined the meeting.
    -invite members by hovering over the blue code and copy the Session-ID or a direct link which you can send to your team members.

    After your members have joined, start the planning.

4. After the voting has started, you can see at "Waiting for x/y" which members have already voted.
    You can restart the voting at any given time, by clicking on the "New" button.
    Don't forget to mark a story, so the members know which story they are estimating. 
    Click on the "Show result" button or wait for the timer to see what value your members have voted.

5. If you have estimated all your stories, click on the red button to end the meeting and get an overview of your final results for your stories. 

6. Download your results as a CSV file by clicking on the green button after ending the meeting or click on "Go to Home" to go to the landing page.


Join a meeting:

1. Click on the right "GO" button to join an existing meeting.

2. Enter your Name and the meeting-ID, or join via meeting-link and only enter your name and the optional password.
Click "GO" to join the meeting.

3. After joining the meeting you have to wait until the host starts the planning.

4. When the planning has started you can swipe or click depending on your device, to select your estimated value.
    You can open the story description in the upper right corner and check which story you are estimating.

5. The planning will be stopped by the host or when the timer hits zero.

Depending on your device, you can see the results of the other members.

6. The planning can be restarted at any given time by the host.


## Application Instructions and Requirements:

### Technologies:
Frontend communicates via REST and WebSockets with the backend.
It uses Spring boot and communicates directly with the mongoDb in a docker container.
Run the docker container first, then the backend.
#### Frontend
- npm, vue2
```shell
npm install
npm run serve
```
 
#### Backend
- maven, springboot, java11
```shell
mvn sping-boot:run
```

#### Database
- Mongodb in docker on port 27017, no credentials (Run via docker desktop)

Icons from https://www.iconfinder.com/iconsets/animals-105