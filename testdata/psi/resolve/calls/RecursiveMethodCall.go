package main

func /*def*/F(n int) int {
    if n <= 1 {
        return 1
    }
    return n * /*ref*/F(n-1)
}
