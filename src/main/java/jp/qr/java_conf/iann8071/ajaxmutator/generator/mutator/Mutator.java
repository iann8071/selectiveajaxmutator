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
import java.util.Optional;
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
    public static final Set<String> JQUERY_OBJECTS = ImmutableSet.of("jQuery", "$");

    public static final Set<String> JQUERY_EVENT_ATTACHERS = ImmutableSet.of("bind", "on", "one", "live");
    public static final String ADD_EVENT_LISTENER = "addEventListener";
    public static final String ATTACH_EVENT = "attachEvent";
    public static final String SET_TIMEOUT = "setTimeout";
    public static final String SET_INTERVAL = "setInterval";
    public static final String JQUERY_AJAX = "ajax";
    public static final String JQUERY_GET = "get";
    public static final String JQUERY_POST = "post";
    public static final String JQUERY_ATTR = "attr";
    public static final String SET_ATTRIBUTE = "setAttribute";
    public static final String JQUERY_TEXT = "text";
    public static final String JQUERY_HEIGHT = "height";
    public static final String JQUERY_WIDTH = "width";
    public static final String APPEND_CHILD = "appendChild";
    public static final String REPLACE_CHILD = "replaceChild";
    public static final String CLONE = "cloneNode";
    public static final String NORMALIZE = "normalize";
    public static final String REMOVE_CHILD = "removeChild";
    public static final String CREATE_ELEMENT = "createElement";
    public static final String PARENT_PROPERTY = "parentNode";
    public static final String FIRST_CHILD_PROPERTY = "children[0]";
    public static final String GET_ELEMENT_BY_ID = "getElementById";
    public static final String GET_ELEMENT_BY_CLASSNAME = "getElementsByClassName";
    public static final String GET_ELEMENT_BY_TAG_NAME = "getElementsByTagName";
    public static final String GET_ELEMENT_BY_NAME = "getElementsByName";
    public static final String JQUERY_PARENT_CALL = "parent()";
    public static final String JQUERY_FIRST_CHILD_CALL = "children(:first)";
    public static final String JQUERY_CHILDREN = "children";

    private String mOperator;
    private Predicate<AstNode> mIsApplicable = node -> false;

    @Override
    public boolean visit(AstNode node) {
        System.out.println(node.getClass());
        System.out.println(node.toSource());
        if(node.getParent() != null) {
            System.out.println("parent : " + node.getParent().getClass());
        }
        if(mIsApplicable.test(node)) {
            collect(node);
            mutants().add(Mutant.builder().put(node).build());
        }
        return true;
    }

    protected void collect(AstNode node) {}

    public Mutant addInfoToMutant(Mutant mutant) {
        if (mIsApplicable.test(mutant.node())) {
            File originalFile = new File(mutant.node().getAstRoot().getSourceName());
            Files2.write(mutant.node().getAstRoot().toSource(), originalFile);
            mutateNode(mutant.node());
            String mutatedCode = mutant.node().getAstRoot().toSource();
            Files2.write(mutatedCode, Context2.jsNewMutantFile(originalFile));
            restoreNode(mutant.node());
            mutant.put(Mutant.MUTANT, mutatedCode);
            mutant.put(Mutant.OPERATOR, mOperator);
        }
        return mutant;
    }

    abstract protected void mutateNode(AstNode node);

    abstract protected void restoreNode(AstNode node);

    public Mutator addOperator(String operator){
        mOperator = operator;
        return this;
    }

    public Mutator ifFunctionOf(String id) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id));
        return this;
    }

    public Mutator ifObjectPropertyArgumentOf(String id, int num) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id) && isArgumentObjectProperty(node, num));
        return this;
    }

    public Mutator ifObjectPropertyArgumentOf(String targetName, String id, int num) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id) && isArgumentObjectProperty(node, num) && isFunctionName(node, targetName));
        return this;
    }

    public Mutator ifObjectPropertyArgumentOf(Set<String> targetNames, String id, int num) {
        mIsApplicable = mIsApplicable.or(node -> targetNames.stream().map(name -> isFunction(node, id) && isArgumentObjectProperty(node, num) && isFunctionName(node, name)).filter(
                r -> r == true).findFirst().get());
        return this;
    }

    public Mutator ifObjectPropertyArgumentOf(Set<String> targetNames, Set<String> ids, int num) {
        mIsApplicable = mIsApplicable.or(node -> targetNames.stream().map(name -> ids.stream().map(id -> isFunction(node, id) && isArgumentObjectProperty(node, num) && isFunctionName(node, name)).filter(
                r -> r == true).findFirst().get()).findFirst().get());
        return this;
    }

    public Mutator ifAttributeAssignmentOf(Set<String> ids) {
        mIsApplicable = mIsApplicable.or(node -> isAssignmentPropertyAttribute(node, ids) && isAssignmentElementAttribute(node, ids));
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

    public Mutator ifFunctionOf(String targetName, String id, int lower, int upper) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id, lower, upper) && isFunctionName(node, targetName));
        return this;
    }

    public Mutator ifFunctionOf(Set<String> targetNames, String id, int lower, int upper) {
        mIsApplicable = mIsApplicable.or(node -> targetNames.stream().map(name -> isFunction(node, id, lower, upper) && isFunctionName(node, name)).filter(
                r -> r == true).findFirst().get());
        return this;
    }

    public Mutator ifFunctionOf(Set<String> targetNames, String id, int size) {
        mIsApplicable = mIsApplicable.or(node -> targetNames.stream().map(name -> isFunction(node, id, size) && isFunctionName(node, name)).filter(
                r -> r == true).findFirst().get());
        return this;
    }

    public Mutator ifFunctionOf(Set<String> targetNames, String id) {
        mIsApplicable = mIsApplicable.or(node -> targetNames.stream().map(name -> isFunction(node, id) && isFunctionName(node, name)).filter(
                r -> r == true).findFirst().get());
        return this;
    }

    public Mutator ifFunctionOf(Set<String> targetNames, Set<String> ids, int lower, int upper) {
        mIsApplicable = mIsApplicable.or(node -> targetNames.stream().map(name -> ids.stream().map(id -> isFunction(node, id, lower, upper) && isFunctionName(node, name)).filter(
                r -> r == true).findFirst().get()).findFirst().get());
        return this;
    }

    public Mutator ifFunctionOf(Set<String> ids) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, ids));
        return this;
    }

    public Mutator ifFunctionOf(String targetName, String id, int size) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id, size) && isFunctionName(node, targetName));
        return this;
    }

    public Mutator ifFunctionOf(String targetName, String id) {
        mIsApplicable = mIsApplicable.or(node -> isFunction(node, id) && isFunctionName(node, targetName));
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

    private boolean isArgumentObjectProperty(AstNode node, int num) {
        return isFunction(node) && Optional.of(((FunctionCall) node).getArguments().get(num)).orElse(new Name()) instanceof ObjectLiteral;
    }

    private boolean isFunction(AstNode node, String id) {
        return isFunctionIsProperty(node) && ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier().equals(id)
                || isNoTargetFunction(node) && ((Name) ((FunctionCall) node).getTarget()).getIdentifier().equals(id);
    }

    private boolean isFunction(AstNode node, String id, int size) {
        return isFunctionIsProperty(node) && ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier().equals(id)
                && ((FunctionCall) node).getArguments().size() == size;
    }

    private boolean isFunctionName(AstNode node, String targetName) {
        return ((FunctionCall)node).getTarget() instanceof Name && ((Name)((FunctionCall)node).getTarget()).getIdentifier().equals(targetName);
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

    private boolean isFunction(AstNode node, String id, int lower, int upper) {
        return isFunctionIsProperty(node) && id.equals(((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier())
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

    private boolean isAssignmentPropertyAttribute(AstNode node, Set<String> ids) {
        return isAssignmentProperty(node) && ids.contains(((PropertyGet) ((Assignment) node).getLeft()).getProperty().getIdentifier());
    }

    private boolean isAssignmentElement(AstNode node) {
        return isAssignment(node) && ((Assignment) node).getLeft() instanceof ElementGet;
    }

    private boolean isAssignmentElementString(AstNode node) {
        return isAssignmentElement(node) && ((ElementGet) ((Assignment) node).getLeft()).getElement() instanceof StringLiteral;
    }

    private boolean isAssignmentElementAttribute(AstNode node, Set<String> ids) {
        return isAssignmentElementString(node) && ids.contains(((StringLiteral) ((ElementGet) ((Assignment) node).getLeft()).getElement()).getValue());
    }
}
