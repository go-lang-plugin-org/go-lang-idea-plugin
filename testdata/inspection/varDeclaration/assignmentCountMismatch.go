package main

import (
    "crypto/md5"
    "hash"
)

var (
    a = 2
    /*begin*/b, c = 3/*end.Assignment count mismatch: 2 = 1*/
    /*begin*/d, e = 1, 2, 3/*end.Assignment count mismatch: 2 = 3*/
)

var f int

func typeAssertion() {
    h := md5.New()
    i, ok := b.(hash.Hash)
    println(i, ok)
}

func TwoResults()(int, int) {
    return 1, 2
}

func main() {
    /*begin*/b := 5, 6/*end.Assignment count mismatch: 1 = 2*/
    c, d := TwoResults()
}