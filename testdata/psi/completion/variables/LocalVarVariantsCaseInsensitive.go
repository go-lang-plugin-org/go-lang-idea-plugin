package main

const var_topLevelConst = 1
var var_topLevelVar = 1

func (var_receiver *T)f(var_param int) (var_result int) {
    var var_internalTop
    var_shortOuterVar := 1

    for var_inShortFor := 1; true; {
        const var_innerConst = 1
        var var_insideInnerBlock
        if var_inIfStatement := 1; true {
            switch var_inSwitchStatement := 1; true {
            case true:
                var_inCaseClause := 2
                vAR_<caret>
            }
        }
    }
}
/**---
var_inCaseClause
var_inIfStatement
var_innerConst
var_inShortFor
var_insideInnerBlock
var_inSwitchStatement
var_internalTop
var_param
var_receiver
var_result
var_shortOuterVar
var_topLevelConst
var_topLevelVar
