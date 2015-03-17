package foo

type Registry interface {
  Register(string, interface{})
}

func (r Registry) bar() {
  r.Register(<caret>)
}