package main
type T interface {
	Read(b Buffer) bool
	Write(b Buffer) bool
	Close()
}
