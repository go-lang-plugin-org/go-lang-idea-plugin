package if_scope

func pow(x float64) float64 {
    if v := x; true {
        return v
    }
    return x
}

