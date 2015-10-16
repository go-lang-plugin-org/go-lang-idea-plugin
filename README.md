# Go plugin for IntelliJ

[![Build Status](https://teamcity.jetbrains.com/app/rest/builds/buildType:(id:IntellijIdeaPlugins_Go_Test)/statusIcon.svg?guest=1)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=IntellijIdeaPlugins_Go_GradleBuild&guest=1) [![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/go-lang-plugin-org/go-lang-idea-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
## Pre-release builds

**Supported IDEs**
Pre-release builds can be installed on IntelliJ platform 141.1532 or greater and less than 141.9999.

- IntelliJ IDEA 14.1.4 (Ultimate or Community)
- WebStorm 10.0.4
- PhpStorm 9.0
- PyCharm 4.5.2+
- RubyMine 7.1.3+
- CLion 1.0.3+
- Android Studio 1.2.1+

Pre-release builds are available in two forms: nightly and alphas. At the time
of writing, alpha builds are released usually at the start of every week while
nightly builds are released every night, using the latest buildable source.

To use them, you'll need to follow these steps:

1. Follow [the instructions](https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html)
1. Paste the URL for the version you desire:
 - alpha: https://plugins.jetbrains.com/plugins/alpha/5047
 - nightly: https://plugins.jetbrains.com/plugins/nightly/5047

As always, please remember that this is a preview release and while efforts are
made to ensure the best possible experience, sometimes things might be broken.

**NOTE**
The above links are not meant to be used in browsers. As such, don't report issues
about them not working or being inaccessible unless you are getting an error from
the IDE itself.

Reporting issues is very important for us as such, please see the section below
on how to submit a proper bug report.

## FAQ

Here's a list of the most frequent asked questions: [FAQ](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/wiki/FAQ)
 
## Bugs

**Please ensure that the bug is not reported already using the search functionality**

If you found a bug, and it's not a duplicate, you can report it here:
<http://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues>.

When reporting a bug, please include the following:
- IDEA version
- OS version
- JDK version
- plugin version (or commit hash, if you built the plugin yourself)
- steps to reproduce the issue as detailed as possible
- when possible, sample code that can reproduce the issue is crucial to the ability / time to fix the said issue (and greatly appreciated)

## Bumping or +1 or me as well comments

As annoying as things might be when they don't work, please don't comment with
"bump", "+1", "same for me" or another form of comments which is not relevant for
either better identifying the issue providing a solution to it.

If you want to mark that the said issue is meaningful for you, go ahead and
contact Github support to add an issue voting feature to the repositories by
writing them here: https://github.com/contact

Contributions are always welcomed and we'll do our best to help you out to add
yours to the project.

## Contributing

We encourage you to contribute to the plugin if you find any issues or missing
functionality that you'd want to see. In order to get started, see
[CONTRIBUTING.md](CONTRIBUTING.md) guide.

## Contributors

People who helped with the plugin development, please see [contributors page](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/graphs/contributors).

## Thank you

We would like to thank [YourKit](http://www.yourkit.com) for supporting the development
of this plugin by providing us licenses to their full-featured [Yourkit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp).

YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:

- [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp)
- [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp)
