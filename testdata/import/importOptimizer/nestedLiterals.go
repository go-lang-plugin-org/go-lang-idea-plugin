package main

import (
    "a"
    b "b"
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
    "a"
    b "b"
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
