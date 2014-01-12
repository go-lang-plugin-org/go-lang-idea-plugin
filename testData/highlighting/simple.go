package main

func foo() {
    i := 1
    for (i) {return 0}
    if (i) {return <error>j</error>}

    headers := nil
    for _, h := range headers {
      h++
    }
}

