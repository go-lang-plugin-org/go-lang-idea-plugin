package main

func foo() {
    i := 1
    for (i) {return 0}
    if (i) {return <error>j</error>}
}
