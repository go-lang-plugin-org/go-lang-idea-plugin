package main

import (
    a "p1"
    b "p2"
)

var a = []struct {
    v interface {}
}{
    {a.Now()},
}

func main() {
    go func() {
        b.Func()
    }()
}

-----
package main

import (
    a "p1"
    b "p2"
)

var a = []struct {
    v interface {}
}{
    {a.Now()},
}

func main() {
    go func() {
        b.Func()
    }()
}
