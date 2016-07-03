package main

type int struct {}

func _() {
	defer <error descr="Expression in defer must be function call">int(0)</error>
	defer <error descr="Expression in defer must be function call">(func() int)(x)</error>
}