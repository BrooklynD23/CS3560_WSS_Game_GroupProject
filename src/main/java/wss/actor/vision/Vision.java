package wss.actor.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import wss.util.Direction;
import wss.world.Map;
import wss.world.Square;
import wss.world.item.Item;

public abstract class Vision {

    /** returns all squares this vision can "see" */
    protected abstract List<VisibleSquare> scan(Map map, int x, int y);

    /* ------------ new high-level queries ------------ */

    /** first step toward nearest food bonus, empty list if none */
    public List<Direction> closestFood(Map map, int x, int y) {
        List<VisibleSquare> seen = scan(map, x, y);
        System.err.println(getClass().getSimpleName() + " sees " + seen.size() + " squares for food search.");

        Optional<VisibleSquare> target = scan(map, x, y).stream()
                .filter(vs -> vs.square().getItems().stream().anyMatch(Item::isFood))
                .min((a, b) -> Integer.compare(a.distance(), b.distance()));
        return target.map(vs -> List.of(vs.firstStep())).orElseGet(ArrayList::new);
    }

    /** first step toward nearest water bonus, empty list if none */
    public List<Direction> closestWater(Map map, int x, int y) {
        List<VisibleSquare> seen = scan(map, x, y);
        System.out.println(getClass().getSimpleName() + " sees " + seen.size() + " squares for water search.");

        Optional<VisibleSquare> target = scan(map, x, y).stream()
                .filter(vs -> vs.square().getItems().stream().anyMatch(Item::isWater))
                .min((a, b) -> Integer.compare(a.distance(), b.distance()));
        return target.map(vs -> List.of(vs.firstStep())).orElseGet(ArrayList::new);
    }

    /** first step toward nearest gold bonus, empty list if none */
    public List<Direction> closestGold(Map map, int x, int y) {
        List<VisibleSquare> seen = scan(map, x, y);
        System.out.println(getClass().getSimpleName() + " sees " + seen.size() + " squares for gold search.");

        Optional<VisibleSquare> target = scan(map, x, y).stream()
                .filter(vs -> vs.square().getItems().stream().anyMatch(Item::isGold))
                .min((a, b) -> Integer.compare(a.distance(), b.distance()));
        return target.map(vs -> List.of(vs.firstStep())).orElseGet(ArrayList::new);
    }

    /* reusable record: square + distance + first step */
    protected record VisibleSquare(Square square, int distance, Direction firstStep) { }
    
}
