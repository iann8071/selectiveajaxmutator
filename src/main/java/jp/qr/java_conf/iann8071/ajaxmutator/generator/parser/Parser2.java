package jp.qr.java_conf.iann8071.ajaxmutator.generator.parser;

import jp.qr.java_conf.iann8071.ajaxmutator.util.Files2;
import org.mozilla.javascript.*;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.tools.shell.Global;

import java.io.File;
import java.io.IOException;

/**
 * Created by iann8071 on 2015/04/23.
 */
public class Parser2 extends Parser{
    public static Parser2 of() {
            return new Parser2(new CompilerEnvirons());
    }

    private Parser2(CompilerEnvirons compilerEnvirons) {
        super(compilerEnvirons);
    }

    public AstNode parseFile(File file){
        try {
            return parse(Files2.newReader(file), file.getName(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AstNode parse(File file){
        return Parser2.of().parseFile(file);
    }
}
