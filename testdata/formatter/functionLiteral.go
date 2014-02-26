package main

import . "my-test-framework"

func main() {
	foo(func() (int){
			return (2+2)
	})
	Describe("indentation of anonymous functions", func() {
		It("indents properly", func() {
			Fail("didn't work!")
		})
		It(func() {
			Fail("didn't work")
		})
		It("works with a single line function", func() {DoNothing()})
	})
}

-----
package main

import . "my-test-framework"

func main() {
	foo(func() (int) {
		return (2 + 2)
	})
	Describe("indentation of anonymous functions", func() {
		It("indents properly", func() {
			Fail("didn't work!")
		})
		It(func() {
			Fail("didn't work")
		})
		It("works with a single line function", func() {DoNothing()})
	})
}
