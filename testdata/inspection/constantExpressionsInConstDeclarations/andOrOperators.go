package main

const c1 = "386"
const supportsUnaligned1 = c1 == "386" || c1 == "amd64"
const supportsUnaligned2 = c1 == "386" && c1 == "amd64"
const a1 = true || false
const a2 = true && false
