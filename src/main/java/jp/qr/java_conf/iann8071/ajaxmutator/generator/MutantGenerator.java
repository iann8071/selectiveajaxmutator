package jp.qr.java_conf.iann8071.ajaxmutator.generator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import jp.qr.java_conf.iann8071.ajaxmutator.context.Context2;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutant.Mutant;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.*;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.parser.Parser2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by iann8071 on 2015/04/22.
 */
public class MutantGenerator {
    List<NodeVisitor2> mVisitors = new ArrayList();

    public static MutantGenerator of() {
        return new MutantGenerator();
    }

    public void addVisitor(NodeVisitor2 visitor) {
        mVisitors.add(visitor);
    }

    public List<Mutant> doGenerate() {
        mVisitors.stream().forEach(visitor -> Context2.jsOriginalFiles().stream().forEach(file -> Parser2.parse(file).visit(visitor)));
        return mVisitors.stream().map(v -> v.mutants()).reduce((ms, ms1) -> ImmutableList.<Mutant>builder().addAll(ms).addAll(ms1).build()).get().stream().map(original -> mVisitors.stream().map(v -> v.addInfoToMutant(original))
                .collect(Collectors.toList())).reduce((ms, ms1) -> ImmutableList.<Mutant>builder().addAll(ms).addAll(ms1).build()).get();
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        MutantGenerator mGenerator = MutantGenerator.of();

        public MutantGenerator build() {
            return mGenerator;
        }

        public Builder addEventCallbackRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1).ifFunctionOf(Mutator.ADD_EVENT_LISTENER, 2).addOperator("ECR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1).ifFunctionOf(Mutator.ATTACH_EVENT, 2).addOperator("ECR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).ifFunctionOf(Mutator.JQUERY_EVENTS, 0, Integer.MAX_VALUE).addOperator("ECR"));
            mGenerator.addVisitor(Replacer.ofFunctionLastArgument().ifFunctionOf(Mutator.JQUERY_EVENT_ATTACHERS).addOperator("ECR"));
            return this;
        }

        public Builder addEventTargetRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionTarget().ifFunctionOf(Mutator.ADD_EVENT_LISTENER, 2).addOperator("ETaR"));
            mGenerator.addVisitor(Replacer.ofFunctionTarget().ifFunctionOf(Mutator.ATTACH_EVENT, 2).addOperator("ETaR"));
            mGenerator.addVisitor(Replacer.ofFunctionTarget().ifFunctionOf(Mutator.JQUERY_EVENTS, 0, Integer.MAX_VALUE).addOperator("ETaR"));
            mGenerator.addVisitor(Replacer.ofFunctionTarget().ifFunctionOf(Mutator.JQUERY_EVENT_ATTACHERS).addOperator("ETaR"));
            return this;
        }

        public Builder addEventTypeRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).ifFunctionOf(Mutator.ADD_EVENT_LISTENER, 2).addOperator("ETyR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).ifFunctionOf(Mutator.ATTACH_EVENT, 2).addOperator("ETyR"));
            mGenerator.addVisitor(Replacer.ofFunctionProperty().ifFunctionOf(Mutator.JQUERY_EVENTS, 0, Integer.MAX_VALUE).addOperator("ETyR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).ifFunctionOf(Mutator.JQUERY_EVENT_ATTACHERS).addOperator("ETyR"));
            return this;
        }

        public Builder addRequestMethodRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1, "type").addCandidates(ImmutableList.of("POST", "GET")).addFilter(ImmutableList.of("POST", "GET")).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 1).addOperator("RMR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1, "method").addCandidates(ImmutableList.of("POST", "GET")).addFilter(ImmutableList.of("POST", "GET")).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 1).addOperator("RMR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0, "type").addCandidates(ImmutableList.of("POST", "GET")).addFilter(ImmutableList.of("POST", "GET")).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 0).addOperator("RMR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0, "method").addCandidates(ImmutableList.of("POST", "GET")).addFilter(ImmutableList.of("POST", "GET")).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 0).addOperator("RMR"));
            mGenerator.addVisitor(Replacer.ofFunctionProperty().addCandidates(ImmutableList.of("post", "get")).addFilter(ImmutableList.of("post", "get")).ifFunctionOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_POST).ifFunctionOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_GET).addOperator("RMR"));
            return this;
        }

        public Builder addRequestOnSuccessHandlerRAMutator() {
            List<String> sharedCandidates = new ArrayList();
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1, "success").setCandidates(sharedCandidates).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 1).addOperator("RSR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0, "success").setCandidates(sharedCandidates).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 0).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_GET, 0).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_POST, 0).addOperator("RSR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(2).setCandidates(sharedCandidates).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_GET, 2).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_POST, 2).addOperator("RSR"));
            return this;
        }

        public Builder addRequestUrlRAMutator() {
            List<String> sharedCandidates = new ArrayList();
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).setCandidates(sharedCandidates).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 1).addOperator("RSR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0, "url").setCandidates(sharedCandidates).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 0).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_GET, 0).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_GET, 0).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_POST, 0).addOperator("RSR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).setCandidates(sharedCandidates).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_GET, 1).ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_POST, 1).addOperator("RSR"));
            return this;
        }

        public Builder addFakeBlankResponseBodyMutator() {
            return this;
        }

        public Builder addReplacingAjaxCallbackMutator() {
            mGenerator.addVisitor(FunctionArgumentSwapper.of(1, "success", "error").ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, Mutator.JQUERY_AJAX, 1).addOperator("RCR"));
            mGenerator.addVisitor(FunctionArgumentSwapper.of(0, "success", "error").ifObjectPropertyArgumentOf(Mutator.JQUERY_OBJECTS, ImmutableSet.of(Mutator.JQUERY_AJAX, Mutator.JQUERY_GET, Mutator.JQUERY_POST), 0).addOperator("RCR"));
            return this;
        }

        public Builder addTimerEventCallbackRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).ifFunctionOf(Mutator.SET_TIMEOUT).ifFunctionOf(Mutator.SET_INTERVAL).addOperator("TCR"));
            return this;
        }

        public Builder addTimerEventDurationRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1).ifFunctionOf(Mutator.SET_TIMEOUT).ifFunctionOf(Mutator.SET_INTERVAL).addOperator("TIR"));
            return this;
        }

        public Builder addDOMSelectionSelectNearbyMutator() {
            mGenerator.addVisitor(Adder.of(ImmutableList.of(Mutator.FIRST_CHILD_PROPERTY, Mutator.PARENT_PROPERTY)).ifFunctionOf(ImmutableSet.of(Mutator.GET_ELEMENT_BY_ID, Mutator.GET_ELEMENT_BY_CLASSNAME, Mutator.GET_ELEMENT_BY_NAME, Mutator.GET_ELEMENT_BY_TAG_NAME)));
            mGenerator.addVisitor(Adder.of(ImmutableList.of(Mutator.JQUERY_FIRST_CHILD_CALL, Mutator.JQUERY_PARENT_CALL)).ifFunctionOf(ImmutableSet.<String>builder().add((Mutator.JQUERY_CHILDREN)).addAll(Mutator.JQUERY_OBJECTS).build()));
            return this;
        }

        public Builder addAttributeModificationTargetRAMutator() {
            List<String> sharedCandidates = new ArrayList();
            mGenerator.addVisitor(Replacer.ofAssignmentTarget().setCandidates(sharedCandidates).ifAttributeAssignmentOf(Mutator.HTML_ATTRIBUTES).addOperator("AtR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).setCandidates(sharedCandidates).ifFunctionOf(ImmutableSet.of(Mutator.JQUERY_ATTR, Mutator.SET_ATTRIBUTE), 2).addOperator("AtR"));
            mGenerator.addVisitor(Replacer.ofFunctionProperty().ifFunctionOf(ImmutableSet.of(Mutator.JQUERY_TEXT, Mutator.JQUERY_HEIGHT, Mutator.JQUERY_WIDTH), 1).addOperator("AtR"));
            return this;
        }

        public Builder addAttributeModificationValueRAMutator() {
            List<String> sharedCandidates = new ArrayList();
            mGenerator.addVisitor(Replacer.ofAssignmentValue().setCandidates(sharedCandidates).ifAttributeAssignmentOf(Mutator.HTML_ATTRIBUTES).addOperator("AvR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(1).setCandidates(sharedCandidates).ifFunctionOf(ImmutableSet.of(Mutator.JQUERY_ATTR, Mutator.SET_ATTRIBUTE), 2).addOperator("AvR"));
            mGenerator.addVisitor(Replacer.ofFunctionArguments(0).ifFunctionOf(ImmutableSet.of(Mutator.JQUERY_TEXT, Mutator.JQUERY_HEIGHT, Mutator.JQUERY_WIDTH), 1).addOperator("AvR"));
            return this;
        }

        public Builder addAppendedDOMRAMutator() {
            mGenerator.addVisitor(Replacer.ofFunctionTarget().ifFunctionOf(Mutator.APPEND_CHILD, 1).addOperator("ADR"));
            return this;
        }

        public Builder addDOMCloningToNoOpMutator() {
            mGenerator.addVisitor(FunctionCallRemover.of().ifFunctionOf(Mutator.CLONE).addOperator("RDCI"));
            return this;
        }

        public Builder addDOMCreationToNoOpMutator() {
            mGenerator.addVisitor(FunctionCallRemover.of().ifFunctionOf(Mutator.CREATE_ELEMENT).addOperator("RDCr"));
            return this;
        }

        public Builder addDOMNormalizationToNoOpMutator() {
            mGenerator.addVisitor(FunctionCallRemover.of().ifFunctionOf(Mutator.NORMALIZE).addOperator("RDN"));
            return this;
        }

        public Builder addDOMRemovalToNoOpMutator() {
            mGenerator.addVisitor(FunctionCallRemover.of().ifFunctionOf(Mutator.REMOVE_CHILD).addOperator("RDR"));
            return this;
        }

        public Builder addDOMReplacementSrcTargetMutator() {
            mGenerator.addVisitor(FunctionArgumentSwapper.of(0, 1).ifFunctionOf("replaceChild", 2).addOperator("ERE"));
            return this;
        }

        public Builder addJsSpecificMutators() {
            return  addAppendedDOMRAMutator()
                    .addAttributeModificationTargetRAMutator()
                    .addAttributeModificationValueRAMutator()
                    .addDOMCloningToNoOpMutator()
                    .addDOMCreationToNoOpMutator()
                    .addDOMNormalizationToNoOpMutator()
                    .addDOMRemovalToNoOpMutator()
                    .addDOMReplacementSrcTargetMutator()
                    . addDOMSelectionSelectNearbyMutator()
                    .addEventCallbackRAMutator()
                    .addEventTypeRAMutator()
                    .addEventTargetRAMutator()
                    .addFakeBlankResponseBodyMutator()
                    .addReplacingAjaxCallbackMutator()
                    .addRequestMethodRAMutator()
                    .addRequestOnSuccessHandlerRAMutator()
                    .addRequestUrlRAMutator()
                    .addTimerEventCallbackRAMutator()
                    .addTimerEventDurationRAMutator();
        }
    }
}
