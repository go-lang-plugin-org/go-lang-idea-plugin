package main;

var a struct {
  <error descr="Invalid type ***int: must be typeName or *typeName">***int<caret></error>
}