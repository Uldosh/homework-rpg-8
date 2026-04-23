package com.narxoz.rpg.floor;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.combatant.Monster;
import com.narxoz.rpg.floor.FloorResult;
import com.narxoz.rpg.floor.TowerFloor;
import com.narxoz.rpg.state.PoisonedState;
import com.narxoz.rpg.state.StunnedState;
import com.narxoz.rpg.state.BerserkState;

import java.util.ArrayList;
import java.util.List;

public class CombatFloor extends TowerFloor{
    private final List<Monster> monsters;

    public CombatFloor(int floorNumber, String floorName, List<Monster> monsters) {
        super(floorNumber, floorName);
        this.monsters = new ArrayList<>(monsters);
    }

    @Override
    protected void setup(List<Hero> party) {
        System.out.println("  [SETUP] Monsters emerge from the shadows:");
        for (Monster m : monsters) {
            System.out.println("    👾 " + m);
        }
    }

    @Override
    protected FloorResult resolveChallenge(List<Hero> party) {
        System.out.println("  [CHALLENGE] Combat begins!");
        int round = 1;

        while (monstersAlive() && partyAlive(party)) {
            System.out.println("\n  ── Round " + round + " ──");

            for (Hero hero : party) {
                if (!hero.isAlive()) continue;

                hero.onTurnStart();

                if (!hero.canAct()) {
                    hero.onTurnEnd();
                    continue;
                }

                Monster target = firstAliveMonster();
                if (target == null) break;

                int dmg = hero.computeOutgoingDamage();
                System.out.printf("  ⚔  %s attacks %s for %d damage!%n",
                        hero.getName(), target.getName(), dmg);
                target.takeDamage(dmg);

                if (!target.isAlive()) {
                    System.out.printf("  💀 %s has been defeated!%n", target.getName());
                }

                hero.onTurnEnd();
            }

            for (Monster m : monsters) {
                if (!m.isAlive()) continue;

                Hero target = firstAliveHero(party);
                if (target == null) break;

                int rawDmg = m.getAttackPower();
                System.out.printf("  👾 %s attacks %s for %d!%n",
                        m.getName(), target.getName(), rawDmg);
                target.receiveDamage(rawDmg);

                applyMonsterStatus(m, target);

                checkBerserkThreshold(target);
            }

            round++;
        }

        boolean victory = !partyAlive(party) ? false : !monstersAlive();
        String summary = victory
                ? "Party defeated all monsters on floor " + getFloorNumber()
                : "Party was wiped out on floor " + getFloorNumber();

        System.out.println("\n  [CHALLENGE RESULT] " + (victory ? "VICTORY!" : "DEFEAT!"));
        return new FloorResult(victory, summary);
    }

    @Override
    protected void awardLoot(List<Hero> party, FloorResult result) {
        System.out.println("  [LOOT] Each hero earns a healing salve (+15 HP).");
        for (Hero h : party) {
            if (h.isAlive()) h.heal(15);
        }
    }

    private boolean monstersAlive() {
        return monsters.stream().anyMatch(Monster::isAlive);
    }

    private boolean partyAlive(List<Hero> party) {
        return party.stream().anyMatch(Hero::isAlive);
    }

    private Monster firstAliveMonster() {
        return monsters.stream().filter(Monster::isAlive).findFirst().orElse(null);
    }

    private Hero firstAliveHero(List<Hero> party) {
        return party.stream().filter(Hero::isAlive).findFirst().orElse(null);
    }

    private void applyMonsterStatus(Monster m, Hero target) {
        if (!target.isAlive()) return;
        String name = m.getName().toLowerCase();

        if (name.contains("viper") || name.contains("serpent") || name.contains("spider")) {
            if (!(target.getState() instanceof PoisonedState)) {
                System.out.printf("  ☠ %s injects venom into %s!%n", m.getName(), target.getName());
                target.setState(new PoisonedState(3));
            }
        } else if (name.contains("ghost") || name.contains("wraith") || name.contains("specter")) {
            if (!(target.getState() instanceof StunnedState)) {
                System.out.printf("  ⚡ %s's ethereal touch stuns %s!%n", m.getName(), target.getName());
                target.setState(new StunnedState(2));
            }
        }
    }

    private void checkBerserkThreshold(Hero hero) {
        if (!hero.isAlive()) return;
        double ratio = (double) hero.getHp() / hero.getMaxHp();
        if (ratio < 0.40 && hero.getState() instanceof com.narxoz.rpg.state.NormalState) {
            System.out.printf("  🔥 %s's HP is critical (%.0f%%)! BERSERK TRIGGERED!%n",
                    hero.getName(), ratio * 100);
            hero.setState(new BerserkState());
        }
    }
}
