package main
const (X=iota;A)
const (X=iota;A = 5)
const (X=iota;/*begin*/A =/*end.Missing expression in const declaration|AddMissingExpressionFix|RemoveRedundantConstFix*/ )
const (X=iota;
A,B = 5,3
)
const (X=iota; /*begin*/A, B = 1 + 2/*end.Missing expression in const declaration|AddMissingExpressionFix|RemoveRedundantConstFix*/)
const (X=iota;
/*begin*/A, B = 5/*end.Missing expression in const declaration|AddMissingExpressionFix|RemoveRedundantConstFix*/
)
