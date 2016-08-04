package main

import "regexp"

func main() {
	var _ = regexp.MustCompile(`\\n`)
	var _ = regexp.MustCompile(`[a[a-z]b]{2,}`)

	var _ = regexp.MustCompile(`x*`);
	var _ = regexp.MustCompile(`x+`);
	var _ = regexp.MustCompile(`x?`);
	var _ = regexp.MustCompile(`x{1,4}`);
	var _ = regexp.MustCompile(`x<weak_warning descr="Repetition range replaceable by '+'">{1,}</weak_warning>`);
	var _ = regexp.MustCompile(`x<weak_warning descr="Single repetition">{1}</weak_warning>`);
	var _ = regexp.MustCompile(`x*?`);
	var _ = regexp.MustCompile(`x+?`);
	var _ = regexp.MustCompile(`x??`);
	var _ = regexp.MustCompile(`x{1,2}?`);
	var _ = regexp.MustCompile(`x<weak_warning descr="Repetition range replaceable by '+'">{1,}?</weak_warning>`);
	var _ = regexp.MustCompile(`x{3}?`);

	var _ = regexp.MustCompile(`\p{^Han}`)
	var _ = regexp.MustCompile(`.`)
	var _ = regexp.MustCompile(`[xyz]`)
	var _ = regexp.MustCompile(`[^xyz]`)
	var _ = regexp.MustCompile(`\d`)
	var _ = regexp.MustCompile(`\D`)
	var _ = regexp.MustCompile(`[[:alpha:]]`)
	var _ = regexp.MustCompile(`[^[:alpha:]]`)
	var _ = regexp.MustCompile(`[[^:alph<warning descr="Duplicate character 'a' in character class">a</warning><warning descr="Duplicate character ':' in character class">:</warning>]]`)
	var _ = regexp.MustCompile(`\pN`)
	var _ = regexp.MustCompile(`\p{Han}`)
	var _ = regexp.MustCompile(`\PN`)
	var _ = regexp.MustCompile(`\P{Han}`)
	var _ = regexp.MustCompile(`\P{^Coptic}`)
	var _ = regexp.MustCompile(`y`)
	var _ = regexp.MustCompile(`|y`)
	var _ = regexp.MustCompile(`(re)`)
	var _ = regexp.MustCompile(`(?P<name>re)`)
	var _ = regexp.MustCompile(`(?:re)`)
	var _ = regexp.MustCompile(`(?<error descr="Unknown inline option flag">f</error><error descr="Unknown inline option flag">l</error><error descr="Unknown inline option flag">a</error><error descr="Unknown inline option flag">g</error>s)`)
	var _ = regexp.MustCompile(`(?<error descr="Unknown inline option flag">f</error><error descr="Unknown inline option flag">l</error><error descr="Unknown inline option flag">a</error><error descr="Unknown inline option flag">g</error>s:re)`)

	var _ = regexp.MustCompile(`(?im-sU)`)
	var _ = regexp.MustCompile(`(?imsU:re)`)
	var _ = regexp.MustCompile(`(?ims<error descr="Unknown inline option flag">G</error>U:re)`)

	var _ = regexp.MustCompile(`^`)
	var _ = regexp.MustCompile(`$`)
	var _ = regexp.MustCompile(`\A`)
	var _ = regexp.MustCompile(`\b`)
	var _ = regexp.MustCompile(`\B`)
	var _ = regexp.MustCompile(`\z`)
	var _ = regexp.MustCompile(`\a`)
	var _ = regexp.MustCompile(`\f`)
	var _ = regexp.MustCompile(`\t`)
	var _ = regexp.MustCompile(`\n`)
	var _ = regexp.MustCompile(`\r`)
	var _ = regexp.MustCompile(`\v`)
	var _ = regexp.MustCompile(`\*`)
	var _ = regexp.MustCompile(`\123`)
	var _ = regexp.MustCompile(`\x7F`)
	var _ = regexp.MustCompile(`\x{10FFFF}`)
	var _ = regexp.MustCompile(`\Q...\E`)
	var _ = regexp.MustCompile(``)
	var _ = regexp.MustCompile(`-Z`)
	var _ = regexp.MustCompile(`\d`)
	var _ = regexp.MustCompile(`[:fo<warning descr="Duplicate character 'o' in character class">o</warning><warning descr="Duplicate character ':' in character class">:</warning>]`)
	var _ = regexp.MustCompile(`\p{Han}`)
	var _ = regexp.MustCompile(`\pC`)
	var _ = regexp.MustCompile(`[\d]`)
	var _ = regexp.MustCompile(`[^\d]`)
	var _ = regexp.MustCompile(`[\D]`)
	var _ = regexp.MustCompile(`[^\D]`)
	var _ = regexp.MustCompile(`[[:cntrl:]]`)
	var _ = regexp.MustCompile(`[^[:cntrl:]]`)
	var _ = regexp.MustCompile(`[\p{Han}]`)
	var _ = regexp.MustCompile(`[^\p{Han}]`)
	var _ = regexp.MustCompile(`\d`)
	var _ = regexp.MustCompile(`\D`)
	var _ = regexp.MustCompile(`\s`)
	var _ = regexp.MustCompile(`\S`)
	var _ = regexp.MustCompile(`\w`)
	var _ = regexp.MustCompile(`\W`)
	var _ = regexp.MustCompile(`[[:alnum:]]`)
	var _ = regexp.MustCompile(`[[:alpha:]]`)
	var _ = regexp.MustCompile(`[[:ascii:]]`)
	var _ = regexp.MustCompile(`[[:blank:]]`)
	var _ = regexp.MustCompile(`[[:cntrl:]]`)
	var _ = regexp.MustCompile(`[[:digit:]]`)
	var _ = regexp.MustCompile(`[[:graph:]]`)
	var _ = regexp.MustCompile(`[[:lower:]]`)
	var _ = regexp.MustCompile(`[[:print:]]`)
	var _ = regexp.MustCompile(`[[:punct:]]`)
	var _ = regexp.MustCompile(`[[:space:]]`)
	var _ = regexp.MustCompile(`[[:upper:]]`)
	var _ = regexp.MustCompile(`[[:word:]]`)
	var _ = regexp.MustCompile(`[[:xdigit:]]`)

	var _ = regexp.MustCompile(`[<error descr="Unknown POSIX character class">[:</error><error descr="POSIX character class name expected">^</error><error descr="Pattern expected">a</error>lpha<error descr="Pattern expected">:</error>]<error descr="Pattern expected">]</error>`) // must be fixed in platform
}