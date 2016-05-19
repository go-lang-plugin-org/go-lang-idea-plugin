package main

var a [10]byte

func len(o [10]byte) int {
	return 1;
}

func main (){
  _ = a[0: len(a)]
}