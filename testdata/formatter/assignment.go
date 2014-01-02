package main

func main() {
    a,b:=5,6
    a,b=4,7

// go fmt
x:=5*5
x=5*5

    if c:=a+b;c<3 {
    }

    for i:=1;;i++ {
    }
}

-----
package main

func main() {
	a, b := 5, 6
	a, b = 4, 7

	// go fmt
	x := 5 * 5
	x = 5 * 5

	if c := a + b; c < 3 {
	}

	for i := 1; ; i++ {
	}
}
