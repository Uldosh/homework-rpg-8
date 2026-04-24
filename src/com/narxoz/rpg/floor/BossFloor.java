package com.narxoz.rpg.floor;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.combatant.Monster;
import com.narxoz.rpg.floor.FloorResult;
import com.narxoz.rpg.floor.TowerFloor;
import com.narxoz.rpg.state.BerserkState;
import com.narxoz.rpg.state.NormalState;
import com.narxoz.rpg.state.PoisonedState;
import com.narxoz.rpg.state.StunnedState;

import java.util.List;


public class BossFloor extends TowerFloor{
    private final Monster boss;

    public BossFloor(int floorNumber, Monster boss) {
        super(floorNumber, "Boss Chamber");
        this.boss = boss;
    }

    @Override
    protected void announce() {
        System.out.println("\n");
        System.out.println("BOSS");
        System.out.printf ("%n  Floor %-3d │ ☠  BOSS CHAMBER  ☠%n", getFloorNumber());
        System.out.printf ("  The tower shakes as %s awakens!%n%n", boss.getName());
    }

    @Override
    protected void setup(List<Hero> party) {
        System.out.println("  [SETUP] Boss rises from the abyss: " + boss);
        System.out.println("  [SETUP] The party steels themselves for the hardest fight...");
    }

    @Override
    protected FloorResult resolveChallenge(List<Hero> party) {
        System.out.println("  [CHALLENGE] BOSS FIGHT!");
        int round = 1;

        while (boss.isAlive() && partyAlive(party)) {
            System.out.println("\n  ── Boss Round " + round + " ──");

            for (Hero hero : party) {
                if (!hero.isAlive()) continue;

                hero.onTurnStart();

                if (!hero.canAct()) {
                    System.out.printf("  ⚡ %s cannot act!%n", hero.getName());
                    hero.onTurnEnd();
                    continue;
                }

                int dmg = hero.computeOutgoingDamage();
                System.out.printf("  ⚔  %s strikes %s for %d damage!%n",
                        hero.getName(), boss.getName(), dmg);
                boss.takeDamage(dmg);

                hero.onTurnEnd();

                if (!boss.isAlive()) {
                    System.out.println("  💀 " + boss.getName() + " has been SLAIN!");
                    break;
                }
            }

            if (!boss.isAlive()) break;
            System.out.printf("%n  👹 %s unleashes its WRATH!%n", boss.getName());
            bossPrimaryAttack(party, round);
            bossSecondaryAttack(party, round);

            round++;
        }

        boolean victory = !boss.isAlive();
        String summary = victory
                ? "Party defeated the boss " + boss.getName() + " on floor " + getFloorNumber()
                : "Party was slain by " + boss.getName() + " on floor " + getFloorNumber();

        System.out.println("\n  [CHALLENGE RESULT] " + (victory ? "BOSS DEFEATED! 🏆" : "PARTY WIPED! 💀"));
        return new FloorResult(victory, summary);
    }

    @Override
    protected void awardLoot(List<Hero> party, FloorResult result) {
        System.out.println("  [LOOT] The boss drops precious relics — each hero fully healed!");
        for (Hero h : party) {
            if (h.isAlive()) {
                h.heal(h.getMaxHp());
                h.setState(new NormalState());
            }
        }
    }

    private void bossPrimaryAttack(List<Hero> party, int round) {
        Hero target = party.stream().filter(Hero::isAlive).findFirst().orElse(null);
        if (target == null) return;

        int dmg = boss.getAttackPower();
        System.out.printf("  👹 Cleave! %s takes %d damage!%n", target.getName(), dmg);
        target.receiveDamage(dmg);
        checkBerserkThreshold(target);
    }

    private void bossSecondaryAttack(List<Hero> party, int round) {
        for (Hero h : party) {
            if (!h.isAlive()) continue;
            if (round % 2 == 0) {
                System.out.printf("  👹 Venom Cloud! %s is poisoned!%n", h.getName());
                h.setState(new PoisonedState(2));
            } else {
                System.out.printf("  👹 Thunder Slam! %s is stunned!%n", h.getName());
                h.setState(new StunnedState(1));
            }
            break;
        }
    }

    private boolean partyAlive(List<Hero> party) {
        return party.stream().anyMatch(Hero::isAlive);
    }

    private void checkBerserkThreshold(Hero hero) {
        if (!hero.isAlive()) return;
        double ratio = (double) hero.getHp() / hero.getMaxHp();
        if (ratio < 0.40 && hero.getState() instanceof NormalState) {
            System.out.printf("  🔥 %s's HP is critical! BERSERK TRIGGERED!%n", hero.getName());
            hero.setState(new BerserkState());
        }
    }
}
