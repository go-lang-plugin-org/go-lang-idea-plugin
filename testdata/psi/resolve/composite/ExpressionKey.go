package main

type error interface {
	Error() string
}

type scanError struct {
	err error
}

func (s *ss) error(/*def*/err error) {
	panic(scanError{/*ref*/err})
}
