package main

func Ok1() {
Ok1_Label1:
    goto Ok1_Label1

    goto Ok1_Label2
Ok1_Label2:
}

func JumpOut() {
JumpOut_Label1:
    for {
        goto JumpOut_Label1
    }

    if true {
        goto JumpOut_Label1
    } else {
        goto JumpOut_Label1
    }
}

func JumpIn() {
    {
    JumpIn_Label1:
    }

    goto /*begin*/JumpIn_Label1/*end.goto JumpIn_Label1 jumps into block.*/
}

func SkipVariable() {
    goto /*begin*/SkipVariable_Label1/*end.goto SkipVariable_Label1 jumps over declaration of i.*/
    i := 3
SkipVariable_Label1:
}
