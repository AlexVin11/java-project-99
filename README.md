### Hexlet tests and linter status:
[![Actions Status](https://github.com/AlexVin11/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/AlexVin11/java-project-99/actions)
[![Maintainability](https://api.codeclimate.com/v1/badges/dc121359ba93aab28290/maintainability)](https://codeclimate.com/github/AlexVin11/java-project-99/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/dc121359ba93aab28290/test_coverage)](https://codeclimate.com/github/AlexVin11/java-project-99/test_coverage)

# Project overview
See it here https://taskmanager-v3pj.onrender.com

Project represents a simple task manager that provides to user a tool for CRUD a task, configure it and assign it to another user.

These task statuses created by default: Draft, To review, To be fixed, To publish, Published.

These labels created by default: bug, feature.

# Note:
Only authenticated Users can work with swagger and use the app.

To go to Swagger add "/swagger-ui/index.html" to url.

User can delete and edit himself only.

User cant be deleted if he has assigned Task.

Task status cant be deleted if it connected to the Task.

Task cant be created without Task status.

# Standard workflow looks like this:
1. Log in or create a new user.
2. Create a new Task status and Task label if needed.
3. Create a new Task and choose a Task status and label.
4. Assign created Task to another User.