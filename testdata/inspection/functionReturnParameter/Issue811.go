package main

type Foo interface {
	bar() (string, error)
}

type FooBar struct{}

func (f *FooBar) bar() (string, error) {
	return "hello", nil
}

func makeFooBar() (*FooBar, error) {
	return &FooBar{}, nil
}

func doSomthing() (Foo, error) {
	return makeFooBar()
}