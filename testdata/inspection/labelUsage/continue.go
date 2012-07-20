package main

func main() {
Label1:
    println()
    for {
        continue /*begin*/Label1/*end.Invalid continue label Label1.*/
    }

Label2:
    for {
    }

    for {
        continue /*begin*/Label2/*end.Invalid continue label Label2.*/
    }

Label3:
    for {
        continue Label3
    }
}
