package main

func main() <fold text='{...}'>{
    var a interface{}
    switch t := a.(type) {
    case S:<fold text='...'>
        println(t./*ref*/a)</fold>
    case D:<fold text='...'>
        break</fold>
    }
}</fold>