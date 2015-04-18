package main;
import (
	"fmt"
)

func aaa() {
	fmt.Println()
}

type RateHistoryResponse struct {}
type RateHistoryResponses []RateHistoryResponse

func (r *RateHistoryResponses) persist() {
	for _, rh := range []RateHistoryResponse(*r) {
		rh./*ref*/persist()
	}
}

func (r *RateHistoryResponse) /*def*/persist() {
}