package main

func function(a, b int) (/*def*/c, d int) {
    /*ref*/c, d = b, a
    return
}
