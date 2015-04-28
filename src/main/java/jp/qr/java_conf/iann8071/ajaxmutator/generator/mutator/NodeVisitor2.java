package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator;

import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutant.Mutant;
import org.mozilla.javascript.ast.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iann8071 on 2015/04/15.
 */
abstract public class NodeVisitor2 implements NodeVisitor {
    private List<Mutant> mMutants = new ArrayList();

    public List<Mutant> mutants(){
        return mMutants;
    }

    @Override
    abstract public boolean visit(AstNode astNode);

    abstract public Mutant addInfoToMutant(Mutant original);
}
