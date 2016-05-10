package main

func _() {
    defer <error descr="Expression in defer must be function call">(f<caret>unc(){})</error>
}