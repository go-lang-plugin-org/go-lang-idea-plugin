package main

import "to_import/unique"
import _ "to_import/sha<caret>red"

func main() {
	unique.Foo()
}