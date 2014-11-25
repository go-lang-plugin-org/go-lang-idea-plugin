package functionWithResultButWihtoutReturn

func Ok1(p []byte, r rune) int {
	switch i := uint32(r); {
	case i <= 1:
		return 1
	case i <= 2:
		return 2
	case i <  3:
		fallthrough
	case i <= 4:
		return 3
	default:
		return 4
	}
}

func Bad1(p []byte, r rune) int {
	switch i := uint32(r); {
	case i <= 1:
		return 1
	case i <= 2:
		return 2
	case i <  3:
		fallthrough
	case i <= 4:
		return 3
	default:
	}
/*begin*/}/*end.Function ends without a return statement|AddReturnStmtFix|RemoveFunctionResultFix*/