package p1

func bad() {
	// this file should not see types defined in the test files
	_ = /*begin*/T1/*end.Unresolved symbol: 'T1'|CreateFunctionFix|CreateClosureFunctionFix*/(1)

	// but is should see type defined inside this package
	_ = MainT1(1)
}
