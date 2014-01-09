package main

type (
	a func()
	b func(x int) int
	c func(a, _ int, z float32) bool
	d func(a, b int, z float32) (bool)
	e func(prefix string, values ...int)
	f func(a, b int, z float64, opt ...interface{}) (success bool)
	g func(int, int, float64) (float64, *[]int)
	h func(n int) func(p *T)
	i func() ()
)
