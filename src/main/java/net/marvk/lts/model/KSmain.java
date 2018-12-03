package net.marvk.lts.model;

import net.marvk.lts.model.ctl.CTL;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class KSmain {
    public static void main(String args[]){
        System.out.println("Hello");

        final State off = new State("off");
        final State low = new State("low");
        final State high = new State("high");

        final State rel = new State("rel");
        final State pr = new State("pr");


        final Symbol p = new Symbol("p");
        final Symbol h = new Symbol("h");
        final Symbol r = new Symbol("r");


        Set<KSTransition> ksTransitions = new HashSet<>();
        ksTransitions.add(new KSTransition(off, low));
        ksTransitions.add(new KSTransition(low, off));
        ksTransitions.add(new KSTransition(low, high));
        ksTransitions.add(new KSTransition(high, off));

        Set<State> states = new HashSet<>();
        states.add(off);
        states.add(low);
        states.add(high);

        Set<State> initialStates = new HashSet<>();
        initialStates.add(off);

        HashSet<AtomicProposition> apList = new HashSet<>();
        apList.add(new AtomicProposition("lightOn"));
        apList.add(new AtomicProposition("highBattUse"));

        Collection<AtomicProposition> apLow = new HashSet<>();
        apLow.add(new AtomicProposition("lightOn"));
        Collection<AtomicProposition> apHigh = new HashSet<>();
        apHigh.add(new AtomicProposition("lightOn"));
        apHigh.add(new AtomicProposition("highBattUse"));


        HashMap<State, Set<AtomicProposition>> labelingFunction = new HashMap<>();
        labelingFunction.put(off, new HashSet<>());
        labelingFunction.put(low, Set.copyOf(apLow));
        labelingFunction.put(high, Set.copyOf(apHigh));

        final LabeledTransitionSystem lamp = new LabeledTransitionSystem(off, apList, labelingFunction,
                new Transition(off, p, low),
                new Transition(low, p, off),
                new Transition(low, h, high),
                new Transition(high, p, off)
        );

        final LabeledTransitionSystem button = new LabeledTransitionSystem(rel,
                new Transition(rel, p, pr),
                new Transition(pr, r, rel),
                new Transition(rel, h, rel)
        );

        final KripkeStructure lampKS = new KripkeStructure("lamp", states, initialStates,
                ksTransitions, labelingFunction);

        System.out.println("-----Created KS----");
        System.out.println(lampKS.checkLeftTotality(ksTransitions) ? "This transition relation is left-total"
                : "This transition relation is not left-total");

        CTL ctl1 = new CTL("lightOn∨E[EX lightOn U lightOn]");
        System.out.print("expression " + ctl1.getFormula() + "is " + (ctl1.check(lamp) ? "true": false));

    }
}
