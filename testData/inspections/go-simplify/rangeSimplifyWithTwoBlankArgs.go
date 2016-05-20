package p

func _() {

  var x []int

  for <weak_warning descr="Redundant '_' expression">_, _ =<caret> </weak_warning>range x {

  }
}