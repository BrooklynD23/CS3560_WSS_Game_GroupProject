package wss.controller;

import java.util.Scanner;
import wss.actor.*;
import wss.actor.brain.*; 
import wss.actor.vision.*;
import wss.util.*;
import wss.world.Map;

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
                System.out.println("[ERROR] Invalid input. Please try again.");
            }
        }
        System.out.println("[CONFIG] Difficulty level mode set to: " + difficulty.getClass().getSimpleName()); // Confirmation output after difficulty selection
        
        // 3) Prompt for vision type
        Vision vision = null;
        while (vision == null) {
            System.out.println("Choose vision type (FULL, CAUTIOUS, DIAGONAL, RANDOM):");
            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "FULL" -> vision = new FullVision();
                case "CAUTIOUS" -> vision = new CautiousVision();
                case "DIAGONAL" -> vision = new DiagonalVision();
                case "RANDOM"    -> vision = new RandomVision();
                default -> System.out.println("[ERROR] Invalid vision type.");
            }
        }
        System.out.println("[CONFIG] Vision mode set to: " + vision.getClass().getSimpleName()); // Confirmation output after vision selection

        // 4) Prompt for brain type
        Brain brain = null;
        while (brain == null) {
            System.out.println("Choose brain type (CONSERVATIVE, GREEDY, RESOURCE, RISKY, SURVIVOR, ADAPTIVE):");
            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "CONSERVATIVE" -> brain = new ConservativeBrain();
                case "GREEDY"       -> brain = new GreedyBrain();
                case "RESOURCE"     -> brain = new ResourceOptimizingBrain();
                case "RISKY"        -> brain = new RiskTakingBrain();
                case "SURVIVOR"     -> brain = new SurvivorBrain();
                case "ADAPTIVE"     -> brain = new AdaptiveBrain();
                default             -> System.out.println("[ERROR] Invalid brain type.");
            }
        }
        System.out.println("[CONFIG] Brain mode set to: " + brain.getClass().getSimpleName()); // Confirmation output after brain selection

        // 5) Build map and player
        Map gameMap = new Map(width, height, difficulty);
        Player player = new Player(0, 0, brain, vision);

        // 6) Print initial player stats
        System.out.printf("""
            \n[START] Game Start
            Player Position: (0,0)
            Strength: %d | Food: %d | Water: %d | Gold: %d
            -----------------------------------------------%n""",
                player.getStrength(), player.getFood(), player.getWater(), player.getGold());

        // 7) Main loop
        final int MAX_TURNS = 200;
        for (int turn = 0; turn < MAX_TURNS; turn++) {
            System.out.println("\n================= TURN " + turn + " =================");
            player.takeTurn(gameMap);

            if (player.getX() == gameMap.getWidth() - 1) {
                System.out.println("[VICTORY] Reached east edge! You win.");
                break;
            }
            if (player.getStrength() <= 0 || player.getWater() <= 0 || player.getFood() <= 0) {
                System.out.println("[GAME OVER] Out of resources. Game over.");
                break;
            }
        }
    }
}
