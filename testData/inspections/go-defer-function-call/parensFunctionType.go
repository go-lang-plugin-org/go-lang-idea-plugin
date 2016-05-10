package main

func _() {
    defer <error descr="Expression in defer must be function call"><error descr="Expression in defer must not be parenthesized">(fu<caret>nc(){})</error></error>
}