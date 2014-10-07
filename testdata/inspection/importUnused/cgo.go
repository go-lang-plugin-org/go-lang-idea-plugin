package main

// Don't report error for package "C". It's for Cgo.
// It's valid to import "C" but not use it.
import "C"