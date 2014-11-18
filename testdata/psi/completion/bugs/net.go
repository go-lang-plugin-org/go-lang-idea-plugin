package net

type AddrError struct {
	Err  string
	Addr string
}

func (e *AddrError) Error() string {
	if e == nil {
		return "<nil>"
	}
	s := e.Err
	if e.Addr != "" {
		s += " " + e.Addr
	}
	return s
}

func (e *AddrError) Temporary() bool {
	return false
}

func (e *AddrError) Timeout() bool {
	return false
}