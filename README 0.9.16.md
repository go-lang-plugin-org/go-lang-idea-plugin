Release notes for 0.9.16
===

Thank you for installing the latest version of the golang plugin for IDEA.

This version has been almost complete reworked from ground up to fix many
of the previous version issues with resolving and other problems.

We've also removed the dependency on system variables and we are using
a IntelliJ IDEA based approach.

Please help us finding them by submitting a proper bug report to the [repository](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues?state=open)
or fixing the existing ones.

This plugin has been tested against IDEA 14 and PHPStorm 8.1 with
Go 1.3.3 installed from [golang.org](http://golang.org). While other setups
may work, there are reported issues with Homebrew installation on Mac OS X. For
Mac OS X, if you installed Go SDK via Homebrew, point the SDK root directory
to ``` libexec ``` not to ``` 1.3 ```.

Thank you.

Report an issue
===

Please use the ``` Tools -> Go Tools -> go plugin Debug internals ``` to get the internal
state of the plugin and paste that into the ticket. Also please try to include
a way to reproduce the issue and the full code snipet (if possible) that triggers
the issue. If not, at least a minimum sample of code that can reproduce the issue
is welcomed.

NOTE: The above output may contain private or confidential information, be sure
to filter it first.

