package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator;

import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.node.NodeWrapperSelector;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class FunctionArgumentSwapper extends Mutator{

    private Consumer<AstNode> mSwapper = node -> {};

    public static FunctionArgumentSwapper of(int srcNum, int destNum){
        return new FunctionArgumentSwapper(node -> {
            FunctionCall function = ((FunctionCall)node);
            List<AstNode> args = function.getArguments();
            AstNode source = args.get(srcNum);
            AstNode dest = args.get(destNum);
            args.set(srcNum, dest);
            args.set(destNum, source);
            function.setArguments(args);
        });
    }

    public static FunctionArgumentSwapper of(int num, String srcStr, String destStr){
        return new FunctionArgumentSwapper(node -> {
            FunctionCall function = ((FunctionCall)node);
            List<AstNode> args = function.getArguments();
            Map<String, ObjectProperty> data = ((ObjectLiteral) (args.get(num))).getElements().stream()
                    .collect(Collectors.<ObjectProperty, String, ObjectProperty>toMap(e -> (String)NodeWrapperSelector.doSelect(e.getLeft()).get(e.getLeft()), Function.identity()));
            AstNode src = data.get(srcStr).getRight();
            AstNode dest = data.get(destStr).getRight();
            data.get(srcStr).setRight(dest);
            data.get(destStr).setRight(src);
            ((ObjectLiteral) (args.get(num))).setElements(data.values().stream().collect(Collectors.toList()));
            function.setArguments(args);
        });
    }

    private FunctionArgumentSwapper(Consumer<AstNode> swapper) {
        mSwapper = swapper;
    }

    @Override
    protected void mutateNode(AstNode node) {
        mSwapper.accept(node);
    }

    @Override
    protected void restoreNode(AstNode node) {
        mSwapper.accept(node);
    }
}
