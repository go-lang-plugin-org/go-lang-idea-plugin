+++
title = "Home"
date = "2014-04-09"
sidemenu = "true"
description = "Home page"
+++

{{% fluid_img "/images/main.png" %}}

# Install plugin

Use the `File | Settings | Plugins | Browse Repositories` dialog to search and install Go plugin.

# Pre-release builds

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

1. Use <a href="https://www.jetbrains.com/idea/help/managing-enterprise-plugin-repositories.html" target="_blank">the instructions</a>
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