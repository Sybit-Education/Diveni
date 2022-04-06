# Introduction

Diveni is a web application for Planning-Poker sessions.
Sprint Planning often requires teams to meet to estimate and plan user stories.
With Diveni more flexibility is provided, team members can do interactive planning from different locations. 
In addition, the mobile application offers JIRA integration.

# Usage

## Create new session

To create a new session click on the "GO" button of the left window "New session".
    
    ![Home](/img/diveni_home.png)

After this you will be sent to the Prepare session page where you can customize the upcoming voting session for your team. 
As first step you choose the mode for your User Stories. You can choose to estimate
   1. Without User Stories
   2. With User Stories created in Diveni. You are then able to create User Stories on the right side.
   3. With User Stories imported from Jira. Choosing the last option you’ll have to connect to your Jira account. It will be explained how to connect and how to choose the correct project.
      Any mode specific information is given below the chosen mode.

   ![User_Stories_mode](/img/prepare_your_session_selection.png)


## Create new session without User Stories

At planning without User Stories you will vote with your selected cards and will not be able to add User Stories once you've started the session.
- In the second row, you can decide which poker card set will be used for the upcoming voting session. You can choose between different sets: Fibonacci, T-shirt sizes (not for Jira US), hours or integer values. You can also select individually the range of each card set, including an additional „?“.

    ![Step_2_Select_card_set](/img/Select_card_set.png)       


- In the third row, you can specify a timer for the voting session. The timer will automatically be initialized when starting the voting session. The value of the timer can be changed by clicking on the buttons on the left and right. The voting will automatically be stoped and the results will be shown when the timer hits zero.

    ![Step_3_Adjust_time](/img/adjust_time.png)


- To finish the setting, you can secure your session by an optional password.

    ![Step_4_Choose_password](/img/Choose_password.png)


- Finalize by clicking „Start Session“ on the bottom of the page.


## Screenshots

![Voters view of voted story](/img/userEstimationVoted.JPG)

![Host view voted story](/img/hostEstimationFinished.JPG)

