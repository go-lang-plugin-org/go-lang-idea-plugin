# How to contribute

[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/go-lang-plugin-org/go-lang-idea-plugin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## TOC
+ [Reporting errors](#reporting-errors)
+ [Contributing to the code](#contributing-to-the-code)
+ [Submitting test cases](#submitting-test-cases)
+ [Getting started with the plugin development](#getting-started-with-the-plugin-development)
+ [Checking out and building Google Go](#checking-out-and-building-google-go)
+ [Checking out the plugin sources](#checking-out-the-plugin-sources)
+ [Building and running the unit tests](#building-and-running-the-unit-tests)
+ [Profit](#profit)
+ [Useful links](#useful-links)

## Reporting errors

Before reporting an error, please read the [FAQ](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/wiki/FAQ)
and search for the issue in the issue tracker. Also, please don't bump, +1
or "me too" issues. Thank you.

The simplest way to contribute to the plugin is to report issues you encounter
in your day to day use.

As a rule of thumb, always keep in mind that we are developers just like you. So,
whenever you are going to report an issue, think of how you'd like to receive issues
for your projects. Also, we are doing this in our spare time, so the more information
we have in the report, the faster we can replicate the problem and get on solving it
rather that just doing a bunch of ping-pong in comments trying to extract the needed
details from you.

As of 0.9.16 release, the plugin has a way to catch the exceptions it generates
and send them to us as an issue on Github but we still need to understand the
context in which the error happens. As such, whenever you are using the built-in
error reporting facility, be as descriptive as possible.

This information applies also when you don't have an error that is caught by the
built-in facility but it's something that happens and shouldn't happen (say for
example, a formatting issue).

First and foremost, we really need to know the following:
- IDEA version
- OS version
- JDK version
- plugin version (or commit hash, even better, if possible)

Also, every time you can, submit the piece of code that's generating the issue.
As it might be some proprietary code, take some time and write the smallest code
sample that can reproduce it and paste it in the issue (or send it as a link to
[Go Playground](http://play.golang.org/). Screenshots are useful, but, just like
you, we can't copy paste code from screenshots either.

Please ensure that the bug is not reported already, this helps us focusing on
working on bug fixes not triage work.

## Contributing to the code

If you want to contribute to the code, go to GitHub and check out the latest version
and follow the instructions on how to build the plugin from source. After that, you
can start picking some [pending tasks](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues) on the issue tracker.

Make sure you look for issues tags with [up for grabs](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/labels/up%20for%20grabs)
as these are some of the easier ones to get started with.

### CLA requirement

Contributing to the plugin requires a signed CLA with JetBrains.
You can view the steps necessary for this at [this page](http://www.jetbrains.org/display/IJOS/Contribute#Contribute-ContributeCode).

### Submitting test cases

Submitting test cases is the next best thing you can do to developing on this
project. In fact, you'll actually develop on it since the test code you are
going to contribute is still code.

Whenever your time or knowledge allows, submitting good test cases for either
for existing issues or for issues that you come across will make a huge difference
in the way we spend time to understand the problems and thus solve them.

### Getting started with the plugin development
1. Checkout plugin repo and open the project
1. Import the project from gradle
1. Setup the right version of [Grammar-Kit](https://dl.dropboxusercontent.com/u/4294872/GrammarKit-21-08-2015.zip)
1. Setup **Ant Support**, **UI Designer**, and **Gradle** plugins
1. Run the **Go** run configuration

Please don't use class comments with information about author or date and time creation.

You can also have a look at some [useful links](#useful-links) for getting started with
plugin development on IntelliJ IDEA platform.

##### IMPORTANT: MacOS X users note

You might get the following error ```Unsupported major.minor version 52``` in the
logs or the plugin might not work at all.

Check the version of Java your IDE is running on. Since in 99.9% of the cases it will
be Java 6, this means that you compiled the plugin with a different version of Java,
for example Java 8.

To fix the error, please use Java JDK 6 to compile the plugin and everything should work.

To get the log files, you can go to ```Help | Show Log in File Manager``` and then the
log will be displayed to you.

### Delve integration

We're syncing the plugin source with the [Delve](https://github.com/derekparker/delve) debugger via https://github.com/ignatov/delve.

If you want to customize delve distribution that's used in the plugin you can use `-Ddlv.path` for setting up the path to your local version of dlv.

### Checking out and building Google Go

While we support Go from 1.0 to 1.5.1, we prefer to work with the latest release
installed from sources by following the page source installation page from here:
<http://golang.org/doc/install/source>.

This will work even with a binary installation (as a result of following the
<http://golang.org/doc/install> page).

### Checking out the plugin sources

Fork the repository using the GitHub web interface and do a clone of your fork
on the local machine.

### Building and running the unit tests

In order to build the sources, you need to download an IDEA Community version.

The steps:

* [Download](http://www.jetbrains.com/idea/) IDEA Community or Ultimate.
* Open the copy of go-lang-idea-plugin repository (`File | New | Project from Existing Sources...`) 
with it. Then import Gradle project. The default project module config should work with a recent IDEA 15 version.
* Open the Project Settings (`Command + ;` on Mac or `File -> Project Settings` on
other platforms), go to the SDKs entry, click the `+` and select JDK 1.6. 
If you use mac, you may check follow path:
```
/System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/
/Library/Java/JavaVirtualMachines/jdk1.8.0_25.jdk/Contents/
```
* Go to the Project entry and make sure that the Project SDK is set to selected
SDK.
* Wait until the source files of the SDK are indexed.

Now you can use the run configurations provided by the plugin source code to
run and play.

Going to ``` Run -> Run... ``` will provide you with the following run configurations:

+ `Go` will spawn a new IDEA with the latest version of the plugin enabled
+ `All tests` will run all the test cases available in the project. Please make
sure that all the test cases pass before committing anything (or making a pull request).
+ `Performance tests` will run all performance test cases available in the project.
+ `Build plugin` will build plugin distribution archive. All artifacts are stored in `gradle/distributions` directory.

### Profit

Now you can play, add functionality and commit or make pull requests.
That's it. Enjoy your play time.

### Useful links

For further information please see [the official plugin development page](http://confluence.jetbrains.net/display/IDEADEV/PluginDevelopment).

Also, you can read some [tips and tricks](http://tomaszdziurko.pl/2011/09/developing-plugin-intellij-idea-some-tips-and-links/).

For all development questions and proposals, you can mail to our [Open API and Plugin Development forum](https://devnet.jetbrains.com/community/idea/open_api_and_plugin_development).

Happy hacking!
