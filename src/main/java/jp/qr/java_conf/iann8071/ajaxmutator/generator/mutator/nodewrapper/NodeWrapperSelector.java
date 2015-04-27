package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.nodewrapper;

import com.google.common.collect.ImmutableList;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class NodeWrapperSelector {
    private static final List<NodeWrapper> mWrappers = ImmutableList.of(
            NodeWrapper.<AstNode>of(n -> n.getString(), (n, value) -> n.setString(value)),
            NodeWrapper.<Name>of(n -> n.getIdentifier(), (n, value) -> n.setIdentifier(value)),
            NodeWrapper.<StringLiteral>of(n -> n.getValue(), (n, value) -> n.setValue(value))
    );

    public static NodeWrapper doSelect(AstNode node) {
        return mWrappers.stream().filter(w -> w.isSameType(node)).findAny().get();
    }
}
