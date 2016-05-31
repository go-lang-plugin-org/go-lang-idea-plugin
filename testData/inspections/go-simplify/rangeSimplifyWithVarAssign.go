package main

func _() {

  var x []int

  for a<warning descr="Redundant '_' expression">, _<caret></warning> := range x {

  }
}