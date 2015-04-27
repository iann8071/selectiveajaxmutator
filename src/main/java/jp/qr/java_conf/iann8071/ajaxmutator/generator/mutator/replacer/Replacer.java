package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.replacer;

import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.Mutator;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.nodewrapper.NodeWrapper;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.nodewrapper.NodeWrapperSelector;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Randomizer;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.PropertyGet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class Replacer extends Mutator {
    public static final int SIZE = Integer.MAX_VALUE;

    private List<String> mCandidates = new ArrayList();
    private Function<AstNode, AstNode> mNodeExtractor;

    public static Replacer ofTargetNode(){
        return new Replacer(node -> ((FunctionCall)node).getTarget());
    }

    public static Replacer ofProperty(){
        return new Replacer(node -> {try {
            return ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty();
        } catch(ClassCastException e) {
            return ((FunctionCall) node).getTarget();
        }});
    }

    public static Replacer ofArguments(int num){
        return new Replacer(node -> num < ((FunctionCall) node).getArguments().size() ? ((FunctionCall) node).getArguments().get(num)
                : ((FunctionCall) node).getArguments().get(((FunctionCall) node).getArguments().size() - 1));
    }

    private Replacer(Function<AstNode, AstNode> nodeExtractor){
        mNodeExtractor = nodeExtractor;
    }

    public Replacer addCandidate(String candidate){
        mCandidates.add(candidate);
        return this;
    }

    @Override
    protected boolean collect(AstNode node) {
        String originalValue = NodeWrapperSelector.doSelect(mNodeExtractor.apply((FunctionCall) node)).get(node);
        if (!mCandidates.contains(originalValue)) mCandidates.add(originalValue);
        return true;
    }

    @Override
    protected String mutateNode(AstNode node) {
        String originalValue = NodeWrapperSelector.doSelect(mNodeExtractor.apply(node)).get(node);
        NodeWrapperSelector.doSelect(node).set(mNodeExtractor.apply(node), Randomizer.differentString(mCandidates, originalValue));
        return originalValue;
    }

    @Override
    protected void restoreNode(AstNode node, String originalValue) {
        NodeWrapperSelector.doSelect(node).set(mNodeExtractor.apply(node), originalValue);
    }
}
