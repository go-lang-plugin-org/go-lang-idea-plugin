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