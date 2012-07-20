package main

func main() {
Label1:
    println()
    for {
        break /*begin*/Label1/*end.Invalid break label Label1.*/
    }

Label2:
    for {
    }

    for {
        break /*begin*/Label2/*end.Invalid break label Label2.*/
    }

Label3:
    for {
        break Label3
    }
}
