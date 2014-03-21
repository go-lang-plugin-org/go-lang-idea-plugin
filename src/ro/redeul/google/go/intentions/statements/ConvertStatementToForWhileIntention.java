package ro.redeul.google.go.intentions.statements;

public class ConvertStatementToForWhileIntention extends ConvertStatementToIfIntention {

    @Override
    protected String getKeyword() {
        return "for";
    }
}
