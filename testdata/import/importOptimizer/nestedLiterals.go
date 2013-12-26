package main

import (
    "a"
    "b"
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
    "b"
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
