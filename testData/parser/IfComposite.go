package main

func main() {
    if allowedErrors[osPkg{"", ""}] {
     
    }
}

type osPkg struct {
    goos, pkg string
}


var allowedErrors = map[osPkg]bool{
    osPkg{"windows", "log/syslog"}: true,
    osPkg{"plan9", "log/syslog"}:   true,
}
