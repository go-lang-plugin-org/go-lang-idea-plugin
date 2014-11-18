package bugs

import (
	"net"
)

type SomeOtherFoo struct {
	net.AddrError
}

func (s SomeOtherFoo) GetThing() string {
	return ""
}

type Foo struct {
	SomeOtherFoo
}

func test() {
	x := Foo{}
	x.<caret>
}
/**---
AddrError
Addr
GetThing
SomeOtherFoo
Err
Error
Temporary
Timeout

