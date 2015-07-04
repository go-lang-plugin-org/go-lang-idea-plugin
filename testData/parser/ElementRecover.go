package foo

type connection interface{}

func (conn *connection) Delete(ctx *context.Context) (err errors.Error) {
	connection := &entity.Connection{
		connection.AccountID = ctx.Bag["accountID"].(int64),
		connection.AccountID = ctx.Bag["accountID"].(int64)
	}

	if err = conn.storage.Delete(connection); err != nil {
		return
	}

	server.WriteResponse(ctx, "", http.StatusNoContent, 10)
	return
}

func (conn *connection) Create(ctx *context.Context) (err errors.Error) {

}