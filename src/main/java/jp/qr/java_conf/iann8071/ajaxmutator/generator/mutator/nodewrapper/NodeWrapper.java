package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.nodewrapper;

import com.google.common.collect.ImmutableList;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class NodeWrapper<T extends AstNode> {
    Function<T, String> mGetter;
    BiConsumer<T, String> mSetter;

    public static <T> NodeWrapper of(Function<T, String> getter, BiConsumer<T, String> setter){
        return new NodeWrapper(getter, setter);
    }

    public boolean isSameType(AstNode node){
        try {
            T tested = (T) node;
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private NodeWrapper (Function<T, String> getter, BiConsumer<T, String> setter){
        mGetter = getter;
        mSetter = setter;
    }

    public String get(T node){
        return mGetter.apply(node);
    }

    public void set(T node, String value){
        mSetter.accept(node, value);
    }
}
