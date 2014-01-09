package main

type (
	name  int     //
	name1 float32 //

	structType    struct{} //
	interfaceType interface {
	}

	arrayType [10]string //
	sliceType []int

	pointerType              *name               //
	mapType                  map[int]pointerType //
	longerTypeToAlignPointer int
)
