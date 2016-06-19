package main

type int struct {}

func _() {
	defer <error descr="defer requires function call, not conversion">int(0)</error>
	defer <error descr="Expression in defer must be function call">(func() int)(x)</error>
}