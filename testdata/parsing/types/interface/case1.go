package main
type ITest interface {
    Writer
    Sum(values []int) []byte
}
