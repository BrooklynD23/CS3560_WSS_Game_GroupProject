package wss.controller;

import wss.util.*;
import wss.world.Map;

import java.util.Scanner;

import wss.actor.*;
import wss.actor.brain.ConservativeBrain;
import wss.actor.vision.FullVision;

public class WSS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1) Prompt for map size
        System.out.println("Enter map width:");
        int width  = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("Enter map height:");
        int height = Integer.parseInt(scanner.nextLine().trim());

        // 2) Prompt for difficulty
        DifficultyLevel difficulty = null;
        while (difficulty == null) {
            System.out.println("Choose difficulty level (EASY, MEDIUM, HARD):");
            try {
                difficulty = DifficultyLevel.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice. Try again.");
            }
        }

        // 3) Build map and player
        Map gameMap = new Map(width, height, difficulty);
        Player player = new Player(0, 0, new ConservativeBrain(), new FullVision());

        // 4) Print initial player stats
        System.out.printf("Game start — Player at (0,0)  Strength:%d  Food:%d  Water:%d  Gold:%d%n",
                          player.getStrength(), player.getFood(), player.getWater(), player.getGold());

        // 5) Main loop
        final int MAX_TURNS = 200;
        for (int turn = 0; turn < MAX_TURNS; turn++) {
            System.out.println("\n=== TURN " + turn + " ===");
            player.takeTurn(gameMap);

            if (player.getX() == gameMap.getWidth() - 1) {
                System.out.println("Reached east edge! You win.");
                break;
            }
            if (player.getStrength() <= 0 || player.getWater() <= 0 || player.getFood() <= 0) {
                System.out.println("Out of resources. Game over.");
                break;
            }
        }
    }
}
