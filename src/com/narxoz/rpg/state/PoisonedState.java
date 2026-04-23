package com.narxoz.rpg.state;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.state.HeroState;

public class PoisonedState implements HeroState{
    private static final int    POISON_DAMAGE_PER_TURN = 5;
    private static final double OUTGOING_PENALTY       = 0.75;

    private int turnsRemaining;

    public PoisonedState(int duration) {
        this.turnsRemaining = duration;
    }

    @Override
    public String getName() {
        return "Poisoned(" + turnsRemaining + ")";
    }

    @Override
    public int modifyOutgoingDamage(int basePower) {
        return (int) (basePower * OUTGOING_PENALTY);
    }

    @Override
    public int modifyIncomingDamage(int rawDamage) { return rawDamage; }

    @Override
    public void onTurnStart(Hero hero) {
        System.out.printf("  ☠ %s is poisoned — suffers %d poison damage!%n",
                hero.getName(), POISON_DAMAGE_PER_TURN);
        hero.receiveDamage(POISON_DAMAGE_PER_TURN);
    }

    @Override
    public void onTurnEnd(Hero hero) {
        turnsRemaining--;
        System.out.printf("  ☠ Poison on %s fades... (%d turn(s) left)%n",
                hero.getName(), turnsRemaining);
        if (turnsRemaining <= 0) {
            hero.setState(new NormalState());
        }
    }

    @Override
    public boolean canAct() { return true; }
}
