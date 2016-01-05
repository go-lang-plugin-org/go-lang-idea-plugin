<weak_warning descr="Package comment should be of the form 'Package commentstart ...'">// demo</weak_warning>
package commentstart

type (
	<weak_warning descr="Comment should start with 'Hello'">// help</weak_warning>
	Hello struct{}

	<weak_warning descr="Comment should start with 'Helloi'">// help</weak_warning>
	Helloi interface{}
)

<weak_warning descr="Comment should start with 'Helllo'">// help</weak_warning>
type Helllo struct{}

<weak_warning descr="Comment should start with 'Hellloi'">// help</weak_warning>
type Hellloi interface{}

const (
	<weak_warning descr="Comment should start with 'Helloc'">// help</weak_warning>
	<warning descr="Unused constant 'Helloc'">Helloc</warning> = 3
)

<weak_warning descr="Comment should start with 'Hellloc'">// help</weak_warning>
const <warning descr="Unused constant 'Hellloc'">Hellloc</warning>, <warning descr="Unused constant 'hellloc2'">hellloc2</warning> = 1, 2

var (
	<weak_warning descr="Comment should start with 'Hello1'"><weak_warning descr="Comment should start with 'Hellow1'">// help</weak_warning></weak_warning>
	Hello1, Hellow1 int
)

<weak_warning descr="Comment should start with 'Hello2'">// help</weak_warning>
var Hello2 int

<weak_warning descr="Comment should start with 'Hellllo'">// Helllo</weak_warning>
func (a Helllo) Hellllo() {
	_ = Hello1
	_ = Hellow1
	_ = Hello2
}

// Demo does things  -> correct
func Demo() {
	Demo2()
}

<weak_warning descr="Comment should start with 'Demo2'">// Demo does other things -> incorrect</weak_warning>
func Demo2() {
	Demo()
	Demo3()
	Demo4()
	Demo5()
	Demo6()
	Demo7()
	Demo8()
	Demo9()
}

<weak_warning descr="Comment should be meaningful or it should be removed">// Demo3</weak_warning>
func Demo3() {}

// A Demo4 does things  -> correct
func Demo4() {}

// An Demo5 does things  -> correct
func Demo5() {}

// The Demo6 does things  -> correct
func Demo6() {}

// Demo7 does things  -> correct
//
// Deprecated: use other thing
func Demo7() {}

// Demo8 does things  -> correct
//
// Deprecated: use other thing

func Demo8() {}

// Demo does things  -> correct
//
// Deprecated: use other thing

// Demo9 demo
func Demo9() {}
