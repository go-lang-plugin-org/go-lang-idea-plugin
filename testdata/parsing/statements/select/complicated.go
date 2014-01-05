package main

func Foo(x interface{}) {
    var c, c1, c2, c3 chan int
    var i1, i2 int
    select {
        case i1 = <-c1:
            print("received ", i1, " from c1\n")
        case c2 <- i2:
            print("sent ", i2, " to c2\n")
        case i3, ok := (<-c3): // same as: i3, ok := <-c3
            if ok {
                print("received ", i3, " from c3\n")
            } else {
                print("c3 is closed\n")
            }
        default:
            print("no communication\n")
    }

    for { // send random sequence of bits to c
        select {
            case c <- 0: // note: no statement, no fallthrough, no folding of cases
            case c <- 1:
        }
    }

    select {} // block forever
}
