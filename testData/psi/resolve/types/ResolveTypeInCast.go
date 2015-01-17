package main

import "unsafe"

type emptyInterface struct {
    /*def*/typ  uintptr
    word unsafe.Pointer
}

func TypId(i interface{}) uintptr {
    return (*emptyInterface)(unsafe.Pointer(&i))./*ref*/typ
}