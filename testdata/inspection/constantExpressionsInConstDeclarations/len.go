package constantExpressionsInConstDeclarations

const (
	c1 = imag(2i)                    // imag(2i) = 2.0 is a constant
	c2 = len([10]float64{2})         // [10]float64{2} contains no function calls
	c3 = len([10]float64{c1})        // [10]float64{c1} contains no function calls
	c4 = len([10]float64{imag(2i)})  // imag(2i) is a constant and no function call is issued

	// the following should be displayed but not right now
	// c5 = len([10]float64{imag(z)})	// invalid: imag(x) is a (non-constant) function call
)
var z complex128
