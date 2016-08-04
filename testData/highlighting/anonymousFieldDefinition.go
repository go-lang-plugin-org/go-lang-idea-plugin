package main

type MyType int

var <warning descr="Unused variable 't'">t</warning> struct {
  *int
  string

  <error descr="Invalid type []float64: must be typeName or *typeName">[]float64</error>
  <error descr="Invalid type map[int]int: must be typeName or *typeName">map[int]int</error>
  <error descr="Invalid type chan int64: must be typeName or *typeName">chan int64</error>
  <error descr="Invalid type *[]int32: must be typeName or *typeName">*[]int32</error>
  <error descr="Invalid type *(uint): must be typeName or *typeName">*(uint)</error>
  <error descr="Invalid type **float32: must be typeName or *typeName">**float32</error>
  <error descr="Invalid type *struct{}: must be typeName or *typeName">*struct{}</error>
}