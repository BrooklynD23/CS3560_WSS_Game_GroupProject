package wss.controller;

import wss.util.*;
import wss.world.Map;

import java.util.Scanner;

import wss.actor.*;
import wss.actor.brain.ConservativeBrain;
import wss.actor.vision.FullVision;
import wss.actor.vision.Vision;

public class WSS {
    public static void main(String[] args) {
        //ask user for difficulty, not case sensitive
        Scanner scanner = new Scanner(System.in);
        DifficultyLevel difficulty = null;

        while (difficulty == null) {
            System.out.println("Choose difficulty level (EASY, MEDIUM, HARD):");
            try {
                difficulty = DifficultyLevel.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice. Try again.");
            }
        }

        Map gameMap = new Map(5, 3, difficulty);
        Vision vision = new FullVision();
        Player player = new Player(0, 1, new ConservativeBrain(), vision);

        final int MAX_TURNS = 200;
        for (int turn=0; turn<MAX_TURNS; turn++) {
            System.out.println("=== TURN "+turn+" ===");
            player.takeTurn(gameMap);

            if (player.getX() == gameMap.getWidth()-1) {
                System.out.println("Reached east edge! You win.");
                break;
            }
            if (player.getStrength()<=0 || player.getWater()<=0 || player.getFood()<=0){
                System.out.println("Out of resources. Game over.");
                break;
            }
        }
    }
}
