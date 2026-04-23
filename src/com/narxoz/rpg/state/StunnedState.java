package com.narxoz.rpg.state;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.state.HeroState;

public class StunnedState implements HeroState{
    private static final double INCOMING_MULTIPLIER = 1.5;

    private int turnsRemaining;

    public StunnedState(int duration) {
        this.turnsRemaining = duration;
    }

    @Override
    public String getName() {
        return "Stunned(" + turnsRemaining + ")";
    }

    @Override
    public int modifyOutgoingDamage(int basePower) {
        return 0;
    }

    @Override
    public int modifyIncomingDamage(int rawDamage) {
        return (int) (rawDamage * INCOMING_MULTIPLIER);
    }

    @Override
    public void onTurnStart(Hero hero) {
        System.out.printf("  ⚡ %s is STUNNED and cannot act!%n", hero.getName());
    }

    @Override
    public void onTurnEnd(Hero hero) {
        turnsRemaining--;
        if (turnsRemaining <= 0) {
            System.out.printf("  ⚡ %s shakes off the stun!%n", hero.getName());
            hero.setState(new NormalState());
        }
    }

    @Override
    public boolean canAct() { return false; }
}
