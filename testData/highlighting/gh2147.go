package main

import "sync"

type myMutex sync.Mutex

var (
	pathLock sync.RWMutex
)

func _() {
	pathLock.RLock()
	defer pathLock.RUnlock()
}

func main() {
	var mtx myMutex
	mtx.<error descr="Unresolved reference 'Lock'">Lock</error>()
	mtx.<error descr="Unresolved reference 'Unlock'">Unlock</error>()
}
