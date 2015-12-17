package main

import (
	"fmt"
	"unsafe"
)

// struct x {
// 	int y, z;
// };
//
// int sum(struct x a) {
// 	return a.y + a.z;
// }
//
/*
typedef struct {
int x;
unsigned char y;
char *name;
} S;

S* some_C_function_call() {
	return 0;
}
*/
import "C"

type X struct{ Y, Z int32 }

var s C.struct_x
var s1 *C.struct_x

func main() {
	s2 := C.some_C_function_call()
	if (false) {
		fmt.Println(s2.x)
                fmt.Println(s2.x.y.y.wad.sd.asd.ad()) // todo[ignatov]: update test data with a valid sample
	}

	a := &X{5, 7}
	fmt.Println(s.y)
	fmt.Println(s1)
	fmt.Println(a, "->", C.sum(*((*C.struct_x)(unsafe.Pointer(a)))))

	cs := C.S{}
	fmt.Println(cs.x)
	fmt.Println(cs.y)
	fmt.Println(cs.name)
}