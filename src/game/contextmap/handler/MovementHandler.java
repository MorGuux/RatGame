package game.contextmap.handler;

import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.handler.result.MovementResult;
import game.entity.Entity;
import game.entity.subclass.rat.Rat;
import game.level.Level;
import game.tile.Tile;
import game.tile.base.grass.Grass;
import game.tile.base.grass.GrassSprite;
import game.tile.base.path.Path;
import game.tile.base.path.PathSprite;
import gui.game.dependant.tilemap.Coordinates;

import java.lang.reflect.MalformedParametersException;
import java.util.*;

/**
 *
 */
public class MovementHandler {

    /**
     * Not really sure what a good number for this is. So I pulled numbers
     * out of my ass.
     */
    private static final int MAXIMUM_HISTORY_SIZE = 5;

    /**
     * Entities that the handler will not move onto the same tile with.
     */
    private final List<Class<? extends Entity>> blackListedEntities;

    /**
     * Tiles that the handler will not move onto.
     */
    private final List<Class<? extends Tile>> blackListedTiles;

    /**
     * The target object that we're handling.
     */
    private final Entity target;

    /**
     * States if the evaluation should be verbose in that it will not return
     * on the first instance of a blacklisted Object being identified.
     * Default is false.
     */
    private boolean isVerboseEvaluation;

    /**
     * Previous moves made by the handler. This helps us determine new
     * positions and avoid getting stuck in a cycle.
     */
    private List<Coordinates<Integer>> previousMoves = new ArrayList<>();

    /**
     * The directions that will be evaluated when considering a move. Default
     * evaluates N,E,W,S.
     */
    private CardinalDirection[] dirToEvaluate = new CardinalDirection[]{
            CardinalDirection.NORTH,
            CardinalDirection.EAST,
            CardinalDirection.WEST,
            CardinalDirection.SOUTH
    };


    /**
     * @param target              Target entity that this handler manages.
     * @param blackListedTiles    Tiles that this handler will not move onto a
     *                            tile with.
     * @param blackListEnt Entities that this handler will not move
     *                            onto a tile with. That does not mean that
     *                            they won't attempt to just that the result
     *                            will be blocked.
     * @throws NullPointerException If any of the parameters are null.
     */
    public MovementHandler(final Entity target,
                           final List<Class<? extends Tile>> blackListedTiles,
                           final List<Class<? extends Entity>> blackListEnt) {
        Objects.requireNonNull(target);
        Objects.requireNonNull(blackListEnt);
        Objects.requireNonNull(blackListedTiles);

        this.isVerboseEvaluation = false;
        this.blackListedEntities = blackListEnt;
        this.blackListedTiles = blackListedTiles;
        this.target = target;
    }

    public static void main(String[] args) {
        Level l = new Level(3,3, "");
        Tile[][] map = new Tile[][] {
                new Tile[] {p(0,0), p(0,1), g(0,2)},
                new Tile[] {p(1,0), g(1,1), p(1,2)},
                new Tile[] {p(2,0), p(2,1), p(2,2)},
        };

        for (Tile[] tiles : map) {
            for (Tile t : tiles) {
                l.setTile(t, t.getRow(), t.getCol());
            }
        }

        ContextualMap contextualMap = new ContextualMap(map, 3,3);
        Rat r = new Rat(2,0);
        contextualMap.placeIntoGame(r);

        Timer t = new Timer();

        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                r.update(contextualMap, null);
            }
        }, 0, 1000);

    }

    public static Path p(int row, int col) {
        return new Path(PathSprite.BARE_PATH, row, col);
    }

    public static Grass g(int row, int col) {
        return new Grass(GrassSprite.BARE_GRASS, row, col);
    }

    /**
     * Get all the Object types that the Movement handler will not even
     * attempt to make a move onto.
     *
     * @return All Blacklisted Tile Types.
     */
    public List<Class<? extends Tile>> getBlackListedTiles() {
        return blackListedTiles;
    }

    /**
     * Get all Entities that the Movement Handler will not move onto a Tile
     * with. This does not mean that it won't attempt to, just that it will
     * to move onto it.
     *
     * @return All blacklisted Entity types.
     */
    public List<Class<? extends Entity>> getBlackListedEntities() {
        return blackListedEntities;
    }

    /**
     * Set the direction order that should be evaluated when considering a
     * move.
     *
     * @param directions The directions that should be evaluated where index 0
     *                   is first to be evaluated.
     * @throws MalformedParametersException If there is not at least one
     *                                      direction to bias.
     */
    public void setDirectionOrder(final CardinalDirection... directions) {
        Objects.requireNonNull(directions);

        if (directions.length == 0) {
            throw new MalformedParametersException();
        } else {
            this.dirToEvaluate = directions;
            this.isVerboseEvaluation = false;
        }
    }

    /**
     * Set the verbose evaluation state. This determines if we should continue
     * evaluating after a Blacklisted Object is identified. If true the
     * evaluation will continue, this may mean that the number of Entities
     * returned from the {@link MovementResult#getEntitiesThatBlocked()} can
     * exceed One.
     *
     * @param bool The verbose evaluation state.
     */
    public void setIsVerboseEvaluation(final boolean bool) {
        this.isVerboseEvaluation = bool;
    }

    /**
     * Evaluates a move for the target entity and produces a result for what
     * the move was. If the Entity can move with the current rule set then an
     * Optional of MovementResult is returned. However, if not position could
     * be safely evaluated then an Empty optional is returned.
     *
     * @param map The map to traverse.
     * @return A single potential move that you can make if any exist.
     * Otherwise, an Empty optional is returned.
     * @throws IllegalStateException If the Target entity of this handler
     *                               does not exist in the provided map.
     */
    public Optional<MovementResult> makeMove(final ContextualMap map) {
        Objects.requireNonNull(map);

        if (map.isExistingEntity(target)) {
            final Optional<MovementResult> result = traverse(map);

            // If the move could be made then update the internals to
            // accommodate the move.
            result.ifPresent(this::updateStateForMove);

            return result;
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Updates the internal state of the Handler to accommodate for the new
     * state of the handler. Specifically, this ensures that the history is
     * upto date and clean.
     *
     * @param movementResult The result that was produced by the handler that
     *                       is the new state forward.
     */
    private void updateStateForMove(final MovementResult movementResult) {
        final TileData move = movementResult.getToPosition();

        final Coordinates<Integer> pos
                = new Coordinates<>(move.getRow(), move.getCol());

        this.previousMoves.remove(pos);
        // This shifts the index of all other elements one to the right
        this.previousMoves.add(0, pos);

        // We don't want the history to expand too much we only care about
        // the last 4 or so moves
        if (this.previousMoves.size() > MAXIMUM_HISTORY_SIZE) {
            this.previousMoves
                    = this.previousMoves.subList(0, MAXIMUM_HISTORY_SIZE);
        }
    }

    /**
     * Attempts to get traverse using adhering to the blacklist restrictions
     * and the analysis order. If a move is deduced as possible a result
     * based on the further restrictions is returned.
     *
     * @param map The map to make a move for with the target entity.
     * @return MovementResult if a move could be made whilst adhering to the
     * blacklisted restrictions. If no move could be made then an Empty
     * optional is returned.
     */
    private Optional<MovementResult> traverse(final ContextualMap map) {
        final TileData from = map.getOriginTile(target);
        final Optional<TileData> to = deduceTravelTo(map, from);

        // Move is possible
        if (to.isPresent()) {
            // Verbose result
            if (isVerboseEvaluation) {
                return Optional.of(new MovementResult(
                        from, to.get(),
                        getBlackListedEntities(to.get())
                ));

                // Singleton result
            } else {
                final Optional<Entity> e = getBlackListedEntity(to.get());

                return e.map(entity -> new MovementResult(
                        from,
                        to.get(),
                        entity
                )).or(() -> Optional.of(new MovementResult(
                        from,
                        to.get()
                )));
            }

            // No move was possible
        } else {
            return Optional.empty();
        }
    }

    /**
     * Deduces through a thorough check a safe move that can be made from the
     * given origin point in the provided map. Where the analysis order is
     * based around the value set at
     * {@link #setDirectionOrder(CardinalDirection...)} which if not set is
     * Defaulted to: N, E, W, S as the analysis order.
     *
     * @param map    The map to traverse.
     * @param origin The origin point to traverse from.
     * @return Potential move that can be made or if no moves are possible
     * due to restraints with the Blacklisted tiles then an Empty optional is
     * returned.
     */
    private Optional<TileData> deduceTravelTo(final ContextualMap map,
                                              final TileData origin) {
        // Find a position to move from no history
        if (previousMoves.size() == 0) {
            return evaluateInOrder(map, origin);
        }

        // Find potential moves
        final TileData[] potentialMoves = getPotentialMoves(map, origin);

        // For the potential moves
        if (potentialMoves.length > 0) {
            final TileData[] filtered = filterFurther(potentialMoves);

            // All moves are considered backtracking; bias towards the oldest
            // backtracking move
            if (filtered.length == 0) {
                return Optional.of(getEldestBackMove(potentialMoves));

                // At least one move is not considered backtracking; first
                // index is the biased move
            } else {
                return Optional.of(filtered[0]);
            }
        }

        return Optional.empty();
    }

    /**
     * Gets the eldest known move possible from the origin tile.
     *
     * @param potentialMoves The potential moves to evaluate.
     * @return The eldest move in potentialMoves that is adjacent to the origin.
     */
    private TileData getEldestBackMove(final TileData[] potentialMoves) {
        int oldestMove = -1;

        // Find the eldest move
        for (TileData move : potentialMoves) {
            final Coordinates<Integer> pos
                    = new Coordinates<>(move.getRow(), move.getCol());
            final int age = previousMoves.indexOf(pos);

            if (oldestMove < age) {
                oldestMove = age;
            }
        }

        // If an eldest move was found return it.
        if (oldestMove > 0 && oldestMove < potentialMoves.length) {
            return potentialMoves[oldestMove];

            // If one was not found, then this method was called incorrectly
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Filters the given potential moves to those that are not considered
     * backtracking in that they are moves that are not going to a previously
     * known position.
     *
     * @param potentialMoves The moves to filter.
     * @return All moves not considered backtracking.
     */
    private TileData[] filterFurther(final TileData[] potentialMoves) {
        final List<TileData> filtered = new ArrayList<>();

        // Get all positions that are not considered backtracking
        for (TileData move : potentialMoves) {
            for (Coordinates<Integer> prevPos : this.previousMoves) {

                // If move not backtracking
                if (!(move.getRow() == prevPos.getRow())
                        && !(move.getCol() == prevPos.getCol())) {
                    filtered.add(move);
                }
            }
        }

        // No potential moves
        if (filtered.size() == 0) {
            return new TileData[0];

            // Some potential moves
        } else {
            return filtered.toArray(new TileData[0]);
        }
    }

    /**
     * Gets all potential moves from an Origin position. The potential moves
     * are those that are inbounds index wise through
     * {@link ContextualMap#isTraversePossible(CardinalDirection, TileData)}
     * and the Tile is not a blacklisted tile.
     *
     * @param map    Map to traverse.
     * @param origin Origin to traverse from.
     * @return All moves if any that are not blacklisted.
     */
    private TileData[] getPotentialMoves(final ContextualMap map,
                                         final TileData origin) {
        final List<TileData> list = new ArrayList<>();

        for (CardinalDirection dir : dirToEvaluate) {
            // If move possible (index wise)
            if (map.isTraversePossible(dir, origin)) {
                final TileData data = map.traverse(dir, origin);

                // If move isn't blacklisted
                if (!isBlacklistedTile(data.getTile())) {
                    list.add(data);
                }
            }
        }

        return list.toArray(new TileData[0]);
    }

    /**
     * Evaluates just the Tile of the resulting moves returning a TileData if
     * any position has a valid Tile. Otherwise, if no position has a valid
     * tile then an empty optional is returned.
     *
     * @param map The map to traverse.
     * @param pos The origin to move from.
     * @return TileData if one could be found. Otherwise, empty optional.
     */
    private Optional<TileData> evaluateInOrder(final ContextualMap map,
                                               final TileData pos) {
        // Only evaluates that the Tile is safe
        for (CardinalDirection dir : dirToEvaluate) {

            // Safe index
            if (map.isTraversePossible(dir, pos)) {
                final TileData temp = map.traverse(dir, pos);

                // Good tile
                if (!isBlacklistedTile(temp.getTile())) {
                    return Optional.of(temp);
                }
            }
        }

        // No good positions
        return Optional.empty();
    }

    /**
     * Checks to see if the provided tile is a blacklisted tile or not.
     *
     * @param t The tile to check.
     * @return {@code true} if the provided Tile is a blacklisted tile.
     * Otherwise, {@code false} is returned.
     */
    private boolean isBlacklistedTile(final Tile t) {
        if (t == null) {
            return false;
        }

        // Search for blacklisted
        for (Class<?> tileClass : blackListedTiles) {
            if (tileClass.isInstance(t)) {
                return true;
            }
        }

        // Default exit
        return false;
    }

    /**
     * Verbose evaluation of the TileData getting all entities that are
     * blacklisted.
     *
     * @param data The data to evaluate.
     * @return All blacklisted Entities if any on the provided tile data.
     */
    private Entity[] getBlackListedEntities(final TileData data) {
        final LinkedList<Entity> list = new LinkedList<>();

        for (Entity e : data.getEntities()) {
            for (Class<?> entityClass : this.blackListedEntities) {
                if (entityClass.isInstance(e)) {
                    list.add(e);
                }
            }
        }

        return list.toArray(new Entity[0]);
    }

    /**
     * Gets the first Entity on the Tile that is blacklisted.
     *
     * @param data The data to evaluate.
     * @return Optional of the Blacklisted entity. Where null if no
     * blacklisted entities could be found.
     */
    private Optional<Entity> getBlackListedEntity(final TileData data) {
        for (Entity e : data.getEntities()) {
            for (Class<?> entityClass : blackListedEntities) {
                if (entityClass.isInstance(e)) {
                    return Optional.of(e);
                }
            }
        }
        return Optional.empty();
    }
}
