package main

func _() {
<error descr="Unused label 'UnusedLabel'">UnusedLabel</error>:
    println("unused")
UsedLabel:
    println("used")
    goto UsedLabel
_:
    println("blank")
}