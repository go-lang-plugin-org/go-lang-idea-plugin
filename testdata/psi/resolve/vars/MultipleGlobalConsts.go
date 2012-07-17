package test2

const (
    a = /*ref*/b
    /*def*/b = 10
)

func main() {
    println(a, b)
}
