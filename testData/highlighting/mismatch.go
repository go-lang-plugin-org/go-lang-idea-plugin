package main

type validate interface {
    validate() (bool, error)
    void()
}

func main() {
    var s validate
    err := <error descr="Multiple-value s.validate() in single-value context">s.validate()</error>
    _ = err
    v := <error descr="s.void() used as value"><error descr="s.void() doesn't return a value">s.void()</error></error>
    _ = v
    shortDeclarationWithoutExpressions :=<error descr="<expression> expected, got ';'"> </error>;
    _ = shortDeclarationWithoutExpressions
}