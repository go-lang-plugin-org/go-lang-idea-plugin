package main

import (
	"fmt"
)

func main() {

}

type Foo struct {
	f1 int
}

func (f *Foo) () {
	fmt.Printf("The value is %v.\n", f.<caret>)
}
/**---
const
func
import
type
var
