package main

func main() {
    select {
    case a <- 1:
        return a
    case a, ok := <-c3:
        break
        //
    default:
        return b
    }
}
