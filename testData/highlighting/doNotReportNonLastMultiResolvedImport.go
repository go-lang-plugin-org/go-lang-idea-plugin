package main

import "to_import/unique"
import "to_import/<error descr="Resolved to several targets">shared</error>"

func main() {
	unique.Foo()
}