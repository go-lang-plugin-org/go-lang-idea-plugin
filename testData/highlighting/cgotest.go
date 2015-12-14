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
*/
import "C"

type X struct{ Y, Z int32 }

var s C.struct_x
var s1 *C.struct_x

func main() {
	a := &X{5, 7}
	fmt.Println(s.y)
	fmt.Println(s1)
	fmt.Println(a, "->", C.sum(*((*C.struct_x)(unsafe.Pointer(a)))))

	cs := C.S{}
	fmt.Println(cs.x)
	fmt.Println(cs.y)
	fmt.Println(cs.name)
}