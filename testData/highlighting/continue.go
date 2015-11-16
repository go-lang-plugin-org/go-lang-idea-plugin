package main

import "fmt"

func _() {
	for i := 0; i < 10; i++ {
		fmt.Printf("%d\n", i)
		continue
	}

	<error>continue</error>

	if 1 > 0 {
		<error>continue</error>
	}
}