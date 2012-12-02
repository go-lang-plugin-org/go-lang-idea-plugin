# Google Go language plugin for IntelliJ Idea

Google go language plugin is an attempt to build an outstanding IDE for
[Google Go language](http://golang.org) using Intellij IDEA.

+ Project pages on github:
    + <http://github.com/mtoader/google-go-lang-idea-plugin>
    + <http://wiki.github.com/mtoader/google-go-lang-idea-plugin/> (wiki)
+ Google Go language page:
    + <http://golang.org/>
+ Intellij IDEA homepage:
    + <http://www.jetbrains.com/idea/>
    + <http://plugins.intellij.net/plugin/?idea&id=5047> (plugin page at the intellij plugin repository).

## What it does

* Basic language parsing and highlighting
* Code folding
* Brace matching
* Comment/Uncomment (Single/Multiple line) support
* Go SDK (work with the latest release and on windows)
* File type icon
* Go application file and library generation.
* Auto completion of sdk package names and/or local application packages.
* Compilation of the go applications (supported semantics are similar to those of gobuild)
* Go To definition (for types) works across files and Go SDK
* Code formatting - experimental (disabled)
* Type name completion
* ColorsAndSettings page with a new color scheme
* Automatically add new line at end of file
* Force UTF-8 encoding for go files
* Go module type
* Go SDK indexing mode

## How to use it

* Download and install Intellij IDEA (Ultimate or Community edition).
* Open the Plugins installation page: File -> Settings -> Plugins -> Available Plugins
* Search for google go
* Right click on the proper plugin and install
* Download [latest release of the Google Go language](http://golang.org/doc/install.html).
* Build and install it.
* Open IDEA and create an empty Java project.
* Go to File -> Project Structure and select SDKs entry in the left column of the new window
* Add a new Google Go SDK by clicking the plus sign an choosing the appropriate SDK type.
* After the SDK is defined go to the Modules entry and add a new google facet to your default module.
Select the proper sdk for the module.

Now you are ready to play with golang.

## Tutorials

* [Philip Andrew's Intellij Go Plugin tutorial](http://www.philipandrew.com/?p=46)

## Bugs

If you found a bug, please report it at the Google Go plugin project's tracker
on GitHub: <http://github.com/mtoader/google-go-lang-idea-plugin/issues>

## Authors

+ [Mihai Claudiu Toader (mtoader@gmail.com)](http://redeul.ro)
    + Language parsing and PSI building
    + Sdk indexing
    + Reference Resolving & Completions

+ <https://github.com/leojay>
    + folding improvement
    + refactorings
        + introduce variable
    + inspections
        + unused imports

+ [Alexandre Normand](https://github.com/alexandre-normand)
    + gomake integration

+ [Jhonny](https://github.com/khronnuz)
    +  gofmt integration
    +  simple Structure View for Go files.

## Contributing

If you want to contribute to this effort you should read the following page:
[how to contribute](https://github.com/mtoader/google-go-lang-idea-plugin/blob/master/contributing.md).