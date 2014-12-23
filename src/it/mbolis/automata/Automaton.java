package it.mbolis.automata;

import static it.mbolis.automata.Utils.indexOf;

public abstract class Automaton<C extends Automaton<C>> {

    protected final State[] states;
    protected State state;

    public Automaton() {
        states = states();
        state = states[0];
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        if (indexOf(states, state) < 0) {
            throw new IllegalArgumentException(state.toString());
        }
        this.state = state;
    }

    protected abstract State[] states();

    public abstract C evolve(C[][] neighbors);

    public int neighborhood() {
        return 1;
    }

    public char print() {
        return state.icon;
    }
}