# Users Guide

*Guide for all users of Diveni.*

Diveni is a web application for Planning-Poker sessions. Sprint Planning often requires teams to
meet to estimate and plan user stories. With Diveni more flexibility is provided, team members can
do interactive planning from different locations. In addition, the mobile application offers JIRA
integration.

## Roles

There are two roles of users:

* **Host**: Person which starts, configures and hosts planning session.
* **Team**: Persons voting User Stories.

---

## Create new Session

To create a new session click on the "GO" button of the left window "New session".

<img :src="$withBase('/img/diveni_home.png')" alt="Home">    


After this you will be sent to *Prepare Session* page where you can customize the upcoming voting 
session for your team.

As first step you choose the mode for your User Stories. You can choose to estimate 

* Without User Stories 
* With User Stories created within Diveni. You are then able to create User Stories on the right side.
* With User Stories imported from Jira. Choosing the last option you’ll have to connect to your Jira 
  account. It will be explained how to connect and how to choose the correct project.

Any mode specific information is given below the chosen mode.

<img :src="$withBase('/img/prepare_your_session_selection.png')" alt="Prepare Session Screen">


## Create new Session without User Stories

At planning without User Stories you will vote with your selected cards and will not be able to add
User Stories once you've started the session.

- In the second row, you can decide which poker card set will be used for the upcoming voting
  session. You can choose between different sets: Fibonacci, T-shirt sizes (not for Jira),
  hours or integer values. You can also select individually the range of each card set, including
  an additional ```?```.

  <img :src="$withBase('/img/Select_card_set.png')" alt="">

- In the third row, you can specify a timer for the voting session. The timer will automatically be
  initialized when starting the voting session. The value of the timer can be changed by clicking on
  the buttons on the left and right. The voting will automatically be stopped and the results will be
  shown when the timer hits zero.
  
  <img :src="$withBase('/img/adjust_time.png')" alt="step 3_ Adjust time">

- To finish the setting, you can secure your session by an optional password.
  
  <img :src="$withBase('/img/diveni_home.png')" alt="Step 4: Choose password">

- Finalize by clicking „Start Session“ on the bottom of the page.

## Invite Team Members

## Start Session

## Start voting of User Story


## Screenshots

<img :src="$withBase('/img/userEstimationVoted.JPG')" alt="Voters view of voted story">

<img :src="$withBase('/img/hostEstimationFinished.JPG')" alt="Host view voted story">
