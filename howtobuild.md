Build the plugin on various platforms
============

Ubuntu
===

(as provided by [LarryBattle](https://github.com/LarryBattle) [here](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/248#issuecomment-29878589))

The plugin works now with IDEA 13 after being built with the latest code from this repository.
Here's the build process for Ubuntu 13 for those wondering.

``` bash
# Make sure to install ant and java jdk
sudo apt-get install ant openjdk-6-jdk
git clone https://github.com/go-lang-plugin-org/go-lang-idea-plugin.git
cd google-go-lang-idea-plugin
gedit build-package.xml
```

Change the value of location to the installation directory of IDEA ide.
Example:

```
<property name="idea.community.build" location="${user.home}/Downloads/idea-IC-133.193/" />
Build the project with ant.

```bash
ant -f build-package.xml
```

After a successful build, ```dist/ro.redeul.google.go.jar``` should be created.
Lastly, import the jar as plugin (File > Settings > Plugins > "Install plugin from disk") and restart the IDEA ide.


Mac OS X
===

(as provided [here](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/306#issuecomment-30012659))

Should work as above:

- download & install IDEA 13 Community
- open IDEA and use the built it: 'New project to Version Control'
- get the path of your fork (or this repository) and execute it
- when in IDEA, go to 'preferences' (or how it's called) to add an IDEA JDK
- you are prompted first to add the JDK, do so
- now add IDEA JDK by pointing IDEA to itself

