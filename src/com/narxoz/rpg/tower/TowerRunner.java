package com.narxoz.rpg.tower;

import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.floor.FloorResult;
import com.narxoz.rpg.floor.TowerFloor;

import java.util.ArrayList;
import java.util.List;

public class TowerRunner {
    private final List<TowerFloor> floors;
    private final List<Hero>       party;

    public TowerRunner(List<TowerFloor> floors, List<Hero> party) {
        this.floors = new ArrayList<>(floors);
        this.party  = new ArrayList<>(party);
    }

    public TowerRunResult run() {
        System.out.println("   THE HAUNTED TOWER — ASCENT BEGINS");
        printPartyStatus();

        int floorsCleared = 0;

        for (TowerFloor floor : floors) {
            if (!partyAlive()) {
                System.out.println("\n  All heroes have fallen! Ascent halted.");
                break;
            }

            FloorResult result = floor.explore(party);
            System.out.println("\n  Result: " + result);
            printPartyStatus();

            if (result.isVictory()) {
                floorsCleared++;
            } else {
                System.out.println("\n  ❌ Floor failed — tower run ends.");
                break;
            }
        }

        boolean towerDefeated = floorsCleared == floors.size() && partyAlive();
        List<Hero> survivors = party.stream().filter(Hero::isAlive).toList();

        return new TowerRunResult(floorsCleared, floors.size(), survivors, towerDefeated);
    }

    private boolean partyAlive() {
        return party.stream().anyMatch(Hero::isAlive);
    }

    private void printPartyStatus() {
        System.out.println("\n  ── Party Status ──────────────────────────");
        for (Hero h : party) {
            System.out.printf("  %s %s%n",
                    h.isAlive() ? "⚔" : "💀", h);
        }
        System.out.println("  ─────────────────────────────────────────");
    }
}
