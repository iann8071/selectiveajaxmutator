package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator;

import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.node.NodeWrapperSelector;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import java.util.function.Function;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class FunctionCallRemover extends Mutator {

    private Function<AstNode, AstNode> mNodeExtractor;
    private AstNode mOriginalFunction;

    public static FunctionCallRemover of(){
        return new FunctionCallRemover(node -> node.getParent());
    }

    private FunctionCallRemover(Function<AstNode, AstNode> nodeExtractor) {
        mNodeExtractor = nodeExtractor;
    }

    @Override
    protected void mutateNode(AstNode node) {
        mOriginalFunction = (AstNode) NodeWrapperSelector.doSelect(mNodeExtractor.apply(node)).get(node);
        NodeWrapperSelector.doSelect(node).set(mNodeExtractor.apply(node), new AstNode(){
            @Override
            public String toSource(int depth) {
                return new StringBuilder().append(makeIndent(depth)).append("void(0)").toString();
            }
            @Override
            public void visit(NodeVisitor nodeVisitor) {}
        });
    }

    @Override
    protected void restoreNode(AstNode node) {
        NodeWrapperSelector.doSelect(node).set(mNodeExtractor.apply(node), mOriginalFunction);
    }
}
