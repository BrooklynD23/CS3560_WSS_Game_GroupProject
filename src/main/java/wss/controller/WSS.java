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
        System.out.print("Enter map width: ");
        int width = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter map height: ");
        int height = Integer.parseInt(scanner.nextLine().trim());

        // 2) Prompt for difficulty
        DifficultyLevel difficulty = null;
        while (difficulty == null) {
            System.out.print("\nChoose difficulty level (EASY, MEDIUM, HARD): ");
            try {
                difficulty = DifficultyLevel.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[ERROR] Invalid input. Please try again.");
            }
        }
        System.out.println("[CONFIG] Difficulty level mode set to: " + difficulty.name());

        // 3) Prompt for vision type
        Vision vision = null;
        while (vision == null) {
            System.out.print("Choose vision type (FULL, CAUTIOUS, DIAGONAL, RANDOM, EXTENDED): ");
            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "FULL" -> vision = new FullVision();
                case "CAUTIOUS" -> vision = new CautiousVision();
                case "DIAGONAL" -> vision = new DiagonalVision();
                case "RANDOM" -> vision = new RandomVision();
                case "EXTENDED" -> {
                    System.out.print("Enter vision range (1–5): ");
                    int range = Integer.parseInt(scanner.nextLine().trim());
                    vision = new ConfigurableVision(VisionConfig.fullVision(range));
                }
                default -> System.out.println("[ERROR] Invalid vision type.");
            }
        }
        System.out.println("[CONFIG] Vision mode set to: " + vision.getClass().getSimpleName());

        // 4) Prompt for brain type
        Brain brain = null;
        while (brain == null) {
            System.out.print("Choose brain type (CONSERVATIVE, GREEDY, RESOURCE, RISKY, SURVIVOR, ADAPTIVE(Program will switch brain as it sees best): ");
            String choice = scanner.nextLine().trim().toUpperCase();
            switch (choice) {
                case "CONSERVATIVE" -> brain = new ConservativeBrain();
                case "GREEDY" -> brain = new GreedyBrain();
                case "RESOURCE" -> brain = new ResourceOptimizingBrain();
                case "RISKY" -> brain = new RiskTakingBrain();
                case "SURVIVOR" -> brain = new SurvivorBrain();
                case "ADAPTIVE" -> brain = new AdaptiveBrain();
                default -> System.out.println("[ERROR] Invalid brain type.");
            }
        }
        System.out.println("[CONFIG] Brain mode set to: " + brain.getClass().getSimpleName());

        // 5) Build map and player
        Map gameMap = new Map(width, height, difficulty);
        Player player = new Player(0, 0, brain, vision);
        player.initExplorationMap(gameMap);
        player.scaleSuppliesByMapSize(gameMap);

        // 6) Print initial player stats
        System.out.printf("""
            \n-----------------------------------------------
            [START] Game Start
            Player Position: (0,0)
            Strength: %d | Food: %d | Water: %d | Gold: %d
            -----------------------------------------------%n""",
            player.getStrength(), player.getFood(), player.getWater(), player.getGold());

        // 7) Set up game parameters
        GameParameters gameParams = new GameParameters();
        int maxTurns = gameParams.getMaxTurns();

        // 8) Main loop
        for (int turn = 0; turn < maxTurns; turn++) {
            System.out.println("\n=========================== TURN " + turn + " =============================");
            player.takeTurn(turn, gameMap);

            // Win condition
            if (gameParams.getCustomWinCondition().test(player, gameMap)) {
                System.out.println("[VICTORY] Win condition achieved! You win.");
                break;
            }

            // Loss condition
            if (gameParams.isOutOfResources(player)) {
                System.out.println("[GAME OVER] Out of resources. Game over.");
                break;
            }

            // Brain suggestion
            if (!(player.getBrain() instanceof AdaptiveBrain)) {
                Brain suggested = suggestNewBrain(player, gameMap);
                if (suggested != null && !suggested.getClass().equals(player.getBrain().getClass())) {
                    System.out.printf("[PROMPT] Suggested brain: %s. Switch? (yes/no): ",
                            suggested.getClass().getSimpleName());
                    String response = scanner.nextLine().trim().toLowerCase();
                    if (response.equals("yes")) {
                        player.setBrain(suggested);
                        System.out.println("[UPDATE] Brain switched to " + suggested.getClass().getSimpleName());
                    }
                }
            }
        }
        // 9) End of game
        System.out.println("[GAME OVER] Game over. Thanks for playing!");
        scanner.close();
    }

    // Suggest a brain based on current status
    private static Brain suggestNewBrain(Player p, Map map) {
        if (p.getWater() <= 2 || p.getFood() <= 2) {
            System.out.println("[SUGGESTION] Low on resources.");
            return new ResourceOptimizingBrain();
        }
        if (p.getStrength() <= 5) {
            System.out.println("[SUGGESTION] Strength is low.");
            return new SurvivorBrain();
        }
        if (p.getFood() >= 10 && p.getWater() >= 10 && p.getGold() < 5) {
            System.out.println("[SUGGESTION] Plenty of resources available. Time to be more greedy?");
            return new GreedyBrain();
        }
        if (p.getFood() > 5 && p.getWater() > 5 && p.getStrength() > 5 && p.getGold() >= 5) {
            System.out.println("[SUGGESTION] Stable resources. Staying alive is priority.");
            return new ConservativeBrain();
        }
        if ((p.getFood() <= 3 || p.getWater() <= 3) && p.getStrength() < 10) {
            System.out.println("[SUGGESTION] Risk may be necessary to survive.");
            return new RiskTakingBrain();
        }
        return null;
    }
}
