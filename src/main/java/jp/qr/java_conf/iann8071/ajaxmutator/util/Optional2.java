package jp.qr.java_conf.iann8071.ajaxmutator.util;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by iann8071 on 2015/04/23.
 */
public class Optional2<T> {
        private Optional<T> mOptional;

        public static <T> Optional2<T> of(T t) {
            return new Optional2<>(t);
        }

        private Optional2(T t) {
            this.mOptional = Optional.ofNullable(t);
        }

        public Optional2<T> ifPresent(Consumer<T> c) {
            mOptional.ifPresent(c);
            return this;
        }

        public Optional2<T> ifNotPresent(Runnable r) {
            if (!mOptional.isPresent()) r.run();
            return this;
        }
}
