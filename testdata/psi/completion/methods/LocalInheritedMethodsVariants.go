package main

type GrandParent int

type Parent struct {
	GrandParent
}

type Derived struct {
	Parent
}

func (t GrandParent) methodGrandParentStandard() {
}

func (t *GrandParent) methodGrandParentByPointer() {
}

func (t Parent) methodParentStandard() {
}

func (t *Parent) methodParentByPointer() {
}

func (t Derived) methodDerivedStandard() {
}

func (t *Derived) methodDerivedByPointer() {
}

func main() {
	var x Derived
	x.method<caret>
}
/**---
methodDerivedByPointer
methodDerivedStandard
methodGrandParentByPointer
methodGrandParentStandard
methodParentByPointer
methodParentStandard
