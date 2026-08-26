# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Intermediate
* IDE and level of expertise: Intermediate

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Follow the course's Git conventions for every commit: https://se-education.org/guides/conventions/git.html

Commit message subject line:
* Limit to 50 characters (72 is a hard limit).
* Use the imperative mood (e.g. "Add README.md", not "Added README.md").
* Capitalize the first letter.
* Do not end with a period.
* May optionally be prefixed with a scope/category, e.g. `Person class:` or `bug fix:`.

Commit message body (when needed):
* Separate from the subject with one blank line.
* Wrap at 72 characters; use blank lines between paragraphs and bullet points where useful.
* Explain WHAT and WHY, not HOW — the current situation, why the change is needed, what's being done, why done that way, and any other relevant info.

Branch names:
* kebab-case with meaningful keywords, e.g. `refactor-ui-tests`.
* For issue-related branches, use `issueNumber-some-keywords-from-issue-title`, e.g. `1234-ui-freeze-error`.

Other:
* Use lightweight tags unless the user requests an annotated tag.
* Do not commit or push unless explicitly asked.

## Testing after code changes

After making any code change that could affect the chatbot's console
behavior (a new/changed command, output wording, or formatting):

1. Update `test/ui-test-plan.md` if needed — add a test case for new
   behavior, or update the expected output of an existing test case if
   the change was intentional.
2. Invoke the `test-ui` skill to run the test plan against the current
   build and confirm nothing regressed before considering the change
   done.
