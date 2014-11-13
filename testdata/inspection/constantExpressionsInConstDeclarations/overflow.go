package constantExpressionsInConstDeclarations

const good1 = uint(0)
const good2 = int8(-1)

const Huge = 1 << 100         // Huge == 1267650600228229401496703205376  (untyped integer constant)
const Four int8 = Huge >> 98  // Four == 4                                (type int8)

const bad1 = /*begin*/uint(-1)/*end.Constant expression expected*/     // -1 cannot be represented as a uint
const bad2 = /*begin*/int(3.14)/*end.Constant expression expected*/    // 3.14 cannot be represented as an int
const bad3 = /*begin*/int64(Huge)/*end.Constant expression expected*/  // 1267650600228229401496703205376 cannot be represented as an int64
const bad4 = /*begin*/Four * 300/*end.Constant expression expected*/   // operand 300 cannot be represented as an int8 (type of Four)
const bad5 = /*begin*/Four * 100/*end.Constant expression expected*/   // product 400 cannot be represented as an int8 (type of Four)
const bad6 = /*begin*/int32(1) << 33/*end.Constant expression expected*/   // illegal    (constant 8589934592 overflows int32)
const bad7 = /*begin*/float64(2) >> 1/*end.Constant expression expected*/  // illegal    (float64(2) is a typed floating-point constant)