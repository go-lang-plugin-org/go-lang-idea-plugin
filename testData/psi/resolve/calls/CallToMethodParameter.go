package main
func F(/*def*/n func()) {
    /*ref*/n()    // <-- first place
}
