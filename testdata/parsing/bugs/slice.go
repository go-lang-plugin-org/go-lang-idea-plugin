// Copyright 2013 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package cldr

// Value returns the reflect.Value of the underlying slice.
func (s *Slice) Value() reflect.Value {
	return s.ptr.Elem()
}

