package demo2031

import (
	"time"
)

type Duration time.Duration

func (_ *<error descr="Unresolved type 'string'">string</error>) Demo() {

}

func (_ <error descr="Unresolved type 'int'">int</error>) Demo() {

}

func (_ <error descr="Unresolved type 'ScanState'">ScanState</error>) Demo() {

}

func (_ *Duration) UnmarshalText(data []byte) (error) {
	_ = data
	return nil
}