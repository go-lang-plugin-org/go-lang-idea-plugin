package simple

import (
	"p1"
	/*begin*/"p2"/*end.Unused import "p2"|RemoveImportFix*/
)

func main() {
	p1.a()
}
