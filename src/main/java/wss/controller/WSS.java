package wss.controller;

import java.util.Scanner;
import wss.actor.*;
import wss.actor.brain.ConservativeBrain;
import wss.actor.vision.CautiousVision;
import wss.actor.vision.DiagonalVision;
import wss.actor.vision.FullVision;
import wss.actor.vision.Vision;
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
                System.out.println("[ERROR] Invalid choice. Try again.");
            }
        }
        
        // 3) Prompt for vision type
        Vision vision = null;
        while (vision == null) {
            System.out.println("Choose vision type (FULL, CAUTIOUS, DIAGONAL):");
            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "FULL" -> vision = new FullVision();
                case "CAUTIOUS" -> vision = new CautiousVision();
                case "DIAGONAL" -> vision = new DiagonalVision();
                default -> System.out.println("[ERROR] Invalid vision type.");
            }
        }

        // 4) Build map and player
        Map gameMap = new Map(width, height, difficulty);
        Player player = new Player(0, 0, new ConservativeBrain(), vision);

        // 5) Print initial player stats
        System.out.printf("""
            \n[START] Game Start
            Player Position: (0,0)
            Strength: %d | Food: %d | Water: %d | Gold: %d
            ------------------------------%n""",
                player.getStrength(), player.getFood(), player.getWater(), player.getGold());

        // 6) Main loop
        final int MAX_TURNS = 200;
        for (int turn = 0; turn < MAX_TURNS; turn++) {
            System.out.println("\n=== TURN " + turn + " ===");
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
