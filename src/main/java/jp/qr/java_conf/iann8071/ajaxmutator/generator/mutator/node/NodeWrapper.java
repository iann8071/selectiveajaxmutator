package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.node;

import org.mozilla.javascript.ast.AstNode;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by iann8071 on 2015/04/26.
 */
public class NodeWrapper<T extends AstNode, U> {
    Function<T, U> mGetter;
    BiConsumer<T, U> mSetter;

    public static <T, U> NodeWrapper of(Function<T, U> getter, BiConsumer<T, U> setter){
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

    private NodeWrapper (Function<T, U> getter, BiConsumer<T, U> setter){
        mGetter = getter;
        mSetter = setter;
    }

    public U get(T node){
        return mGetter.apply(node);
    }

    public void set(T node, U value){
        mSetter.accept(node, value);
    }
}
