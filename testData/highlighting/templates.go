package main

import "text/template"

func main() {
    template.Must(template.ParseGlob("templates/*"))
}
