package main

import "test"

func f() {
    r := test.Method()
    x := r./*ref*/ActualMethod()
}
