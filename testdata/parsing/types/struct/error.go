package main

type cartesian struct

func createSolver(questions chan polar) chan cartesian {
    answers := make(chan cartesian)
    go func() {
        for {
            polarCoord := <-questions
            answers <-cartesian{x,y}
        }
    }()
    return answers
}

func main() {
}

