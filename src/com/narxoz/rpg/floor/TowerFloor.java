package com.narxoz.rpg.floor;

import com.narxoz.rpg.combatant.Hero;
import java.util.List;

/**
 * Abstract base class for tower floors, using the Template Method pattern.
 *
 * The public explore() method defines the fixed skeleton for exploring a floor:
 *   1. announce()            — print floor introduction (can be overridden)
 *   2. setup(party)          — prepare the floor (abstract; subclass implements)
 *   3. resolveChallenge()    — conduct the floor's challenge (abstract; subclass implements)
 *   4. awardLoot()           — grant rewards if applicable (abstract; subclass implements)
 *   5. cleanup(party)        — clean up after the floor (optional hook; can be overridden)
 *
 * Subclasses must:
 * - Implement all abstract methods: getFloorName(), setup(), resolveChallenge(), awardLoot()
 * - NEVER override explore() — it is final
 * - Optionally override the hooks: announce(), shouldAwardLoot(), cleanup()
 *
 * This is the Template Method pattern in action: the algorithm skeleton is fixed in the
 * final method, and subclasses customize the details.
 */
public abstract class TowerFloor {

    /**
     * The fixed template method: orchestrates floor exploration in a defined sequence.
     * This method is final and defines the algorithm skeleton that all subclasses follow.
     *
     * @param party the list of heroes exploring this floor
     * @return a FloorResult describing the outcome
     */
    public final FloorResult explore(List<Hero> party) {
        announce();
        setup(party);
        FloorResult result = resolveChallenge(party);
        if (shouldAwardLoot(result)) {
            awardLoot(party, result);
        }
        cleanup(party);
        return result;
    }

    /**
     * Prints an announcement for this floor.
     * Default implementation prints a generic floor intro; subclasses may override for custom messages.
     * This is a hook — it has a default implementation and is not abstract.
     */
    protected void announce() {
        System.out.println("\n--- Entering " + getFloorName() + " ---");
    }

    /**
     * Prepares the floor before the challenge.
     * Subclasses implement this to set up monsters, traps, NPCs, etc.
     * This is an abstract method — every subclass must implement it.
     *
     * @param party the heroes entering this floor
     */
    protected abstract void setup(List<Hero> party);

    /**
     * Conducts the main challenge of the floor (combat, puzzle, trap, etc.).
     * Subclasses implement this to return a FloorResult describing the outcome.
     * This is an abstract method — every subclass must implement it.
     *
     * @param party the heroes facing the challenge
     * @return a FloorResult indicating whether the floor was cleared and any side effects
     */
    protected abstract FloorResult resolveChallenge(List<Hero> party);

    /**
     * Determines whether loot should be awarded for this floor result.
     * Default implementation returns true; subclasses may override to add logic.
     * This is a hook — it has a default implementation and is not abstract.
     *
     * @param result the outcome of resolveChallenge()
     * @return true if loot should be awarded, false otherwise
     */
    protected boolean shouldAwardLoot(FloorResult result) {
        return true;
    }

    /**
     * Awards loot to the party if they cleared the floor.
     * Subclasses implement this to grant rewards (gold, items, healing, etc.).
     * This is an abstract method — every subclass must implement it.
     *
     * @param party the heroes receiving loot
     * @param result the outcome of the challenge
     */
    protected abstract void awardLoot(List<Hero> party, FloorResult result);

    protected void cleanup(List<Hero> party) {
        // Default: no cleanup needed
    }

    protected abstract String getFloorName();
}
