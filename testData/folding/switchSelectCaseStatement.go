package foo

func main() <fold text='{...}'>{
	ch := make(chan int)
	select <fold text='{...}'>{
	case _ <- ch:<fold text='...'>
		println("demo")</fold>
	case _ <- ch:<fold text='...'>
		println("demo")
		println("demo")</fold>
	}</fold>

	switch true <fold text='{...}'>{
	case 1 == 2:<fold text='...'>
		println("demo")</fold>

	case 2 == 3:<fold text='...'>
		println("demo")
		println("demo")</fold>

	default:<fold text='...'>
		println("demo")
		println("demo")
		println("demo")</fold>
	}</fold>

	switch true <fold text='{...}'>{
	case 3 == 4:<fold text='...'>
		<fold text='{...}'>{
			println("demo")
		}</fold></fold>

	default:<fold text='...'>
		println("demo")</fold>
	}</fold>

}</fold>