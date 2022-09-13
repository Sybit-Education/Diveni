# Contributing to Diveni

:+1::tada: First off, thanks for taking the time to contribute! :tada::+1:

The following is a set of guidelines for contributing to *Diveni* and
its packages, which are hosted by [Diveni](https://github.com/Sybit-Education/Diveni)
on GitHub.

These are mostly guidelines, not rules. Use your best judgment, and feel free to propose
changes to this document in a pull request.

## What should I know before I get started?

The documentation of the project could be found at [https://docs.diveni.io](https://docs.diveni.io).
Source of these is stored in `/docs` within the repository.

### Architecture of Diveni

Diveni is a web based app which is separated in two main components:

* Backend: Java based business logic based on Spring Boot.
* Frontend: Vue.js based web UI.

These components are bundled within Docker containers.

For more details, see: [docs/guide/architecture.md](docs/guide/architecture.md).

## How Can I Contribute?

### Reporting Bugs

This section guides you through submitting a bug report. Following these
guidelines helps maintainers and the community understand your report :pencil:,
reproduce the behavior :computer: :computer:, and find related reports :mag_right:.

Before creating bug reports, please check [this list](#before-submitting-a-bug-report) as
you might find out that you don't need to create one. When you are creating a bug report,
please [include as many details as possible](#how-do-i-submit-a-good-bug-report). Filling
out the information helps us resolve issues faster.

> **Note:** If you find a **Closed** issue that seems like it is the same thing that
you're experiencing, open a new issue and include a link to the original issue in
the body of your new one.

#### Before Submitting A Bug Report

- **Perform a [cursory search](https://github.com/Sybit-Education/Diveni/issues?q=)**
  to see if the problem has already been reported. If it has **and the issue is still open**, add a comment
  to the existing issue instead of opening a new one.

#### How Do I Submit A (Good) Bug Report?

Bugs are tracked as [GitHub issues](https://github.com/Sybit-Education/Diveni/issues).

Explain the problem and include additional details to help maintainers reproduce the problem:

- **Use a clear and descriptive title** for the issue to identify the problem.
- **Provide specific examples to demonstrate the steps**. Include links to files
  or copy/pasteable snippets, which you use in those examples. If you're providing
  snippets in the issue, use
  [Markdown code spans and blocks](https://docs.gitlab.com/ee/user/markdown.html#code-spans-and-blocks).
- **Describe the behavior you observed after following the steps** and point out what
  exactly is the problem with that behavior.
- **Explain which behavior you expected to see instead and why.**

### Suggesting Enhancements

This section guides you through submitting an enhancement suggestion for Diveni,
including completely new features and minor improvements to existing functionality. Following
these guidelines helps maintainers and the community understand your suggestion :pencil:
and find related suggestions :mag_right:.

Before creating enhancement suggestions, please check
[this list](#before-submitting-an-enhancement-suggestion) as you might find out that you
don't need to create one. When you are creating an enhancement suggestion, please
[include as many details as possible](#how-do-i-submit-a-good-enhancement-suggestion).
Fill in the template, including the steps that you imagine you would take if the feature
you're requesting existed.

#### Before Submitting An Enhancement Suggestion

- **Perform a [cursory search](https://github.com/Sybit-Education/Diveni/issues?q=)**
  to see if the enhancement has already been suggested. If it has, add a comment to the
  existing issue instead of opening a new one.

#### How Do I Submit A (Good) Enhancement Suggestion?

Enhancement suggestions are tracked as [GitHub issues](https://github.com/Sybit-Education/Diveni/issues).
After you've determined [which repository](#repositores) your enhancement suggestion is related to, create
an issue on that repository and provide the following information:

- **Use a clear and descriptive title** for the issue to identify the suggestion.
- **Provide a step-by-step description of the suggested enhancement** in as many details as possible.
- **Provide specific examples to demonstrate the steps**. Include copy/pasteable snippets which you use
  in those examples, as [Markdown code spans and blocks](https://docs.gitlab.com/ee/user/markdown.html#code-spans-and-blocks).
- **Describe the current behavior** and **explain which behavior you expected to see instead** and why.

## Development

### Software Requirements

- Java 11+
  - We prefere SapMachine but any other Java JDK 11+ is fine.
  - Download from SapMachine <https://sap.github.io/SapMachine/>
  
      <img :src="$withBase('/img/Java11_Installer_Selection.png')" alt="Download_Selection_Java11">
  - Select the appropriate installer for your OS
- Maven
  - Download from Apache <https://maven.apache.org/download.cgi>
    <img :src="$withBase('/img/Maven_Installer_Selection.png')" alt="Download_Selection_Maven">
  - Click the link in the marked line
- Node.js 14+
  - Download from nodejs <https://nodejs.org/en/download/>
  
    <img :src="$withBase('/img/nodejs_Installer_Selection.png')" alt="Download_Selection_nodejs">
  - Select the appropriate installer for your OS
- MongoDB 4+
  - MongoDB is running in the background at port 27017
  - Select the appropriate installer for your OS and follow the instructions
    <https://www.mongodb.com/docs/manual/administration/install-community/>
  
    <img :src="$withBase('/img/MongoDB_Installer_Selection.png')" alt="Download_Selection_MongoDB">


### Setup Project

1. Clone repository with Git from GitHub:
    ```shell
       git clone https://github.com/Sybit-Education/Diveni.git
    ```
2. Start backend
  * Open  first terminal and enter these commands:
    ```shell
        cd /backend
        mvn spring-boot:run
    ```
  * The backend should now be running and accessible at <http://localhost:8081>
3. Start frontend
  * Open the second terminal and enter these commands:
    ```shell
        cd /frontend
        npm install
        npm run serve
    ```
  * The frontend should now be running and accessible at <http://localhost:8080>
4. To use Diveni simply enter the following url in your standard browser: <http://localhost:8080>


## Connectors

### JIRA Cloud

    TODO

### JIRA Enterprise

For setting up the JIRA application, the steps described here should be sufficient:
<https://developer.atlassian.com/server/jira/platform/oauth/>

It doesn't matter what you enter in remaining fields (URL, name, type, and so on).
This is because we only want to retrieve data from Jira, therefore we only need to set up a
one-way (incoming) link from the client to Jira.

Afterwards, you will see the client secret and ID, which needs to be provided for Diveni on
JIRA-Server.

### Merge Requests

The process described here has several goals:

- Maintain quality
- Fix problems that are important to users
- Engage the community in working toward the best possible results
- Enable a sustainable system for maintainers to review contributions

Please follow these steps to have your contribution considered by the maintainers:

1. Follow the [styleguides](#styleguides)
2. After you submit your pull request, verify that all status checks are passing
   <details><summary>What if the status checks are failing?</summary>
   If a status check is failing, and you believe that the failure is unrelated to your
   change, please leave a comment on the pull request explaining why you believe the
   failure is unrelated. A maintainer will re-run the status check for you. If we
   conclude that the failure was a false positive, then we will open an issue to track
   that problem with our status check suite.</details>

While the prerequisites above must be satisfied prior to having your pull request
reviewed, the reviewer(s) may ask you to complete additional design work, tests,
or other changes before your pull request can be ultimately accepted.

## Styleguides

### Git Commit Messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line

### Java Styleguide

We use [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

### JavaScript and Vue.js Styleguide

We use [standard.js](https://standardjs.com/) to enforce the style guide for JavaScript and Vue.js code.

The [standard.js](https://standardjs.com/) style guide is a [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript) style guide.

To format frontend code with standard.js, you can use the following command within
directory `webui`:

    npm run lint:fix

### Documentation Styleguide

- Use [Markdown](https://daringfireball.net/projects/markdown).
- For Use Case / UML diagrams use inline [Mermaid](https://mermaid-js.github.io/mermaid/#/) format.
- Reference methods and classes in markdown with the custom `{}` notation:
  - Reference classes with `{ClassName}`
  - Reference instance methods with `{ClassName::methodName}`
  - Reference class methods with `{ClassName.methodName}`


## Troubleshooting

- When working with IntelliJ and starting Diveni's Backend with IntelliJs editor it may throw an
  exception when trying to connect with Jira.\
  This is because it can not find the desired .env file in your working directory (/backend).
  When this happens make sure to include ```/backend``` to your run working directory in the run
  configuration settings.
