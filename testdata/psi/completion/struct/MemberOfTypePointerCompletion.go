package main

type NodePtr *Node

type Node struct {
    data interface {}
    next NodePtr
}

func foo(n *Node) {
    n.next.<caret>
}
/**---
data
next
