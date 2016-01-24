package main

type demoStr struct {
    Field string
}

type f func() *demoStr

func DemoFunc() f {
    return func () *demoStr {
        return &demoStr{Field: "dmeo"}
    }
}

func main() {
    fun := DemoFunc()
    field := fun()
    _ = field.Field
}

type A struct {
}

func (self *A) chain() *A{
    return self
}

func getAnA() (_ *A) {
    return &A{}
}

func _() {
    getAnA().chain()
}

func _(callback func() (interface{}, error)) {
	if _, err := callback(); err != nil {
		err.Error()
	}
}

func _(callback func() error) {
	if err := callback(); err != nil {
		err.Error()
	}
}

func _(fn func() (i int, e error)) {
	i, e :=  fn()
	print(i)
	print(e.Error())
}

func _(fn func() (_ error)) {
	err := fn()
	err.Error()
}

func _(fn func() error) {
	err := fn()
	err.Error()
}

type TestStruct struct {
	SomeId int
}

type DataSlice []*TestStruct

func _() {
	data := DataSlice{}
	for _, element := range *data {
		if element.SomeId > 20 {
			println("some text")
		}
	}
}

func FA() (int, string) {
	return 0, "abc"
}

func FB(_ int, _ string) {
}

func _() {
	FB(123, "abc")
	FB(FA())
}