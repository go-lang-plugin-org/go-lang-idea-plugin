package main

func (s Elems) Swap(i, j int) {
	s[i], s[j] = s[j], s[i]
}
