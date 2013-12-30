package main
type a struct {
b struct {
c struct {
d struct {
e struct {
f int
}
}
}
}
}

-----
package main

type a struct {
	b struct {
		c struct {
			d struct {
				e struct {
					f int
				}
			}
		}
	}
}
