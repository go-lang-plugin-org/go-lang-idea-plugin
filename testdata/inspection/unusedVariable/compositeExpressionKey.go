package main

type error interface {
	Error() string
}

type scanError struct {
	err error
}

func (s *ss) error(err error) {
	panic(scanError{err})
}
