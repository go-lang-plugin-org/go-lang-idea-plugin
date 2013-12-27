func main() {
    sum := 0
    for i := 0; i < 10; i++ {
        sum += i
    }
    for ; sum < 1000; {
        sum += sum
    }
    for sum < 1000 {
        sum += sum
    }
    for {
      fmt.Println(sum)
      break
    }
}