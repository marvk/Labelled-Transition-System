package net.marvk.lts.model.ctl;


import net.marvk.lts.model.*;
import net.marvk.lts.model.ctl.ctlLexer.CTLLexemeTokenType;
import net.marvk.lts.model.ctl.ctlLexer.CTLLexer;
import net.marvk.lts.model.ctl.ctlLexer.CTLSymbolTokenType;
import net.marvk.lts.model.ctl.ctlLexer.CTLToken;
import net.marvk.lts.model.ctl.ctlParser.CTLNode;
import net.marvk.lts.model.ctl.ctlParser.CTLParser;

import java.util.*;

public class CTL {

    private final String formula;

    private final CTLNode rootNode;

    public CTL(final String formula) {
        this.formula = Objects.requireNonNull(formula.trim());
        List<CTLToken> tokenList = CTLLexer.tokenize(this.formula);
        this.rootNode = CTLParser.startParsing(tokenList);
    }

    public String getFormula() {
        return formula;
    }

    public CTLNode getRootNode() {
        return rootNode;
    }

    public boolean check(LabeledTransitionSystem lts) {

        final KripkeStructure ks = new KripkeStructure(lts);

        boolean result = true;

        for (State startState : ks.getInitialStates()) {
            result = result && checkFromState(startState, ks, rootNode);
        }

        return result;
    }

    private boolean checkFromState(State state, KripkeStructure ks, CTLNode node) {
        if (node.getToken().getType().equals( CTLSymbolTokenType.OR)) {
            return checkOR(state, ks, node);
        } else if (node.getToken().getType().equals( CTLSymbolTokenType.AND)) {
            return checkAND(state, ks, node);
        } else if (node.getToken().getType().equals( CTLSymbolTokenType.NOT)) {
            return checkNOT(state, ks, node);
        } else if (node.getToken().getType().equals( CTLSymbolTokenType.ONE) ){
            return true;
        } else if (node.getToken().getType().equals( CTLSymbolTokenType.ZERO) ){
            return false;
        } else if (node.getToken().getType().equals( CTLLexemeTokenType.IDENTIFIER) ){
            return checkIDENTIFIER(state, ks, node);
        } else {
            assert (node.getToken().getType().equals( CTLSymbolTokenType.E));
            CTLNode child = node.getChildren().get(0);
            if (child.getToken().getType().equals( CTLSymbolTokenType.X) ){
                return checkEXForState(state, ks, child);
            } else if (child.getToken().getType().equals( CTLSymbolTokenType.G)) {
                return checkEGForState(state, ks, child);
            } else {
                assert (child.getToken().getType().equals(CTLSymbolTokenType.U));

                return checkEUForState(state, ks, child);
            }
        }

    }

    private boolean checkEUForState(State state, KripkeStructure ks, CTLNode uNode) {
        return checkEU(ks, uNode).contains(state);
    }

    private Collection<State> checkEU(KripkeStructure ks, CTLNode uNode) {
        Collection<State> result = new HashSet<>();
        CTLNode expressionA = uNode.getChildren().get(0);
        CTLNode expressionB = uNode.getChildren().get(1);
        Collection<State> satisfyB = getAllStatesSatisfied(ks, expressionB);

        return navigateBackwardsFromStates(expressionA, satisfyB, ks);


    }

    private Collection<State> navigateBackwardsFromStates(CTLNode expression, Collection<State> startStates, KripkeStructure ks) {
        Collection<State> result = new HashSet<>();
        Set<State> visited = new HashSet<>();
        Stack<State> stack = new Stack<>();

        for (State start : startStates) {
            for(State s: getAllPredecessorStates(start, ks)){
                stack.push(s);
            }
            result.add(start);
        }

        while (!stack.empty()) {
            State currentState = stack.pop();
            visited.add(currentState);
            if (checkFromState(currentState, ks, expression)) {
                result.add(currentState);
                for (State predecessor : getAllPredecessorStates(currentState, ks)) {
                    if (!visited.contains(predecessor)) {
                        stack.push(predecessor);
                    }
                }
            }
        }

        return result;
    }

    private Collection<State> getAllPredecessorStates(State state, KripkeStructure ks) {
        Collection<State> result = new HashSet<>();

        for (KSTransition t : ks.getTransitionRelation()) {
            if (t.getGoalState().equals(state)) {
                result.add(t.getStartState());
            }
        }

        return result;

    }

    private boolean checkEGForState(State state, KripkeStructure ks, CTLNode gNode) {
        return checkEG(ks, gNode).contains(state);
    }

    private Collection<State> checkEG(KripkeStructure ks, CTLNode gNode) {
        Collection<State> result = new HashSet<>();
        CTLNode expression = gNode.getChildren().get(0);
        Collection<State> satisfyExpression = getAllStatesSatisfied(ks, expression);

        Collection<Collection<State>> scc = getStronglyConnectedComponents(ks);
        for (Collection<State> component : scc) {
            result.addAll(component);
        }

        return getReachables(ks, result);

    }

    private Collection<State> getReachables(KripkeStructure ks, Collection<State> states) {
        Collection<State> result = new HashSet<>();

        for (State s : ks.getStates()) {
            if (isReachable(s, states, ks)) {
                result.add(s);
            }
        }

        return result;

    }

    private boolean isReachable(State s, Collection<State> states, KripkeStructure ks) {
        Stack<State> stack = new Stack<>();
        Set<State> visited = new HashSet<>();
        stack.push(s);

        while (!stack.empty()) {
            State currentState = stack.pop();
            visited.add(currentState);
            for (State successor : getAllSuccessorStates(currentState, ks)) {
                stack.push(successor);
            }

            if (states.contains(currentState)) {
                return true;
            }
        }

        return false;
    }

    private Collection<State> getAllSuccessorStates(State state, KripkeStructure ks) {
        Collection<State> result = new HashSet<>();

        for (KSTransition t : ks.getTransitionRelation()) {
            if (t.getStartState().equals(state)) {
                result.add(t.getGoalState());
            }
        }

        return result;
    }

    private Collection<Collection<State>> getStronglyConnectedComponents(KripkeStructure ks) {
        Collection<Collection<State>> result = new HashSet<>();

        Map<State, Integer> disc = new HashMap<>();

        Map<State, Integer> low = new HashMap<>();

        Set<State> stackMembership = new HashSet<>();

        Stack<State> stack = new Stack<>();

        for (State s : ks.getStates()) {
            if (!disc.containsKey(s)) {
                Collection<Collection<State>> temp = getSCCUtil(ks, s, low, disc, stackMembership, stack, 0);
                result.addAll(temp);
            }
        }


        return result;
    }

    private Collection<Collection<State>> getSCCUtil(KripkeStructure ks, State state, Map<State, Integer> low, Map<State, Integer> disc, Set<State> stackMembership, Stack<State> stack, int lowIndex) {
        Collection<Collection<State>> result = new HashSet<>();

        disc.put(state, lowIndex);
        low.put(state, lowIndex);
        lowIndex++;
        stack.push(state);
        stackMembership.add(state);

        for (KSTransition t : ks.getTransitionRelation()) {
            if (t.getStartState().equals(state)) {
                State goalState = t.getGoalState();
                if (!disc.containsKey(goalState)) {
                    result.addAll(getSCCUtil(ks, goalState, low, disc, stackMembership, stack, lowIndex));
                    low.put(state, Math.min(low.get(state), low.get(goalState)));
                } else if (stackMembership.contains(goalState)) {
                    low.put(state, Math.min(low.get(state), disc.get(goalState)));
                }
            }

        }
        State w = null;
        if (low.get(state).equals(disc.get(state))) {
            Collection<State> component = new HashSet<>();
            while (!state.equals(w)) {
                w = stack.pop();
                component.add(w);
                stackMembership.remove(w);
            }

            if (!component.isEmpty() && (component.size() != 1 || checkSelfRelation(component.iterator().next(), ks))) {
                result.add(component);
            }

        }


        return result;
    }

    private boolean checkSelfRelation(State state, KripkeStructure ks) {
        return ks.getTransitionRelation().contains(new KSTransition(state, state));
    }

    private Collection<State> getAllStatesSatisfied(KripkeStructure ks, CTLNode expression) {
        Collection<State> satisfyExpression = new HashSet<>();
        for (State s : ks.getStates()) {
            if (checkFromState(s, ks, expression)) {
                satisfyExpression.add(s);
            }
        }
        return satisfyExpression;
    }

    private boolean checkEXForState(State state, KripkeStructure ks, CTLNode xNode) {
        return checkEX(ks, xNode).contains(state);
    }


    private Collection<State> checkEX(KripkeStructure ks, CTLNode xNode) {
        Collection<State> result = new HashSet<>();
        CTLNode expression = xNode.getChildren().get(0);
        Collection<State> satisfyExpression = getAllStatesSatisfied(ks, expression);

        for (KSTransition t : ks.getTransitionRelation()) {
            if (satisfyExpression.contains(t.getGoalState())) {
                result.add(t.getStartState());
            }
        }

        return result;
    }

    private boolean checkIDENTIFIER(State state, KripkeStructure ks, CTLNode identNode) {
        return ks.getLabelingFunction().get(state).stream().filter(ap -> ap.toString().equals(identNode.getToken().getLexeme())).findFirst().orElse(null) != null;
    }

    private boolean checkNOT(State state, KripkeStructure ks, CTLNode notNode) {
        return !checkFromState(state, ks, notNode.getChildren().get(0));
    }

    private boolean checkOR(State state, KripkeStructure ks, CTLNode orNode) {
        boolean result = false;

        for (CTLNode child : orNode.getChildren()) {
            result = result || checkFromState(state, ks, child);
        }
        return result;
    }

    private boolean checkAND(State state, KripkeStructure ks, CTLNode andNode) {
        boolean result = true;

        for (CTLNode child : andNode.getChildren()) {
            result = result && checkFromState(state, ks, child);
        }
        return result;
    }



}
