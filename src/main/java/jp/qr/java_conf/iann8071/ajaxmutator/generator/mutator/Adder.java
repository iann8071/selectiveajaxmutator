package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator;

import jp.qr.java_conf.iann8071.ajaxmutator.util.Randomizer;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import java.util.List;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class Adder extends Mutator {

    private AstNode mOriginalNode;
    private String mAppendedString;

    public static Adder of(List<String> candidates){
        return new Adder(Randomizer.string(candidates));
    }

    private Adder(String appendedString) {
        mAppendedString = appendedString;
    }

    @Override
    protected void mutateNode(AstNode node) {
        mOriginalNode = node;
        node = new AstNode() {
            @Override
            public String toSource(int depth) {
                return new StringBuilder().append(mOriginalNode.toSource()).append(mAppendedString).toString();
            }
            @Override
            public void visit(NodeVisitor nodeVisitor) {}
        };
    }

    @Override
    protected void restoreNode(AstNode node) {
        node = mOriginalNode;
    }
}
