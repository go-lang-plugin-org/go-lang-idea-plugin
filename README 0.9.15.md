Release notes for 0.9.15
===

Thank you for installing the latest version of the golang plugin for IDEA.

While there are many new things in this version there are also a couple of bugs.

Please help us finding them by submitting a proper bug report to the [repository](https://github.com/mtoader/google-go-lang-idea-plugin/issues?state=open)
or fixing the existing ones.

This plugin has been tested against IDEA 13.0.1 and PHPStorm 7.0.1 with
Go 1.2 installed from [golang.org](http://golang.org). While other setups
may work, there are reported issues with Homebrew installation on Mac OS X.

Thank you.

Known issues
===

- Projects created in the first directory from ``` GOPATH ``` will not trigger
 the autocompletion properly.
- In IDEA please use the project type from the ``` GO ``` section rather that from ``` Static Web ```.
- MacOS X: Homebrew installation doesn't add the proper ``` GOROOT ``` and ``` GOPATH ```
 variables to the environment and you need to add them manually.
- MacOS X: Go SDK should be pointed to the ``` libexec ``` directory rather
 that the main go directory.
- Non-IDEA IDEs: while we've did our best to test the plugin on other IDEs that
 IDEA, there might be things that don't work on your current IDE. Submit a bug
 report for it
- Can't run various processes (like gofmt): sometimes IDEA doesn't seem to be
 aware of the environment variables, please check the ticket here: [IDEA-118483](http://youtrack.jetbrains.com/issue/IDEA-118483)
