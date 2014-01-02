package main

type (
  T0 []string
  T1 []string
  T2 struct{ a, b int }
  T3 struct{ a, c int }
  T4 func(int, float64) *T0
  T5 func(x int, y float64) *[]string
)