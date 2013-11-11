package main

const (
    A = 5    // this
    BCD = 3     // is
    E = 456       // a test

    LONG_CONST = 4 // another
    F = 345678 // comment
)

func main() {
    a,b:=5,6
    a,b=4,7

    if c:=a+b;c<3 {
    }

    for i:=1;;i++ {
    }
}
-----
package main

const (
	A   = 5   // this
	BCD = 3   // is
	E   = 456 // a test

	LONG_CONST = 4      // another
	F          = 345678 // comment
)

func main() {
	a, b := 5, 6
	a, b = 4, 7

	if c := a + b; c < 3 {
	}

	for i := 1; ; i++ {
	}
}
