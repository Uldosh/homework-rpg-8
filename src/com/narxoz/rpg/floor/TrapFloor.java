package com.narxoz.rpg.floor;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.floor.FloorResult;
import com.narxoz.rpg.floor.TowerFloor;
import com.narxoz.rpg.state.StunnedState;

import java.util.List;

public class TrapFloor extends TowerFloor{
    private record Trap(String name, int damage, boolean stuns) {}

    private final Trap[] traps;

    public TrapFloor(int floorNumber) {
        super(floorNumber, "Trap Corridor");
        this.traps = new Trap[]{
                new Trap("Arcane Bolt",     12, false),
                new Trap("Gravity Crush",   18, false),
                new Trap("Paralysis Rune",   8, true),
        };
    }

    @Override
    protected void cleanup(List<Hero> party) {
        System.out.println("  [CLEANUP] Remaining trap runes crumble — corridor cleared.");
    }

    @Override
    protected void setup(List<Hero> party) {
        System.out.println("  [SETUP] The corridor glows with dangerous runes.");
        System.out.printf ("    %d traps detected ahead.%n", traps.length);
    }

    @Override
    protected FloorResult resolveChallenge(List<Hero> party) {
        System.out.println("  [CHALLENGE] Navigating the trap corridor...");

        for (Trap trap : traps) {
            Hero target = party.stream().filter(Hero::isAlive).findFirst().orElse(null);
            if (target == null) break;

            System.out.printf("%n  🪤 TRAP: %s triggers! %s takes %d damage!%n",
                    trap.name(), target.getName(), trap.damage());
            target.receiveDamage(trap.damage());

            if (trap.stuns() && target.isAlive()) {
                System.out.printf("  🪤 The rune's paralysis affects %s!%n", target.getName());
                target.setState(new StunnedState(1));
            }
        }

        boolean anyAlive = party.stream().anyMatch(Hero::isAlive);
        String summary = anyAlive
                ? "Party navigated the trap corridor on floor " + getFloorNumber()
                : "Party perished in the trap corridor on floor " + getFloorNumber();

        System.out.println("\n  [CHALLENGE RESULT] " + (anyAlive ? "SURVIVED!" : "PERISHED!"));
        return new FloorResult(anyAlive, summary);
    }

    @Override
    protected void awardLoot(List<Hero> party, FloorResult result) {
        System.out.println("  [LOOT] A hidden cache rewards the survivors (+10 HP each).");
        party.stream().filter(Hero::isAlive).forEach(h -> h.heal(10));
    }
}
