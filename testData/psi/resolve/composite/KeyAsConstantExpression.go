package main

const /*def*/MyConstant = "Hello"

type MyMap map[string]int

var MyVar = MyMap{
    /*ref*/MyConstant:10,
}

