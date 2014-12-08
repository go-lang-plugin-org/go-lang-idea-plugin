package main

func (/*def*/p *Point) Length() float64 {
	return math.Sqrt(p.x * /*ref*/p.x + p.y * p.y)
}
