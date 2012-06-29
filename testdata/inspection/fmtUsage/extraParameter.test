package main

import "fmt"

func TestPrintf() {
    fmt.Printf("%d\n", 5, /*begin*/3 * 2 + 1/*end.Extra parameter*/)
    fmt.Printf("", /*begin*/1.1/*end.Extra parameter*/)
}

func TestFprintf() {
    fmt.Fprintf(nil, "%d\n", 5, /*begin*/3 * 2 + 11/*end.Extra parameter*/)
    fmt.Fprintf(nil, "", /*begin*/1.1/*end.Extra parameter*/)
}

func TestScanf() {
    d := new(int)
    fmt.Scanf("%d", d, /*begin*/ds/*end.Extra parameter*/)
    fmt.Scanf("", /*begin*/ds/*end.Extra parameter*/)
}

func TestFscanf() {
    d := new(int)
    fmt.Fscanf(nil, "%d", d, /*begin*/df/*end.Extra parameter*/)
    fmt.Fscanf(nil, "", /*begin*/df/*end.Extra parameter*/)
}