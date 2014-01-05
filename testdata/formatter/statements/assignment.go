package main

func main() {
i, x[i] = 1, 2  // set i = 1, x[0] = 2

i = 0
// asdfa
//  adsfa
x[i], i = 2, 1  // set x[0] = 2, i = 1
x[0],x[0] = 1,2 // set x[0] = 1, then x[0] = 2 (so x[0] == 2 at end)
x[1],x[3] = 4,5      // set x[1] = 4, then panic setting x[3] = 5.

x[2], p.x = 6, 7  // set x[2] = 6, then panic setting p.x = 7

}