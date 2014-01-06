// Copyright 2013 The Go Authors. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package cldr

func (r rule) process(p RuleProcessor, name, context, extend string) error {
	v, err := r.value()
	if err != nil {
		return err
	}
	switch name {
	case "p", "s", "t", "i":
		if strings.HasPrefix(v, cldrIndex) {
			p.Index(v[len(cldrIndex):])
			return nil
		}
		if err := p.Insert(lmap[name[0]], v, context, extend); err != nil {
			return err
		}
	case "pc", "sc", "tc", "ic":
		level := lmap[name[0]]
		for _, s := range v {
			if err := p.Insert(level, string(s), context, extend); err != nil {
				return err
			}
		}
	default:
		return fmt.Errorf("cldr: unsupported tag: %q", name)
	}
	return nil
}

// Process parses the rules for the tailorings of this collation
// and calls the respective methods of p for each rule found.
func (c Collation) Process(p RuleProcessor) (err error) {
	// Collation is generated and defined in xml.go.
	var v string
	for _, r := range c.Rules.Any {
		switch r.XMLName.Local {
		case "reset":
			level := 0
			switch r.Before {
			case "primary", "1":
				level = 1
			case "secondary", "2":
				level = 2
			case "tertiary", "3":
				level = 3
			case "":
			default:
				return fmt.Errorf("cldr: unknown level %q", r.Before)
			}
			v, err = r.value()
			if err == nil {
				err = p.Reset(v, level)
			}
		case "x":
			var context, extend string
			for _, r1 := range r.Any {
				v, err = r1.value()
				switch r1.XMLName.Local {
				case "context":
					context = v
				case "extend":
					extend = v
				}
			}
			for _, r1 := range r.Any {
				if t := r1.XMLName.Local; t == "context" || t == "extend" {
					continue
				}
				r1.rule.process(p, r1.XMLName.Local, context, extend)
			}
		default:
			err = r.rule.process(p, r.XMLName.Local, "", "")
		}
		if err != nil {
			return err
		}
	}
	return nil
}
