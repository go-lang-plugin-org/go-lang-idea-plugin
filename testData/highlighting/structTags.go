package demo

type demoBrokenTags struct {
	CorrectMultipleTags string `json:"demo,something,something" dark:"side"`
	CorrectSingleTag    string `json:"demo,something,something"`
	WrongTag1           string <warning>`json: "demo"`</warning>
	WrongTag2           string <warning>`json:"demo`</warning>
	WrongTag3           string <warning>`json:demo`</warning>
	WrongTag4           string <warning>` json:demo`</warning>
	WrongTag5           string <warning>"json:demo"</warning>
	WrongTag6           string <warning>"json:'demo'"</warning>
	WrongTag7           string <warning>`json:"demo"; bson:"demo"`</warning>
	WrongTag8           string `
}
<EOLError></EOLError>