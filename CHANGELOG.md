<h3>1.0.0 changes:</h3>
<ul>
    <li>Fully reworked internals. Released as 0.9.X (any build > 0.9.15.3)</li>
</ul>    
<h3>0.9.16 changes:</h3>
<ul>
    <li>[feature] Support for IntelliJ IDEA 14 Cassiopeia and removed IntelliJ IDEA 13 support</li>
    <li>[feature] Support for go fmt and goimports on file save</li>
    <li>[feature] Removed the need to set environment variables for the plugin to work correctly</li>
    <li>[feature] Added debugging support via GDB</li>
    <li>[feature] Added support for Go on Google AppEngine for non-IDEA IDEs</li>
    <li>[feature] Intention: For range over expressions</li>
    <li>[feature] Intention: Generate if block over boolean expressions</li>
    <li>[feature] Inspection: Check type of index on index expressions</li>
    <li>[feature] Intention: Generate if block over expressions which return errors</li>
    <li>[enhancement] Inspection: Update "functionReturnCountInspection" to check the type of returning expressions</li>
    <li>[bugfix] Improved support for Go on Google AppEngine</li>
    <li>[bugfix] Change the function return parameters based on returning expressions</li>
    <li>[bugfix] Covert or Assert the type of the expression to the expected type in function calls and returns</li>
</ul>
<h3>0.9.15.3 changes:</h3>
<ul>
    <li>[feature] Added support for running all tests in a directory</li>
    <li>[bugfix] Fix the creation of projects in IntelliJ Community again.</li>
 </ul>
<h3>0.9.15.1 changes:</h3>
<ul>
    <li>[bugfix] Fix auto-complete for third-party packages</li>
</ul>
<h3>0.9.15 changes:</h3>
<ul>
    <li>[feature] Added IntelliJ IDEA 13 (Cardea) support</li>
    <li>[feature] Dropped IntelliJ IDEA 12 (Leda) support</li>
    <li>[feature] Golang 1.2 syntax support</li>
    <li>[feature] Darcula theme support</li>
    <li>[feature] New run / build system available</li>
    <li>[feature] Added a new inspection/fix which will generate a closure function</li>
    <li>[feature] Function inspection/fix will generate the arguments when specified</li>
    <li>[feature] Added intention to run "go get" to import a package</li>
    <li>[feature] Added intention to run "go get -u" to update a package</li>
    <li>[feature] go fmt integration via Tools -> Go menu options (default CTRL+ALT+SHIFT+F for file and CTRL+ALT+SHIFT+G for project)</li>
    <li>[feature] go vet integration</li>
    <li>[bugfix] Dropped support for old build systems</li>
    <li>[bugfix] Fixed live templates and compilation</li>
    <li>[bugfix] Fixed Go App Engine support</li>
    <li>[bugfix] Other fixes and improvements</li>
</ul>

<h3>0.9.14 changes:</h3>
<ul>
    <li>[feature] Basic support for completion of the whole method set for an interface.</li>
    <li>[feature] Handle builtin function calls when doing type inference</li>
    <li>[feature] make/len/cap/true/false/nil are also highlighted as keywords.</li>
    <li>[feature] Add the builtins to the list of methods that we want to be able to autocomplete.</li>
    <li>[bugfix] Make sure that by default go looks good on both dark and clear themes.</li>
    <li>[bugfix] Fixed some formatting edge cases.</li>
    <li>[bugfix] Resolving to variables declared in a select communication clause works.</li>
    <li>[bugfix] Don't show methods for local types twice in strucure view.</li>
    <li>[bugfix] module relative imports are working properly inside the IDE.</li>
    <li>[bugfix] resolve should work inside select statement clauses.</li>
    <li>[bugfix] resolve should target the local package names first.</li>
    <li>[bugfix] Properly detect a go sdk installed via standard deb package in ubuntu.</li>
</ul>

<h3>0.9.13 changes:</h3>
<ul>
    <li>[bugfix] Guard against a NPE when looking at old Go App Engine SDK's.</li>
    <li>[bugfix] Properly scan for a sdk in windows with go 1.0.3.</li>
    <li>[bugfix] Properly scan for a sdk in osx with go installed from golang.org / brew / compiled by hand.</li>
</ul>

<h3>0.9.12 changes:</h3>
<ul>
    <li>[bugfix] IntelliJ Idea 12 compatibility fixes. Only available for Intellij 12 since 0.9.11</li>
    <li>[bugfix] If the current project has go modules it will disable the external compiler feature of IDEA.</li>
</ul>

<h3>0.9.11 changes:</h3>
<ul>
    <li>[bugfix] NPE in certain cases when creating a test configuration.</li>
    <li>[bugfix] Fix wrong parsing of method/function parameter declarations.</li>
    <li>[bugfix] Don't report error for package "C" usage.</li>
    <li>[enhancement] List all promoted fields in auto completion list.</li>
    <li>[enhancement] In structure view, put all struct methods on the top of struct fields.</li>
    <li>[enhancement] Add auto completion for anonymous fields of structs.</li>
    <li>[enhancement] Support find usage of types, struct fields, function parameters, function results, method receiver, functions and methods.</li>
    <li>[enhancement] Add auto completion for variables declared in short var declaration and normal var declaration without explicit type.</li>
    <li>[enhancement] List all imported package names in basic completion, and all imported and unimported package names in second basic completion or no auto completion items are available.</li>
    <li>[feature] Add graphical test runner panel to show results of tests and benchmarks.</li>
    <li>[enhancement] Properly propagate the type of vars declared in a for range statement.</li>
</ul>

<h3>0.9.10 changes:</h3>
<ul>
    <li>[bugfix] Tweaked the default package name for imports.</li>
    <li>[bugfix] Properly parse anonymous functiona parameters having a qualified type</li>
    <li>[bugfix] Add a space after ',' inside calls when reformatting</li>
    <li>[bugfix] Make the InvalidPackageName inspection better.</li>
    <li>[enhancement] The Test Run Configuration now is module scoped.</li>
</ul>

<h3>0.9.9 changes:</h3>
<ul>
    <li>[bugfix] Properly transfer the types via a channel expression.</li>
    <li>[bugfix] Don't highlight functional variables as unused.</li>
    <li>[enhancement] Show the file methods inside the structure view.</li>
    <li>[enhancement] Use the complete method set for inherited structs.</li>
    <li>[enhancement] Automatic management of imports setting should be duplicated into the Go Settings dialog.</li>
    <li>[enhancement] Force GOROOT to the go folder when detecting SDK.</li>
    <li>[feature] Inspection/quickfixes for package names in files with respect to the expected go tools compatible structure.</li>
    <li>[feature] Generate go test run configuration by right clicking inside a test file (with proper handling of clicks inside a benchmark / test method).</li>
</ul>

<h3>0.9.6 changes:</h3>
<ul>
    <li>[enhancement] Allow resolving of composite literal keys to match constants or vars.</li>
    <li>[enhancement] Don't mark function literals as unresolved.</li>
    <li>[features] Use tabs instead of spaces to layout the go source code.</li>
    <li>[bugfix] Always treat the lasts part of the path (when used as package name) as lowercase.</li>
    <li>[bugfix] Fix an infinite loop when parsing an incomplete struct type.</li>
    <li>[bugfix] Don't barf when a go module is created without source folders.</li>
</ul>

<h3>0.9.5 changes:</h3>
<ul>
    <li>[enhancement] Automatic upgrade of Go SDK configuration when needed.</li>
    <li>[bugfixes] Enhanced resolution of methods.</li>
    <li>[bugfix] NPE fixes when handling invalid import declarations (@catalinc).</li>
    <li>[bugfix] Fix running of applications on windows (@catalinc).</li>
    <li>[bugfix] Fix running of applications in child folders.</li>
</ul>

<h3>0.9.4 changes:</h3>
<ul>
    <li>[bugfix] Fix typename resolution for qualified types.</li>
</ul>

<h3>0.9.3 changes:</h3>
<ul>
    <li>[feature] Added a warning about corrupt SDK inside a project.</li>
    <li>[feature] Fix the way we read the SDK type version.</li>
    <li>[bugfix] Updated the SDK detection to work properly when there are cross compilation libraries..</li>
    <li>[bugfix] Updated the compiler to use the proper tools and not call them directly from pkg/<os>_<version>.</li>
</ul>

<h3>0.9.0 changes:</h3>
<ul>
    <li>[feature] Refactoring: Introduce variable.</li>
    <li>[feature] Refactoring: Introduce constant.</li>
    <li>[feature] Refactoring: Inline local variable.</li>
    <li>[feature] Refactoring: Inplace variable/function rename.</li>
    <li>[feature] Optimize import on the fly.</li>
    <li>[feature] Inspection: Unused imports.</li>
    <li>[feature] Inspection: Unused variables.</li>
    <li>[feature] Inspection: Unused constants.</li>
    <li>[feature] Inspection: Unused parameters.</li>
    <li>[feature] Inspection: fmt parameter inspection.</li>
    <li>[feature] Inspection: Function call inspection.</li>
    <li>[feature] Inspection: Import inspection.</li>
    <li>[feature] Inspection: Unresolved symbol inspection.</li>
    <li>[feature] Intention: Invert if condition.</li>
    <li>[feature] Intention: Merge nested 'if's.</li>
    <li>[feature] Intention: Split into 2 'if's.</li>
    <li>[feature] Intention: Convert switch statement to if chain.</li>
    <li>[feature] Intention: Convert between interpreted string and raw string.</li>
    <li>[feature] Intention: Add/Remove parentheses.</li>
    <li>[feature] Intention: Move simple statement out.</li>
    <li>[feature] Documentation for functions, methods, variable, constants and types.</li>
    <li>[feature] Function parameter information.</li>
    <li>[feature] Ctrl+Shift+T to jump between source file and test file.</li>
    <li>[feature] Navigation from stack trace in output panel.</li>
    <li>[feature] Ctrl+Shift+Enter to add curly brackets for "func", "if" and "for" statements.</li>
    <li>[enhancement] code folding improvement.</li>
    <li>[enhancement] Code formatter.</li>
    <li>[enhancement] File structure view.</li>
    <li>[enhancement] TODO's are shown in todo panel.</li>
    <li>[enhancement] Live templates: "sout", "souf", "soutm" and "soutp".</li>
    <li>[feature] Inspection: Detect non constant expression used in constant declarations</li>
    <li>[bugfix] Force the .go file to map to the proper file type.</li>
    <li>[bugfixes] Various parsing inconsistencies</li>
    <li>[enhancement] completion/resolution of struct members (expression/composite literals).</li>
    <li>[enhancement] completion/resolution improvements.</li>
</ul>

<h3>0.5.1 changes:</h3>
<ul>
    <li>[feature] Support for Go 1 release</li>
</ul>

<h3>0.5.0 changes:</h3>
<ul>
    <li>[feature] Go App engine sdk type</li>
    <li>[feature] Go App engine credentials safe storage</li>
    <li>[feature] Added go application wizard</li>
    <li>[bugfix] Pass the arguments on a run config as separate arguments</li>
    <li>[bugfix] Exception while saving the document (nested modification exception)</li>
    <li>[bugfix] Wrong highlighting of a map type with a function inner type</li>
    <li>[bugfix] Idea 11 compatibility</li>
</ul>

<h3>0.4.2 changes:</h3>
<ul>
    <li>[enhancement] Enable control for the operators colors when doing syntax highlighting.</li>
</ul>

<h3>0.4.1 changes:</h3>
<ul>
    <li>[bugfix] Revert to using Icons instead of PlatformIcons.</li>
</ul>

<h3>0.4.0 changes:</h3>
<ul>
    <li>[feature] Detection of bundled Go Sdk packages (useful when deployed as GoIde)</li>
    <li>[bugfix] NullPointerException while highlighting selector expression with non resolving context (Issue #34).</li>
    <li>[feature] Partial error highlighting of non existent imported packages.</li>
    <li>[feature] Run configuration creator (right click on a program file and you can run it). </li>
    <li>[feature] Run configuration validation. </li>
</ul>

<h3>0.3.18 changes:</h3>
<ul>
    <li>[bugfix] Fixed parsing error when the last function element is ...</li>
    <li>[bugfix] Fixed multiline comment parsing in certain cases.</li>
    <li>[bugfix] Fixed regression when parsing big const declaration with comments.</li>
</ul>

<h3>0.3.17 changes:</h3>
<ul>
    <li>[feature] Improved completion (use the top level var and const declarations as completion variants).</li>
</ul>

<h3>0.3.16 changes:</h3>
<ul>
    <li>[feature] Go aware imports optimizer (experimental).</li>
</ul>

<h3>0.3.15 changes:</h3>
<ul>
    <li>[feature] Support GO sdk as packaged by the gophers/go PPA on Ubuntu.</li>
</ul>

<h3>0.3.14 changes:</h3>
<ul>
    <li>[bugfix] Fix the broken run configuration on MacOS. https://github.com/alexandre-normand.</li>
</ul>

<h3>0.3.13 changes:</h3>
<ul>
    <li>[feature] Added simple Structure View for Go files. Thanks https://github.com/khronnuz.</li>
</ul>

<h3>0.3.12 changes:</h3>
<ul>
    <li>[feature] Using gofmt as formatter when formatting a file (Ctrl + Alt + L). Thanks https://github.com/khronnuz.</li>
</ul>

<h3>0.3.11 changes:</h3>
<ul>
    <li>Bump the stub index version.</li>
</ul>

<h3>0.3.10 changes:</h3>
<ul>
    <li>[bugfix] Go To function definitions was broken by the variable completion changes. Fixed.</li>
    <li>[bugfix] Invalid error highlight when parsing a CompositeLiteral ending with a comma.</li>
</ul>

<h3>0.3.9 changes:</h3>
<ul>
    <li>[feature] Added a setting to allow disabling of the variable completion.</li>
    <li>[bugfix] fix ArrayIndexOutOfBoundException when running on some wild code.</li>
</ul>

<h3>0.3.7 changes:</h3>
<ul>
    <li>[feature] experimental Makefile based build system (bring your on Makefile's). Configured in project settings. Thanks to https://github.com/alexandre-normand</li>
    <li>[feature] some more completion options on variables (work in progress)</li>
    <li>[bugfix] invalid framework import causing incompatibility with idea 10.5+ </li>
</ul>

<h3>0.3.6 changes:</h3>
<ul>
    <li>[feature] GoTo class (go types) implementation</li>
</ul>

<h3>0.3.5 changes:</h3>
<ul>
    <li>[bugfix] Go to error on compilation error. Thanks alexandre :)</li>
</ul>

<h3>0.3.4 changes:</h3>
<ul>
    <li>[feature] Properly resolve not public types from the current package.</li>
    <li>[feature] Function names completion (from current package and imported packages) where an identifier is possible.</li>
</ul>

<h3>0.3.3 changes:</h3>
<ul>
    <li>[bugfix] Minor bug with types completion</li>
</ul>

<h3>0.3.2 changes:</h3>
<ul>
    <li>[feature] Fixed and upgraded go types resolution and completion</li>
</ul>

<h3>0.3.1 changes:</h3>
<ul>
    <li>[feature] Removed the version limitation</li>
</ul>

<h3>0.3.0 changes:</h3>
<ul>
    <li>[bugfix] Fixed to run under Idea 10 version</li>
    <li>[feature] Adding indexing of the go SDK (right now only for package names. More to follow</li>
    <li>[feature] Added go module type / removed go facet.</li>
    <li>[feature] Disabled go formatter (in the process of rebuilding).</li>
</ul>

<h3>0.2.9 changes:</h3>
<ul>
    <li>[bugfix] wrong indent when hitting enter in top level elements</li>
    <li>[bugfix] parsing infinite loop with the latest go release.</li>
</ul>

<h3>0.2.8 features:</h3>
<ul>
    <li>[bugfix] wrong indent when hitting enter</li>
</ul>

<h3>0.2.7 features:</h3>
<ul>
    <li>[feature] Type name completion.</li>
    <li>[feature] Code formatting.</li>
    <li>[bugfix] Some parsing errors.</li>
</ul>

<h3>0.2.6 features:</h3>
<ul>
    <li>[bugfix] Fix compilation for windows.</li>
</ul>

<h3>0.2.5 features:</h3>
<ul>
    <li>[bugfix] Force encoding for go files to UTF-8 all the time</li>
</ul>

<h3>0.2.4 features:</h3>
<ul>
    <li>[bugfix] Make sure the latest go release is detected as a Go Sdk</li>
</ul>

<h3>0.2.3 features:</h3>
<ul>
    <li>[feature] Go To definition (for types) works across files and Go SDK</li>
    <li>[feature] ColorsAndSettings page with a new color scheme</li>
    <li>[feature] Automatically add new line at end of file</li>
</ul>

<h3>0.2.2 features:</h3>
<ul>
    <li>[feature] Go To definition (for types defined inside the same file)</li>
    <li>[bugfixes] Fixed the plugin to work with idea 9.0.3.</li>
</ul>

<h3>0.2.1 features:</h3>
<ul>
    <li>[feature] Update Go SDK (to work with the latest release and on windows)</li>
    <li>[feature] Compilation of the go applications (supported semantics are similar to those of gobuild)</li>
    <li>[feature] Go application file and library generation.</li>
    <li>[feature] Auto completion of sdk package names and/or local application packages.</li>
    <li>[bugfixes] Some parsing issues with comments.</li>
</ul>

<h3>0.1.1 features:</h3>
<ul>
    <li>[feature] Go Sdk type (with auto configuration)</li>
    <li>[feature] Icons</li>
</ul>

<h3>0.1.0 features:</h3>
<ul>
    <li>[feature] basic language parsing and highlighting</li>
    <li>[feature] Code folding</li>
    <li>[feature] Brace matching</li>
    <li>[feature] Comment/Uncomment (Single/Multiple line) support</li>
</ul>
