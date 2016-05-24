package test

import (
	"test"
	"package/path"
)
/*
#cgo LDFLAGS: -L${SRCDIR}/libs -lopus

#include "include/opus.h"
 */
import "C"

func hello() string {
  return "hello"
}