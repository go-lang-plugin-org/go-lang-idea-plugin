package indexExpression

func EncodeInt(a []byte, b int) {}

func main() {
	var d [8]byte
	sp := 1
	EncodeInt(d[0:8], sp)
}