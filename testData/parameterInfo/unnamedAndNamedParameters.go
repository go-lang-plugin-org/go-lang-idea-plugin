package foo

type Registry interface {
  Register(a string, interface{})
}

func (r Registry) bar() {
  r.Register(<caret>)
}