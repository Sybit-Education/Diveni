# Translations Guide

Translations for Diveni are maintained on [Crowdin](https://crowdin.com/project/diveni).

Crowdin is a web-based translation tool that can be used without knowledge of programming.

You can help by contributing to the improvement and expansion of our translations for Diveni. 
If you find a mistake or if your preferred language is not yet available, you can report and 
update it on Crowdin: [https://crowdin.com/project/diveni](https://crowdin.com/project/diveni).

::: warning Important:
Do not update translations directly in the source files on GitHub. Crowdin will overwrite them!
Only English labels are maintained in the project.
:::

## Translation Process

```mermaid
graph TD
  A1[Source Code: New English texts are added]
  A2[Scheduled Extraction]
  A3[New texts are automatically extracted and uploaded to Crowdin]

  B[Crowdin: Translations are maintained]
  C1[Scheduled Automation]
  C2[Translations are automatically exported and made available]
  D[Pull Request is created for the Vue project]
  E[Code Review & Verification]
  F[PR is merged and changes are deployed to the live project]

  A1 --> A2
  A2 --> A3
  A3 --> B
  B --> C1
  C1 --> C2
  C2 --> D
  D --> E
  E --> F
``` 
