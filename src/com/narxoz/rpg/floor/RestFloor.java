package com.narxoz.rpg.floor;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.floor.FloorResult;
import com.narxoz.rpg.floor.TowerFloor;
import com.narxoz.rpg.state.NormalState;
import com.narxoz.rpg.state.RegeneratingState;

import java.util.List;

public class RestFloor extends TowerFloor{
    private static final int HEAL_AMOUNT        = 30;
    private static final int REGEN_PER_TURN     = 8;
    private static final int REGEN_DURATION     = 2;

    public RestFloor(int floorNumber) {
        super(floorNumber, "Sanctuary");
    }

    @Override
    protected void announce() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.printf ("║  Floor %-3d │  ✦ SANCTUARY — Rest here ✦   ║%n", getFloorNumber());
        System.out.println("╚══════════════════════════════════════════╝");
    }

    @Override
    protected boolean shouldAwardLoot(FloorResult result) { return false; }

    @Override
    protected void cleanup(List<Hero> party) {
        System.out.println("  [CLEANUP] The sanctuary's magic cleanses all ailments.");
        for (Hero h : party) {
            if (h.isAlive() && !(h.getState() instanceof NormalState)
                    && !(h.getState() instanceof RegeneratingState)) {
                h.setState(new NormalState());
            }
        }
    }

    @Override
    protected void setup(List<Hero> party) {
        System.out.println("  [SETUP] A warm fire crackles. The party tends to their wounds.");
    }

    @Override
    protected FloorResult resolveChallenge(List<Hero> party) {
        System.out.println("  [CHALLENGE] The party rests and recovers.");
        for (Hero h : party) {
            if (!h.isAlive()) continue;
            System.out.printf("  🏕  %s rests (+%d HP, gains Regeneration for %d turns).%n",
                    h.getName(), HEAL_AMOUNT, REGEN_DURATION);
            h.heal(HEAL_AMOUNT);
            h.setState(new RegeneratingState(REGEN_PER_TURN, REGEN_DURATION));
        }
        return new FloorResult(true, "Party rested on floor " + getFloorNumber());
    }

    @Override
    protected void awardLoot(List<Hero> party, FloorResult result) {
    }
}
