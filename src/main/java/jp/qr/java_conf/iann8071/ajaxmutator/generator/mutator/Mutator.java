package jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import jp.qr.java_conf.iann8071.ajaxmutator.context.Context2;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutant.Mutant;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Files2;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Randomizer;
import org.mozilla.javascript.ast.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by iann8071 on 2015/04/25.
 */
abstract public class Mutator extends NodeVisitor2 {

    public static final Set<String> HTML_ATTRIBUTES = ImmutableSet.<String>of("abbr",
            "accept-charset", "accept", "action", "align", "alink", "alt",
            "archive", "axis", "background", "bgcolor", "border",
            "cellpadding", "cellspacing", "char", "charoff", "charset",
            "checked", "cite", "classid", "clear", "code", "codebase",
            "codetype", "color", "cols", "colspan", "compact", "content",
            "coords", "data", "datetime", "declare", "defer", "disabled",
            "enctype", "face", "for", "frame", "frameborder", "headers",
            "height", "href", "hreflang", "hspace", "http-equiv", "id",
            "ismap", "label", "language", "link", "longdesc", "marginheight",
            "marginwidth", "maxlength", "media", "method", "multiple", "name",
            "nohref", "noresize", "noshade", "nowrap", "object", "profile",
            "prompt", "readonly", "rel", "rev", "rows", "rowspan", "rules",
            "scheme", "scope", "scrolling", "selected", "shape", "size",
            "span", "src", "standby", "start", "summary", "target", "text",
            "type", "usemap", "valign", "value", "valuetype", "version",
            "vlink", "vspace", "width", "accessKey", "class", "dir", "id", "lang", "style", "tabindex",
            "title", "contenteditable", "contextmenu", "draggable", "dropzone",
            "hidden", "spellcheck");
    public static final Set<String> JQUERY_EVENTS = ImmutableSet.of("blur", "change",
            "click", "dblclick", "error", "focus", "keydown", "keypress",
            "keyup", "load", "mousedown", "mousemove", "mouseout", "mouseover",
            "mouseup", "resize", "scroll", "select", "submit", "unload");
    public static final Set<String> JQUERY_EVENT_ATTACHERS = ImmutableSet.of("bind", "on", "one", "live");
    public static final String ADD_EVENT_LISTENER = "addEventListener";
    public static final String ATTACH_EVENT = "attachEvent";
    public static final String SET_TIMEOUT = "setTimeout";
    public static final String SET_INTERVAL = "setInterval";

    private String mOperator;
    private Predicate<AstNode> mIsApplicable = node -> false;

    @Override
    public boolean visit(AstNode node) {
        return mIsApplicable.test(node) ? collect(node) : true;
    }

    abstract protected boolean collect(AstNode node);

    protected Mutant addInfoToMutant(Mutant mutant) {
        if (mIsApplicable.test(mutant.node())) {
            File originalFile = new File(mutant.node().getAstRoot().getSourceName());
            Files2.write(mutant.node().getAstRoot().toSource(), originalFile);
            String originalValue = mutateNode(mutant.node());
            String mutatedCode = mutant.node().getAstRoot().toSource();
            Files2.write(mutatedCode, Context2.jsNewMutantFile(originalFile));
            restoreNode(mutant.node(), originalValue);
            mutant.put(Mutant.MUTANT, mutatedCode);
            mutant.put(Mutant.OPERATOR, mOperator);
        }
        return mutant;
    }

    abstract protected String mutateNode(AstNode node);

    abstract protected void restoreNode(AstNode node, String originalValue);

    public Mutator addOperator(String operator){
        mOperator = operator;
        return this;
    }

    public Mutator ifFunctionOf(String id) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id));
        return this;
    }

    public Mutator ifFunctionOf(Set<String> ids, int size) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, ids, size));
        return this;
    }

    public Mutator ifFunctionOf(Set<String> ids, int lower, int upper) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, ids, lower, upper));
        return this;
    }

    public Mutator ifFunctionOf(Set<String> ids) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, ids));
        return this;
    }

    public Mutator ifFunctionOf(String id, int size) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id, size));
        return this;
    }

    private boolean isFunction(AstNode node) {
        return node instanceof FunctionCall;
    }

    private boolean isFunctionIsProperty(AstNode node) {
        return isFunction(node) && ((FunctionCall) node).getTarget() instanceof PropertyGet;
    }

    private boolean isNoTargetFunction(AstNode node) {
        return isFunction(node) && ((FunctionCall) node).getTarget() instanceof Name;
    }

    private boolean isFunction(AstNode node, String id) {
        return isFunctionIsProperty(node) && ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier().equals(id)
                || isNoTargetFunction(node) && ((Name) ((FunctionCall) node).getTarget()).getIdentifier().equals(id);
    }

    private boolean isFunction(AstNode node, String id, int size) {
        return isFunctionIsProperty(node) && ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier().equals(id)
                && ((FunctionCall) node).getArguments().size() == size;
    }

    private boolean isFunction(AstNode node, Set<String> ids, int size) {
        return isFunctionIsProperty(node) && ids.contains(((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier())
                && ((FunctionCall) node).getArguments().size() == size;
    }

    private boolean isFunction(AstNode node, Set<String> ids) {
        return isFunctionIsProperty(node) && ids.contains(((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier());
    }

    private boolean isFunction(AstNode node, Set<String> ids, int lower, int upper) {
        return isFunctionIsProperty(node) && ids.contains(((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier())
                && lower < ((FunctionCall) node).getArguments().size() && ((FunctionCall) node).getArguments().size() < upper;
    }

    private boolean isFirstArgumentLiteral(AstNode node, String id, int size) {
        return isFunction(node, id, size) && ((FunctionCall) node).getArguments().get(0) instanceof StringLiteral;
    }

    private boolean isSecondArgumentLiteral(AstNode node, String id, int size) {
        return isFunction(node, id, size) && ((FunctionCall) node).getArguments().get(1) instanceof StringLiteral;
    }

    private boolean isAssignment(AstNode node) {
        return node instanceof Assignment;
    }

    private boolean isAssignmentProperty(AstNode node) {
        return isAssignment(node) && ((Assignment) node).getLeft() instanceof PropertyGet;
    }

    private boolean isAssignmentPropertyAttribute(AstNode node) {
        return isAssignmentProperty(node) && isAttribute(((PropertyGet) ((Assignment) node).getLeft()).getProperty().getIdentifier());
    }

    private boolean isAssignmentElement(AstNode node) {
        return isAssignment(node) && ((Assignment) node).getLeft() instanceof ElementGet;
    }

    private boolean isAssignmentElementString(AstNode node) {
        return isAssignmentElement(node) && ((ElementGet) ((Assignment) node).getLeft()).getElement() instanceof StringLiteral;
    }

    private boolean isAssignmentElementAttribute(AstNode node) {
        return isAssignmentElementString(node) && isAttribute(((StringLiteral) ((ElementGet) ((Assignment) node).getLeft()).getElement()).getValue());
    }

    private boolean isAttribute(String attribute) {
        return HTML_ATTRIBUTES.contains(attribute);
    }
}
