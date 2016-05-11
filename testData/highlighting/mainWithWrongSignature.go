package main

func <error descr="Duplicate function name">main</error>() {
	println("Hello")
}

func <error descr="Duplicate function name">main</error><error descr="main function must have no arguments and no return values">(int)</error> {

}

func <error descr="Duplicate function name">main</error>() <error descr="main function must have no arguments and no return values">int</error> {

<error descr="Missing return at end of function">}</error>

func <error descr="Duplicate function name">main</error><error descr="main function must have no arguments and no return values">(int)</error> <error descr="main function must have no arguments and no return values">int</error> {

<error descr="Missing return at end of function">}</error>

func <error descr="Duplicate function name">main</error>() () {}