package main

type State interface {
	Write(b []byte) (ret int, err error)
	Width() (wid int, ok bool)
	Precision() (prec int, ok bool)
	Flag(c int) bool
}

type Formatter interface {
	Format(f State, c rune)
}

type buffer []byte

func (b *buffer) Write(p []byte) (n int, err error) {
	*b = append(*b, p...)
	return len(p), nil
}

func main() {
}