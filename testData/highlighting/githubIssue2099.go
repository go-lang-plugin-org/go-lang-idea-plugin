package main

// Tests https://github.com/go-lang-plugin-org/go-lang-idea-plugin/issues/2099.

type Interf interface {
	Hello()
}

type demo interface {
	Interf
	Interf()
}
