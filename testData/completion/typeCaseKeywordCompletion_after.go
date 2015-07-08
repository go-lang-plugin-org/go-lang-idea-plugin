package foo

func bar(p interface{}) string {
	switch _ := p.(type) {
	case error:
		return nil
	case :
		<caret>
	}
	return ""
}