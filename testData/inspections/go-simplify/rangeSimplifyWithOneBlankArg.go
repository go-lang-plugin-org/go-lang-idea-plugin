package main

func _() {

  var x []int

  for <warning descr="Redundant '_' expression">_ =<caret> </warning>range x {

  }
}