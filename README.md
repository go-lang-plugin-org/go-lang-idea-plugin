# Go plugin for IntelliJ

[![Build Status](https://teamcity.jetbrains.com/app/rest/builds/buildType:(id:IntellijIdeaPlugins_Go_Test)/statusIcon.svg?guest=1)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=IntellijIdeaPlugins_Go_Test&guest=1) [![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/go-lang-plugin-org/go-lang-idea-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
## Pre-release builds

**Supported IDEs**

The plugin can be installed on following IntelliJ-based:

- IntelliJ 2016.1+ (Ultimate or Community)
- WebStorm 2016.1+
- PhpStorm 2016.1+
- PyCharm 2016.1+
- RubyMine 2016.1+
- CLion 2016.1+
- Android Studio 1.2.1+

Pre-release builds are available in two forms: nightly and alphas. Alpha builds are usually released at the beginning of every week while nightly builds are released every night.

To use them do the following:

1. Use [the instructions](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html)
1. Paste the URL for the version you need:
 - alpha: https://plugins.jetbrains.com/plugins/alpha/5047
 - nightly: https://plugins.jetbrains.com/plugins/nightly/5047

**NOTE**
The above links are not meant to be used in browsers, so don't report issues
about them not working or being inaccessible unless there's an error in the IDE itself.

Since these are not stable releases, some things might not work as expected.

### Release versions schema

Bellow you can see the versions of the plugin which correspond to the versions of the 
IntelliJ Platfom (IntelliJ IDEA, WebStorm, PyCharm etc.):

| Plugin version number | Platform number |
| ---- | --- |
| 0.12.x | IntelliJ 2016.2 (IntelliJ IDEA 2016.2) | 
| 0.11.x | IntelliJ 2016.1 (IntelliJ IDEA 2016.1) | 
| 0.10.x | IntelliJ 143.1180 - 143.9999 (IntelliJ IDEA 15.0.2+) | 
| 0.9.x | IntelliJ 141.1532 - 141.9999 (IntelliJ IDEA 14.1) |
 
 If you are not using IntelliJ IDEA, then please check the build number of your IDE
 as that will correspond to the IntelliJ Platform version.

Reporting issues is very important for us as such, please see the section below
on how to submit a proper bug report.

## FAQ

Here's a list of the most frequently asked questions: [FAQ](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/wiki/FAQ)
 
## Bugs

If you've found a bug, which is not a duplicate, [report it](http://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues).

When reporting a bug, please include the following:
- IDEA version
- OS version
- JDK version
- Plugin version (or commit hash, if you built the plugin yourself)
- Detailed steps to reproduce (please include sample code)

## Bumping or +1 comments

Please don't comment with "bump", "+1", "same for me" or other irrelevant comments as they're useless for identifying the issue and finding the solution.

Contributions are always welcome and we'll do our best to make the most of them.

## Contributing

We encourage you to contribute to the plugin if you find any issues or missing
functionality that you'd like to see. In order to get started, see the
[contribution](CONTRIBUTING.md) guide.

## [People who helped](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/graphs/contributors)

## License

The Gopher icons are based on the Go mascot designed by [Renée French](http://reneefrench.blogspot.com/) and copyrighted under the [Creative Commons Attribution 3.0 license](http://creativecommons.org/licenses/by/3.0/us/).

The plugin is distributed under Apache License, version 2.0. For full license terms, see [LICENCE](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/blob/master/LICENCE).
