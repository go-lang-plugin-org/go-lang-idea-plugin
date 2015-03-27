package main

type validate interface {
    validate() (bool, error)
}

func main() {
    var s validate
    err := <error>s.validate()</error>
    _ = err
}