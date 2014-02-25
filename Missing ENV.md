Fix missing environment paths
===

Sometimes IDEA won't properly detect the environment paths properly.
This can happen because of many reasons, especially on Mac OS X and Linux,
Windows is not reported to have similar issues.

Usually, the simplest way to fix this is to launch IDEA from the command line.
If that doesn't work then read on:

- Linux
--

You need to set ``` GOROOT ``` and ``` GOPATH ``` in ``` /etc/environment ```
to match the current values from your ``` .profile ```, ``` .bashrc ``` or ``` .zshrc ```
After that, you need to restart your system for the changes to take effect.

If you are using Gnome and these variables are stored in .profile, you can change the ```.desktop``` file that launches IDEA and edit the Exect line to:
```
Exec=sh -c "/usr/local/idea/bin/idea.sh" %f
```
changing the path to ```idea.sh``` to the appropriate path of course.


- Mac OS X
--

If you are launching the IDE the normal application launcher and you get the error message
about missing paths then you need to add ```GOPATH``` and ```GOROOT``` in ```/etc/launchd.conf``` to match the
values from your ```.bashrc``` or ```.zshrc```.

Please note that ```/etc/launchd.conf``` does not exist by default, so you will need to create it with the following:

```bash
setenv GOROOT /usr/local/go
setenv GOPATH /Users/yourname/go
```

It's possible to apply these changes [without a restart](http://stackoverflow.com/a/3756686/15677). 

You can view more [details here](https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/318#issuecomment-31303939).

You can get more details about launchd.conf in its respective [launchd.conf(5) Mac OS X Manual Page](https://developer.apple.com/library/mac/documentation/Darwin/Reference/ManPages/man5/launchd.conf.5.html).

NOTE
===

Don't forget to keep the values in sync.
