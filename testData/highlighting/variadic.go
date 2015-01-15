package main

type User interface {
    Name() string
}

func <warning>Add0</warning>(users ...User)  { users[0].Name() }
func <warning>Add1</warning>(users ...*User) { users[0].<error>Name</error>() } 
func <warning>Add2</warning>(users User)     { users[0].<error>Name</error>() }
func <warning>Add3</warning>(users ...User)  { users.<error>Name</error>() }
