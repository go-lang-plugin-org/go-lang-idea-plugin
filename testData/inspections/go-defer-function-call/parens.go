package main

func _() {
    defer <error descr="Expression in defer must be function call"><error descr="Expression in defer must not be parenthesized">(<error descr="fu<caret>nc(){}() used as value">func(){}()</error>)</error></error>
}