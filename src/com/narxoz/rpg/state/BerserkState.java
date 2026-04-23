package com.narxoz.rpg.state;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.state.HeroState;

public class BerserkState implements HeroState{
    private static final double OUTGOING_MULTIPLIER = 2.0;
    private static final double INCOMING_MULTIPLIER = 1.3;
    private static final double CALM_THRESHOLD      = 0.60;

    @Override
    public String getName() { return "Berserk"; }

    @Override
    public int modifyOutgoingDamage(int basePower) {
        return (int) (basePower * OUTGOING_MULTIPLIER);
    }

    @Override
    public int modifyIncomingDamage(int rawDamage) {
        return (int) (rawDamage * INCOMING_MULTIPLIER);
    }

    @Override
    public void onTurnStart(Hero hero) {
        double hpRatio = (double) hero.getHp() / hero.getMaxHp();
        if (hpRatio >= CALM_THRESHOLD) {
            System.out.printf("  🔥 %s's rage subsides as their wounds close (HP %.0f%%)!%n",
                    hero.getName(), hpRatio * 100);
            hero.setState(new NormalState());
        } else {
            System.out.printf("  🔥 %s rages! (HP %.0f%% — berserk threshold active)%n",
                    hero.getName(), hpRatio * 100);
        }
    }

    @Override
    public void onTurnEnd(Hero hero) { /* no countdown — HP-driven */ }

    @Override
    public boolean canAct() { return true; }
}
