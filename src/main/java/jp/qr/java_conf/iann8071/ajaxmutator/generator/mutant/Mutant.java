package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutant;
import org.mozilla.javascript.ast.AstNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iann8071 on 2015/04/08.
 */
public class Mutant<T extends AstNode> {
    public static final String ORIGINAL = "original";
    public static final String MUTANT = "mutant";
    public static final String OPERATOR = "operator";
    private T mNode;
    private Map<String, String> kVs = new HashMap();

    public static Builder builder() {
        return new Builder();
    }

    public void put(String key, String value) {
        kVs.put(key, value);
    }

    public void put(T node) {
        mNode = node;
    }
    public void put(Mutant m) {
        kVs.putAll(m.kVs);
    }

    public Map<String, String> map(){
        return kVs;
    }

    public String value(String key) {
        return kVs.get(key);
    }

    public T node() {
        return mNode;
    }

    public static class Builder{
        Mutant mMutant = new Mutant();

        public Mutant build(){
            return mMutant;
        }

        public Builder put(AstNode node) {
            mMutant.put(node);
            return this;
        }

        public Builder put(String key, String value) {
            mMutant.put(key, value);
            return this;
        }

        public Builder put(Mutant mutant){
            mMutant.put(mutant);
            return this;
        }
    }
}
