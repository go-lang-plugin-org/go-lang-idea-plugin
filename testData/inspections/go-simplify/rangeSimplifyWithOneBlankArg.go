package main

func _() {

  var x []int

  for <weak_warning descr="Can simplify '_'">_ =<caret> </weak_warning>range x {

  }
}