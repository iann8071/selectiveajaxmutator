package jp.qr.java_conf.iann8071.ajaxmutator.generator;

import com.google.common.collect.ImmutableList;
import jp.qr.java_conf.iann8071.ajaxmutator.context.Context2;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutant.Mutant;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.Mutator;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.NodeVisitor2;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.mutator.replacer.Replacer;
import jp.qr.java_conf.iann8071.ajaxmutator.generator.parser.Parser2;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Files2;
import jp.qr.java_conf.iann8071.ajaxmutator.util.Randomizer;
import org.mozilla.javascript.ast.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
        return mVisitors.stream().map(v->v.mutants()).reduce((ms, ms1) -> ImmutableList.<Mutant>builder().addAll(ms).addAll(ms1).build()).get().stream().map(original -> mVisitors.stream().map(v -> v.addInfoToMutant(original))
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
            mGenerator.addVisitor(Replacer.ofArguments(1).ifFunctionOf(Mutator.ADD_EVENT_LISTENER, 2).addOperator("ECR"));
            mGenerator.addVisitor(Replacer.ofArguments(1).ifFunctionOf(Mutator.ATTACH_EVENT, 2).addOperator("ECR"));
            mGenerator.addVisitor(Replacer.ofArguments(0).ifFunctionOf(Mutator.JQUERY_EVENTS, 0, Integer.MAX_VALUE).addOperator("ECR"));
            mGenerator.addVisitor(Replacer.ofArguments(Replacer.SIZE).ifFunctionOf(Mutator.JQUERY_EVENT_ATTACHERS).addOperator("ECR"));
            return this;
        }

        public Builder addEventTargetRAMutator() {
            mGenerator.addVisitor(Replacer.ofTargetNode().ifFunctionOf(Mutator.ADD_EVENT_LISTENER, 2).addOperator("ETaR"));
            mGenerator.addVisitor(Replacer.ofTargetNode().ifFunctionOf(Mutator.ATTACH_EVENT, 2).addOperator("ETaR"));
            mGenerator.addVisitor(Replacer.ofTargetNode().ifFunctionOf(Mutator.JQUERY_EVENTS, 0, Integer.MAX_VALUE).addOperator("ETaR"));
            mGenerator.addVisitor(Replacer.ofTargetNode().ifFunctionOf(Mutator.JQUERY_EVENT_ATTACHERS).addOperator("ETaR"));
            return this;
        }

        public Builder addEventTypeRAMutator() {
            mGenerator.addVisitor(Replacer.ofArguments(0).ifFunctionOf(Mutator.ADD_EVENT_LISTENER, 2).addOperator("ETyR"));
            mGenerator.addVisitor(Replacer.ofArguments(0).ifFunctionOf(Mutator.ATTACH_EVENT, 2).addOperator("ETyR"));
            mGenerator.addVisitor(Replacer.ofProperty().ifFunctionOf(Mutator.JQUERY_EVENTS, 0, Integer.MAX_VALUE).addOperator("ETyR"));
            mGenerator.addVisitor(Replacer.ofArguments(0).ifFunctionOf(Mutator.JQUERY_EVENT_ATTACHERS).addOperator("ETyR"));
            return this;
        }

        public Builder addRequestMethodRAMutator() {
            return this;
        }

        public Builder addRequestOnSuccessHandlerRAMutator() {
            return this;
        }

        public Builder addRequestUrlRAMutator() {
            return this;
        }

        public Builder addFakeBlankResponseBodyMutator() {
            return this;
        }

        public Builder addReplacingAjaxCallbackMutator() {
            return this;
        }

        public Builder addTimerEventCallbackRAMutator() {
            mGenerator.addVisitor(Replacer.ofArguments(0).ifFunctionOf(Mutator.SET_TIMEOUT).ifFunctionOf(Mutator.SET_INTERVAL).addOperator("TCR"));
            return this;
        }

        public Builder addTimerEventDurationRAMutator() {
            mGenerator.addVisitor(Replacer.ofArguments(1).ifFunctionOf(Mutator.SET_TIMEOUT).ifFunctionOf(Mutator.SET_INTERVAL).addOperator("TIR"));
            return this;
        }

        public Builder addDOMSelectionSelectNearbyMutator() {
            return this;
        }

        public Builder addAttributeModificationTargetRAMutator() {
            List<String> doubleArgumentCandidates = new ArrayList();
            List<String> singleArgumentCandidates = new ArrayList();

            Function<AstNode, AstNode> singleArgumentAttributeModificationCollector = node -> {
                String nameString = ((PropertyGet) ((FunctionCall) node).getTarget()).getProperty().getIdentifier();
                if (!singleArgumentCandidates.contains(nameString)) singleArgumentCandidates.add(nameString);
                mGenerator.addMutant(Mutant.builder().put(node).build());
                return node;
            };
            Function<Mutant, Mutant> singleArgumentAttributeModificationModifier = mutant -> {
                Name name = ((PropertyGet) ((FunctionCall) mutant.node()).getTarget()).getProperty();
                String nameString = name.getIdentifier();
                name.setIdentifier(Randomizer.differentString(singleArgumentCandidates, nameString));
                String original = name.getAstRoot().getSourceName();
                File mutantFile = Context2.jsNewMutantFile(original);
                Files2.write(name.getAstRoot().toSource(), mutantFile);
                name.setIdentifier(nameString);
                Files2.write(name.getAstRoot().toSource(), original);
                Files2.diff(original, mutantFile, Context2.jsNewDiffFile(original));
                return Mutant.builder().put(mutant).put("original", original).put("mutant", mutantFile.getName()).build();
            };

            Function<AstNode, AstNode> doubleArgumentAttributeModificationCollector = node -> {
                StringLiteral target = (StringLiteral) (((FunctionCall) node).getArguments().get(0));
                String targetString = target.getValue();
                if (!doubleArgumentCandidates.contains(targetString)) doubleArgumentCandidates.add(targetString);
                mGenerator.addMutant(Mutant.builder().put(node).build());
                return node;
            };
            Function<Mutant, Mutant> doubleArgumentAttributeModificationModifier = mutant -> {
                FunctionCall function = (FunctionCall) mutant.node();
                StringLiteral target = (StringLiteral) (function.getArguments().get(0));
                String targetString = target.getValue();
                target.setValue(Randomizer.differentString(doubleArgumentCandidates, targetString));
                function.setArguments(ImmutableList.of(target, function.getArguments().get(1)));
                String original = function.getAstRoot().getSourceName();
                File mutantFile = Context2.jsNewMutantFile(original);
                Files2.write(function.getAstRoot().toSource(), mutantFile);
                target.setValue(targetString);
                function.setArguments(ImmutableList.of(target, function.getArguments().get(1)));
                Files2.write(function.getAstRoot().toSource(), original);
                Files2.diff(original, mutantFile, Context2.jsNewDiffFile(original));
                return Mutant.builder().put(mutant).put("original", original).put("mutant", mutantFile.getName()).build();
            };

            mGenerator.addVisitor(NodeVisitor2.builder()
                    .ifAssignmentPropertyAttribute(
                            node -> {
                                String nameString = ((PropertyGet) ((Assignment) node).getLeft()).getProperty().getIdentifier();
                                if (!doubleArgumentCandidates.contains(nameString))
                                    doubleArgumentCandidates.add(nameString);
                                mGenerator.addMutant(Mutant.builder().put(node).build());
                                return node;
                            },
                            mutant -> {
                                Name name = ((PropertyGet) ((Assignment) mutant.node()).getLeft()).getProperty();
                                String nameString = name.getIdentifier();
                                name.setIdentifier(Randomizer.differentString(doubleArgumentCandidates, nameString));
                                String original = name.getAstRoot().getSourceName();
                                File mutantFile = Context2.jsNewMutantFile(original);
                                Files2.write(name.getAstRoot().toSource(), mutantFile);
                                name.setIdentifier(nameString);
                                Files2.write(name.getAstRoot().toSource(), original);
                                Files2.diff(original, mutantFile, Context2.jsNewDiffFile(original));
                                return Mutant.builder().put(mutant).put("original", original).put("mutant", mutantFile.getName()).build();
                            }
                    )
                    .ifAssignmentElementAttribute(
                            node -> {
                                String value = ((StringLiteral) ((ElementGet) ((Assignment) node).getLeft()).getElement()).getValue();
                                if (!doubleArgumentCandidates.contains(value)) doubleArgumentCandidates.add(value);
                                mGenerator.addMutant(Mutant.builder().put(node).build());
                                return node;
                            },
                            mutant -> {
                                StringLiteral literal = (StringLiteral) ((ElementGet) ((Assignment) mutant.node()).getLeft()).getElement();
                                String value = literal.getValue();
                                literal.setValue(Randomizer.differentString(doubleArgumentCandidates, value));
                                String original = literal.getAstRoot().getSourceName();
                                File mutantFile = Context2.jsNewMutantFile(original);
                                Files2.write(literal.getAstRoot().toSource(), mutantFile);
                                literal.setValue(value);
                                Files2.write(literal.getAstRoot().toSource(), original);
                                Files2.diff(original, mutantFile, Context2.jsNewDiffFile(original));
                                System.out.println(mutantFile);
                                return Mutant.builder().put(mutant).put("original", original).put("mutant", mutantFile.getName()).build();
                            }
                    )
                    .ifFirstArgumentIsLiteral("attr", 2, doubleArgumentAttributeModificationCollector, doubleArgumentAttributeModificationModifier)
                    .ifFirstArgumentIsLiteral("setAttribute", 2, doubleArgumentAttributeModificationCollector, doubleArgumentAttributeModificationModifier)
                    .ifFunction("text", 1, singleArgumentAttributeModificationCollector, singleArgumentAttributeModificationModifier)
                    .ifFunction("height", 1, singleArgumentAttributeModificationCollector, singleArgumentAttributeModificationModifier)
                    .ifFunction("width", 1, singleArgumentAttributeModificationCollector, singleArgumentAttributeModificationModifier)
                    .build());
            return this;
        }

        public Builder addAttributeModificationValueRAMutator() {
            return this;
        }

        public Builder addAppendedDOMRAMutator() {
            return this;
        }

        public Builder addDOMCloningToNoOpMutator() {
            return this;
        }

        public Builder addDOMCreationToNoOpMutator() {
            return this;
        }

        public Builder addDOMNormalizationToNoOpMutator() {
            return this;
        }

        public Builder addDOMRemovalToNoOpMutator() {
            return this;
        }

        public Builder addReplacingToNoOpMutator() {
            return this;
        }

        public Builder addDOMReplacementSrcTargetMutator() {
            Function<AstNode, AstNode> collector = node -> {
                mGenerator.addMutant(Mutant.builder().put(node).build());
                return node;
            };
            Function<Mutant, Mutant> mutator = mutant -> {
                FunctionCall function = (FunctionCall) mutant.node();
                AstNode source = function.getArguments().get(0);
                AstNode target = function.getArguments().get(1);
                function.setArguments(ImmutableList.of(target, source));
                String original = function.getAstRoot().getSourceName();
                File mutantFile = Context2.jsNewMutantFile(original);
                Files2.write(function.getAstRoot().toSource(), mutantFile);
                function.setArguments(ImmutableList.of(source, target));
                Files2.write(function.getAstRoot().toSource(), original);
                Files2.diff(original, mutantFile, Context2.jsNewDiffFile(original));
                return Mutant.builder().put(mutant).put("original", original).put("mutant", mutantFile.getName()).build();
            };
            mGenerator.addVisitor(NodeVisitor2.builder()
                    .ifFunction("replaceChild", 2, collector, mutator)
                    .build());
            return this;
        }

        public Builder addJsSpecificMutators() {
            return addAppendedDOMRAMutator()
                    .addAttributeModificationTargetRAMutator()
                    .addAttributeModificationValueRAMutator()
                    .addDOMCloningToNoOpMutator()
                    .addDOMCreationToNoOpMutator()
                    .addDOMNormalizationToNoOpMutator()
                    .addDOMRemovalToNoOpMutator()
                    .addDOMReplacementSrcTargetMutator()
                    .addDOMSelectionSelectNearbyMutator()
                    .addEventCallbackRAMutator()
                    .addEventTypeRAMutator()
                    .addEventTargetRAMutator()
                    .addFakeBlankResponseBodyMutator()
                    .addReplacingAjaxCallbackMutator()
                    .addRequestMethodRAMutator()
                    .addRequestOnSuccessHandlerRAMutator()
                    .addRequestUrlRAMutator()
                    .addReplacingToNoOpMutator()
                    .addTimerEventCallbackRAMutator()
                    .addTimerEventDurationRAMutator();
        }
    }
}
