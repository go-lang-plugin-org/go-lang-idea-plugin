package main

func _() {
    go <error descr="Expression in go must be function call">fu<caret>nc(){}</error>
}