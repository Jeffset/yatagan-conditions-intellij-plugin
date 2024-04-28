# Yatagan IntelliJ Plugin for Conditions

<!-- PLUGIN DESCRIPTION -->
An unofficial plugin providing IDE support for [Yatagan](https://github.com/yandex/yatagan)'s `@Condition`/`@ConditionExpression`.

### Functionality
- Syntax highlighting for values inside `@ConditionExpression` and legacy `@Condition` annotations.
- Correct usage tracking and rename refactorings with referenced _features_ and _conditions_.
- Naive autocomplete for _condition_ path members.

### What is not here (yet?)
There is no building and validating yatagan graphs functionality
available.
Doesn't share code with Yatagan, so highlighting and error reporting is done on the best effort basis.
<!-- END PLUGIN DESCRIPTION -->

## How to install

Navigate to the repository's releases, download the latest available plugin jar and
install it in the IDE via the `Plugins > ⚙ > "Install plugin from disk"` option.