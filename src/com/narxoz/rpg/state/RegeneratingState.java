package com.narxoz.rpg.state;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.state.HeroState;

public class RegeneratingState implements HeroState{
    private static final double OUTGOING_BONUS = 1.10;

    private final int healPerTurn;
    private int turnsRemaining;

    public RegeneratingState(int healPerTurn, int duration) {
        this.healPerTurn    = healPerTurn;
        this.turnsRemaining = duration;
    }

    @Override
    public String getName() {
        return "Regenerating(" + turnsRemaining + ")";
    }

    @Override
    public int modifyOutgoingDamage(int basePower) {
        return (int) (basePower * OUTGOING_BONUS);
    }

    @Override
    public int modifyIncomingDamage(int rawDamage) { return rawDamage; }

    @Override
    public void onTurnStart(Hero hero) {
        System.out.printf("  💚 %s regenerates %d HP!%n", hero.getName(), healPerTurn);
        hero.heal(healPerTurn);
    }

    @Override
    public void onTurnEnd(Hero hero) {
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            System.out.printf("  💚 %s's regeneration fades.%n", hero.getName());
            hero.setState(new NormalState());
        }
    }

    @Override
    public boolean canAct() { return true; }
}
