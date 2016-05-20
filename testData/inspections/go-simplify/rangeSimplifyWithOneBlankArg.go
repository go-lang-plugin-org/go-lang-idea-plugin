package main

func _() {

  var x []int

  for <weak_warning descr="Redundant '_' expression">_ =<caret> </weak_warning>range x {

  }
}