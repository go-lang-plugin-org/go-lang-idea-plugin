package constantExpressionsInConstDeclarations

const good1 = ^1         // untyped integer constant, equal to -2
const bad1 = /*begin*/uint8(^1)/*end.Constant expression expected*/  // illegal: same as uint8(-2), -2 cannot be represented as a uint8
const good2 = ^uint8(1)  // typed uint8 constant, same as 0xFF ^ uint8(1) = uint8(0xFE)
const good3 = int8(^1)   // same as int8(-2)
const good4 = ^int8(1)   // same as -1 ^ int8(1) = -2