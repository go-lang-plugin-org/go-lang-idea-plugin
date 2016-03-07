package main

import (
	"bufio"
	"fmt"
	"os"
)

func check(e error) {
	if e != nil {
		panic(e)
	}
}

func main() {
	fmt.Println("Reading your file now")

	fileHandle, err := os.Open("/Path/to/your/file/fileName")

	check(err)
	defer fileHandle.Close()

	newReader := bufio.NewScanner(fileHandle)
	output := ""

	for newReader.Scan() {
		output += newReader.Text()
	}

	fmt.Println("The contents of the file =>", output)
}