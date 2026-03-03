package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
public final class BattleEngine {

    private static BattleEngine instance;
    private Random random = new Random(1L);

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }

    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }

    public void reset() {
    }

    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {

        if (teamA == null || teamB == null || teamA.isEmpty() || teamB.isEmpty()) {
            throw new IllegalArgumentException("Teams must not be null or empty");
        }

        List<Combatant> a = new ArrayList<>(teamA);
        List<Combatant> b = new ArrayList<>(teamB);

        EncounterResult result = new EncounterResult();
        int rounds = 0;

        while (!a.isEmpty() && !b.isEmpty()) {
            rounds++;
            result.addLog("=== Round " + rounds + " ===");

            attackPhase(a, b, result);
            removeDead(b);

            if (b.isEmpty()) break;

            attackPhase(b, a, result);
            removeDead(a);
        }

        result.setRounds(rounds);
        result.setWinner(a.isEmpty() ? "Team B" : "Team A");
        return result;
    }

    private void attackPhase(List<Combatant> attackers,
                             List<Combatant> defenders,
                             EncounterResult result) {

        for (Combatant attacker : attackers) {

            if (!attacker.isAlive() || defenders.isEmpty()) continue;

            Combatant target = defenders.get(0);

            int damage = attacker.getAttackPower();

            // 20% шанс крит-удара
            if (random.nextDouble() < 0.2) {
                damage *= 2;
                result.addLog(attacker.getName() + " lands CRITICAL hit!");
            }

            target.takeDamage(damage);

            result.addLog(attacker.getName() +
                    " attacks " +
                    target.getName() +
                    " for " + damage + " damage");

            if (!target.isAlive()) {
                result.addLog(target.getName() + " has been defeated!");
            }
        }
    }

    private void removeDead(List<Combatant> team) {
        Iterator<Combatant> iterator = team.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isAlive()) {
                iterator.remove();
            }
        }
    }
}