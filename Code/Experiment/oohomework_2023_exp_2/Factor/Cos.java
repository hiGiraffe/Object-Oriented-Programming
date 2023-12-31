package Factor;

public class Cos implements Factor {
    
    private Factor factor;
    
    public Cos(Factor factor) {
        this.factor = factor;
    }
    
    public Sin toSin() {
        return new Sin(factor);
    }
    
    @Override
    public String toString() {
        return "cos(" + factor.toString() + ")";
    }
    
    @Override
    public Factor derive() {
        Term term = new Term();
        term.addFactor(toSin());
        term = Term.mergeTerm(term, (Term) factor.derive());
        /* TODO 3 */
        return term;
    }
    
    @Override
    public Factor clone() {
        return new Cos(factor.clone());
    }
}
