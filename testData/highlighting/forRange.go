package main

import (
    "fmt"
    "time"
)

func main() {
    var tick = 0
    for range time.NewTicker(1 * time.Second).C {
        fmt.Println("Tick")
        tick++
        if tick > 3 {
            break
        }
    }
    
    for _, _ = range "abc"  {
    }
    for _ = range "abc"  {
    }
}

type d_type [32]uintptr

type Bits struct {
	a []uint64
	b [3]uint64
	c [3+3]uint64
	d d_type
}

func _(a *Bits) {
	for i, x := range <error descr="Cannot range over data (type *[]uint64)">&a.a</error> {
		fmt.Println(i)
		fmt.Println(x)
	}
	for i, x := range  &a.b {
		fmt.Println(i)
		fmt.Println(x)
	}
	for i, x := range &a.c {
		fmt.Println(i)
		fmt.Println(x)
	}
	for i, x := range &a.d {
		fmt.Println(i)
		fmt.Println(x)
	}

	for i, x := range <error descr="Cannot range over data (type int, int)">a2()</error> {
        		fmt.Println(i)
        		fmt.Println(x)
        	}
}

func a2() (value int, error int){
    value, error = 2, 3
    return
}