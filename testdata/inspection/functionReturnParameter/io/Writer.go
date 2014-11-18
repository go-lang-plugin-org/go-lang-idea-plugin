package io

type Writer interface {
	Write(p []byte) (n int, err error)
}
