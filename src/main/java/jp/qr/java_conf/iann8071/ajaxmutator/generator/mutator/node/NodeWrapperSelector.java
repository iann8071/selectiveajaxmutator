package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.node;

import com.google.common.collect.ImmutableList;
import org.mozilla.javascript.ast.*;

import java.util.List;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class NodeWrapperSelector {
    private static final List<NodeWrapper> mWrappers = ImmutableList.of(
            NodeWrapper.<Name, String>of(n -> n.getIdentifier(), (n, value) -> n.setIdentifier(value)),
            NodeWrapper.<StringLiteral, String>of(s -> s.getValue(), (s, value) -> s.setValue(value)),
            NodeWrapper.<VariableInitializer, AstNode>of(v -> v.getInitializer(), (v, value) -> v.setInitializer(value)),
            NodeWrapper.<ExpressionStatement, AstNode>of(e -> e.getExpression(), (e, value) -> e.setExpression(value)),
            NodeWrapper.<AstNode, String>of(n -> n.getString(), (n, value) -> n.setString(value))
    );

    public static NodeWrapper doSelect(AstNode node) {
        return mWrappers.stream().filter(w -> w.isSameType(node)).findFirst().get();
    }
}
