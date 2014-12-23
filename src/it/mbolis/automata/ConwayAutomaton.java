package it.mbolis.automata;

import static java.awt.Color.blue;
import static java.awt.Color.white;

public final class ConwayAutomaton extends Automaton<ConwayAutomaton> {

    public static final State DEAD = new State("dead", ' ', white);
    public static final State ALIVE = new State("alive", 'o', blue);

    @Override
    protected State[] states() {
        return new State[] { DEAD, ALIVE };
    }

    @Override
    public ConwayAutomaton evolve(ConwayAutomaton[][] neighbors) {
        int alive = 0;
        for (ConwayAutomaton[] row : neighbors) {
            for (ConwayAutomaton cell : row) {
                if (cell.state == ALIVE) {
                    alive++;
                }
            }
        }

        ConwayAutomaton next = new ConwayAutomaton();
        if (state == DEAD) {
            switch (alive) {
            case 3:
                next.state = ALIVE;
            default:
            }
        } else if (state == ALIVE) {
            switch (alive) {
            case 3:
            case 4:
                next.state = ALIVE;
            default:
            }
        }
        return next;
    }
}