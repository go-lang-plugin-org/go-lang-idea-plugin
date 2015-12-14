package main

func recv(value int, ch chan bool) {
	if value < 0 {
		ch <- true
		return
	}
	go recv(value - 1, ch)<caret>
}