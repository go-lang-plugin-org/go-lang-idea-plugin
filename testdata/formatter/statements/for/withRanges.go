package main

func f() {for i,_:=range testdata.a{
// testdata.a is never evaluated; len(testdata.a) is constant
          	// i ranges from 0 to 6
          	f(i) }


for a = range x{}
          }

