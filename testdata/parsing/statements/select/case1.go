package main
func main() {
    select {
        case a <- b:
            break
    }
}
