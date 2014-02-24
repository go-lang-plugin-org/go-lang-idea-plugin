package main

import . "my-test-framework"

func main() {
	Describe("indentation of anonymous functions", func() {
		It("indents properly", func() {
			Fail("didn't work!")
		})
	})
}

-----
package main

import . "my-test-framework"

func main() {
	Describe("indentation of anonymous functions", func() {
		It("indents properly", func() {
			Fail("didn't work!")
		})
	})
}
