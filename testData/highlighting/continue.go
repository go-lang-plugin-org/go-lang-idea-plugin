package main

import "fmt"

func _() {
	for i := 0; i < 10; i++ {
		fmt.Printf("%d\n", i)
		f := func() {
			<error>continue</error>
		}
        f()
		continue
	}

	<error>continue</error>

	if 1 > 0 {
		<error>continue</error>
	}

    for i := 0; i < 10; i++ {
		defer func() {
			<error>continue</error>
		}()

		go func() {
			<error>continue</error>
		}()

		continue
	}
}