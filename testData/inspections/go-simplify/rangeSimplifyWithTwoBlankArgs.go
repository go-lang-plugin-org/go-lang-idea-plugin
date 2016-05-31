package p

func _() {

  var x []int

  for <warning descr="Redundant '_' expression">_, _ =<caret> </warning>range x {

  }
}