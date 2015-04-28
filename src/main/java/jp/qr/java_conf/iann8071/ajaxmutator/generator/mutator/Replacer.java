package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator;

import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.node.NodeWrapperSelector;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Randomizer;
import org.mozilla.javascript.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class Replacer extends Mutator {
    public static final int SIZE = Integer.MAX_VALUE;

    private List<String> mCandidates = new ArrayList();
    private Function<AstNode, AstNode> mNodeExtractor;
    private Predicate<String> mFilter = candidate -> true;
    private String mOriginalValue;

    public static Replacer ofFunctionTarget() {
        return new Replacer(node -> ((FunctionCall) node).getTarget());
    }

    public static Replacer ofFunctionProperty() {
        return new Replacer(node -> {
            try {
                return ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty();
            } catch (ClassCastException e) {
                return ((FunctionCall) node).getTarget();
            }
        });
    }

    public static Replacer ofFunctionArguments(int num) {
        return new Replacer(node -> ((FunctionCall) node).getArguments().get(num));
    }

    public static Replacer ofFunctionLastArgument() {
        return new Replacer(node -> ((FunctionCall) node).getArguments().get(((FunctionCall) node).getArguments().size() - 1));
    }

    public static Replacer ofFunctionArguments(int num, String key) {
        return new Replacer(node -> ((ObjectLiteral) ((FunctionCall) node).getArguments().get(num)).getElements().stream()
                .collect(Collectors.<ObjectProperty, String, AstNode>toMap(e -> (String) (NodeWrapperSelector.doSelect(e.getLeft()).get(e.getLeft())), e -> e.getRight())).get(key));
    }

    public static Replacer ofAssignmentTarget() {
        return new Replacer(node -> {
            try {
                return ((PropertyGet) ((Assignment) node).getLeft()).getProperty();
            } catch (ClassCastException e) {
                return ((ElementGet) ((Assignment) node).getLeft()).getElement();
            }
        });
    }

    public static Replacer ofAssignmentValue() {
        return new Replacer(node -> ((Assignment) node).getRight());
    }

    private Replacer(Function<AstNode, AstNode> nodeExtractor) {
        mNodeExtractor = nodeExtractor;
    }

    public Replacer setCandidates(List<String> sharedCandidates){
        mCandidates = sharedCandidates;
        return this;
    }

    public Replacer addCandidate(String candidate) {
        mCandidates.add(candidate);
        return this;
    }

    public Replacer addCandidates(List<String> candidates) {
        mCandidates.addAll(candidates);
        return this;
    }

    public Replacer addFilter(List<String> filteredCandidates) {
        mFilter = candidate -> filteredCandidates.contains(candidate);
        return this;
    }

    @Override
    protected void collect(AstNode node) {
        String originalValue = (String) NodeWrapperSelector.doSelect(mNodeExtractor.apply((FunctionCall) node)).get(node);
        if (!mCandidates.contains(originalValue)) mCandidates.add(originalValue);
    }

    @Override
    protected void mutateNode(AstNode node) {
        String originalValue = (String) NodeWrapperSelector.doSelect(mNodeExtractor.apply(node)).get(node);
        NodeWrapperSelector.doSelect(node).set(mNodeExtractor.apply(node), Randomizer.differentString(mCandidates.stream().filter(mFilter).collect(Collectors.toList()), originalValue));
        mOriginalValue = originalValue;
    }

    @Override
    protected void restoreNode(AstNode node) {
        NodeWrapperSelector.doSelect(node).set(mNodeExtractor.apply(node), mOriginalValue);
    }
}
