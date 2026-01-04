# Project Lint Rules

This document outlines the coding standards and linting rules for the Alarm Game project. All AI agents and developers should follow these rules.

## 1. General Formatting (via ktlint)
- **Indent**: 4 spaces.
- **Max Line Length**: 120 characters.
- **Imports**: 
    - No unused imports.
    - No wildcard imports (`import foo.*`).
    - Sorted alphabetically.
- **Braces**: Standard Kotlin bracing style (OTBS).
- **Spacing**: Standard spacing around operators, colons, and parentheses.

## 2. Naming Conventions
- **Classes/Interfaces**: `PascalCase` (e.g., `AlarmService`).
- **Functions/Variables**: `camelCase` (e.g., `startAlarm`, `userPreferences`).
- **Composables**: `PascalCase` (e.g., `AlarmListScreen`).
- **Constants**: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`).
- **Resource Files**: `snake_case` (e.g., `activity_main.xml`, `ic_alarm_on.xml`).

## 3. Code Organization
- **Package Structure**: Follow clean architecture layers (`domain`, `data`, `ui`, `platform`).
- **File Order**: Imports -> Constants -> Class Definition -> Members -> Companion Object -> Internal Classes.

## 4. Best Practices
- **Comments**: Explain *why*, not *what*. Avoid obvious comments.
- **Visibility**: Use `private` unless external access is strictly required. Use `internal` for module-scoped classes.
- **Coroutines**: Always inject `Dispatcher`s (via Hilt) for testability.
- **Compose**: Move complex logic to ViewModels. UI components should only worry about rendering state.

## 5. Enforcement
- This project uses [ktlint](https://pinterest.github.io/ktlint/) for automated formatting.
- Run `./gradlew ktlintCheck` to verify.
- Run `./gradlew ktlintFormat` to auto-fix issues.
