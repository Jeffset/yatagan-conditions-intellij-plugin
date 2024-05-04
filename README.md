# Yatagan IntelliJ Plugin for Conditions

[![CI][ci-svg]][ci-workflow]
[![JetBrains plugins][plugin-version-svg]][plugin-repo]

<!-- PLUGIN DESCRIPTION -->
An unofficial plugin providing IDE support for [Yatagan](https://github.com/yandex/yatagan)'s `@Condition`/`@ConditionExpression`.

# Functionality
- Syntax highlighting for values inside `@ConditionExpression` and legacy `@Condition` annotations.
- Correct usage tracking and rename refactorings with referenced _features_ and _conditions_.
- Naive autocomplete for _condition_ path members.

# What is not here
There is no building and validating yatagan graphs functionality
available.
Doesn't share code with Yatagan, so highlighting and error reporting is done on the best effort basis.
<!-- END PLUGIN DESCRIPTION -->

## Installation & Usage

⚙ In your IDE go to **Settings | Plugins**. Switch to the **Marketplace** tab
and search for the _Yatagan Conditions_ plugin, then click **Install**.

📤 Condition expression strings inside Yatagan's annotations will start being highlighted right away.

🌈 Highlighting colors can be customized as usual in **Settings | Editor | Color Scheme | Yatagan Conditions**.

[ci-svg]: https://github.com/Jeffset/yatagan-conditions-intellij-plugin/actions/workflows/ci.yml/badge.svg
[ci-workflow]: https://github.com/Jeffset/yatagan-conditions-intellij-plugin/actions/workflows/ci.yml
[plugin-repo]: https://plugins.jetbrains.com/plugin/24330-yatagan-conditions
[plugin-version-svg]: https://img.shields.io/jetbrains/plugin/v/24330-yatagan-conditions.svg