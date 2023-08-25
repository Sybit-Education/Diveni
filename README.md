# Diveni - The Planning-Poker App

![GitHub Repo stars](https://img.shields.io/github/stars/Sybit-Education/Diveni?style=social)
[![License](https://img.shields.io/badge/license-GNU%20AGPL%20v3-blue.svg)](LICENSE)
[![Crowdin](https://badges.crowdin.net/diveni/localized.svg)](https://crowdin.com/project/diveni)
[![Discord](https://img.shields.io/discord/935641426216222730?color=%237289DA&label=Discord&logo=Discord&logoColor=%237289DA)](https://discord.gg/7JmRyn5dc6)
<a href="https://www.producthunt.com/posts/diveni?utm_source=badge-featured&utm_medium=badge&utm_souce=badge-diveni" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/featured.svg?post_id=361171&theme=light" alt="Diveni - The&#0032;Planning&#0045;Poker&#0032;App | Product Hunt" style="width: 125px; height: 27px;" width="125" height="27" /></a>
![Code Coverage Lines](.github/badges/jacoco.svg)
![Code Coverage Branches](.github/badges/branches.svg)

---

## Hacktoberfest

Diveni participates on Hacktoberfest! We are looking forward to your support to improve this awesome app!

How to contribute:
1) ⭐ the repository
2) Connect to our Discord: [![Discord](https://img.shields.io/discord/935641426216222730?color=%237289DA&label=Discord&logo=Discord&logoColor=%237289DA)](https://discord.gg/7JmRyn5dc6)
3) Pick an existing [issue tagged `hacktoberfest`](https://github.com/Sybit-Education/Diveni/issues?q=is%3Aissue+is%3Aopen+label%3Ahacktoberfest) or create a new one (new feature or bug fixing)
4) Fork the repository and start working on your branch
5) Create a Pull Request to the original repo and wait for a code review
6) Have fun and learn new things

Happy coding 🚀

**Update 2022-11-02: Many thanks all contributors supporting us during Hacktoberfest 2022!** We are happy to announce that we had **15 PRs** where **11 PRs** have been merged successful during Hacktoberfest 🚀 
---

![DIVENI Logo](docs/assets/diveni_banner.png)

WebApp to do Planning Poker with remote teams using external issue tracker as source of stories.

Supported issue trackers:

- Atlassian JIRA on premise
- Atlassian JIRA Cloud
- Microsoft Azure DevOps
- More connectors are planned

## Read more

see: [Diveni Website](https://sybit-education.github.io/Diveni/)


---

## Some Screenshots

![Voters view of voted story](docs/img/userEstimationVoted.png)

![Host view voted story](docs/img/hostEstimationFinished.png)


## Application Instructions and Requirements

### Technologies

Frontend communicates via REST and WebSockets with the backend.
The backend uses Spring boot and communicates with the mongoDb in a docker infrastructure.

### Run Diveni with Docker and Docker Compose

- create empty file ``.env`` in directory ``backend`` first
- detailed documentation on the ``.env`` file can be found in the [docs](https://github.com/Sybit-Education/Diveni/blob/main/docs/guide/install.md)

#### Run with pre-built docker images using [docker-compose](https://github.com/Sybit-Education/Diveni/blob/main/docker-compose.yml)
```shell
docker-compose up -d
```
#### Run with self-build docker images using [docker-compose.dev](https://github.com/Sybit-Education/Diveni/blob/main/docker-compose.dev.yml)
```shell
docker-compose -f docker-compose.dev.yml up --build -d
```

### Run Diveni locally

#### Frontend
- npm, vue2
- switch to directory ``frontend`` first

```shell
npm install
npm run serve
```
 
#### Backend

- gradle, springboot, java11
- switch to directory ``backend`` first
- database must be started before

```shell
gradle bootRun
```

#### Database

- Mongodb in docker on port 27017, no credentials (Run via docker desktop)

```shell
docker run mongo
```

---

## Contributors

[![Diveni Contributors](https://contrib.rocks/image?repo=Sybit-Education/Diveni)](https://github.com/Sybit-Education/Diveni/graphs/contributors)

Made with [contrib.rocks](https://contrib.rocks).

## Credits

- Icons: [Animal by Thiago Silva](https://www.iconfinder.com/iconsets/animals-105), License: [CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/)

---

## License

[LICENSE](LICENSE)
