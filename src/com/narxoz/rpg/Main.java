package com.narxoz.rpg;
import com.narxoz.rpg.combatant.Hero;
import com.narxoz.rpg.combatant.Monster;
import com.narxoz.rpg.floor.TowerFloor;
import com.narxoz.rpg.floor.BossFloor;
import com.narxoz.rpg.floor.CombatFloor;
import com.narxoz.rpg.floor.RestFloor;
import com.narxoz.rpg.floor.TrapFloor;
import com.narxoz.rpg.state.PoisonedState;
import com.narxoz.rpg.state.StunnedState;
import com.narxoz.rpg.tower.TowerRunResult;
import com.narxoz.rpg.tower.TowerRunner;

import java.util.List;
public class Main {

    public static void main(String[] args) {
        Hero aria   = new Hero("Aria",   80, 18);
        Hero gareth = new Hero("Gareth", 120, 14);

        System.out.println(">>> Setting initial states before the ascent:");
        aria.setState(new PoisonedState(2));
        gareth.setState(new StunnedState(1));

        List<Hero> party = List.of(aria, gareth);

        List<TowerFloor> floors = List.of(
                new CombatFloor(1, "Viper Den", List.of(
                        new Monster("Venom Viper",  40, 10),
                        new Monster("Cave Spider",  25,  8)
                )),
                new TrapFloor(2),
                new RestFloor(3),
                new CombatFloor(4, "Wraith Hall", List.of(
                        new Monster("Wraith Specter", 55, 14),
                        new Monster("Shadow Ghost",   30, 10)
                )),
                new BossFloor(5, new Monster("The Lich King", 150, 22))
        );

        TowerRunner runner = new TowerRunner(floors, party);
        TowerRunResult result = runner.run();

        System.out.println(result);
    }
}
