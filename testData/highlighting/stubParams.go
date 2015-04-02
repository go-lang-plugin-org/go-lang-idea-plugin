package main

import (
    "os"
)

func <warning>writeValues</warning>(outfile string) string {
   file, _ := os.Create(outfile)
   return file.Name()
}

