package main

func main() {
	fs := *(*func(width int) struct{})(nil)
	/*def*/width := 1
	_, _ = fs, /*ref*/width
}