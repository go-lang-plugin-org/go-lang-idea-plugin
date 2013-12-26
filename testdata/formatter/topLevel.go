// doc
package main
import (
"fmt"
"io"
)
const CA = 5
const (
// CB
CB = 2
)
var va = 3
var (
vb = 5
)
type T struct {
a int
}
func (t *T) Print() {
println(t.a)
}

// main function
func main() {
t := &T{}
t.Print()
println(5, Foo())
    exit : println("exit")
}

func Foo() int{
if 1 > 2||
2>4{
// comment 1
return 2
// comment 1
/* comment 2 */
}
// test
return 3
}

-----
// doc
package main

import (
	"fmt"
	"io"
)

const CA = 5
const (
	// CB
	CB = 2
)

var va = 3
var (
	vb = 5
)

type T struct {
	a int
}

func (t *T) Print() {
	println(t.a)
}

// main function
func main() {
	t := &T{}
	t.Print()
	println(5, Foo())
exit:
	println("exit")
}

func Foo() int {
	if 1 > 2 ||
		2 > 4 {
		// comment 1
		return 2
		// comment 1
		/* comment 2 */
	}
	// test
	return 3
}
