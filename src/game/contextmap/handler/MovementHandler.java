package game.contextmap.handler;

import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.contextmap.TileData;
import game.contextmap.handler.result.MovementResult;
import game.entity.Entity;
import game.tile.Tile;
import gui.game.dependant.tilemap.Coordinates;

import java.lang.reflect.MalformedParametersException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

/**
 * Movement handler class wraps a generic non backtracking moving algorithm
 * that will not move onto tiles which are blacklisted.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class MovementHandler {

    //todo I haven't tested what happens when it encounters black listed
    // entities it should evaluate as normal but not update.

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
     * The previous move that was made.
     */
    private Coordinates<Integer> previousMove;

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
     * @param target           Target entity that this handler manages.
     * @param blackListedTiles Tiles that this handler will not move onto a
     *                         tile with.
     * @param blackListEnt     Entities that this handler will not move
     *                         onto a tile with. That does not mean that
     *                         they won't attempt to just that the result
     *                         will be blocked.
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
        final TileData from = movementResult.getFromPosition();
        previousMove = new Coordinates<>(from.getRow(), from.getCol());
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
                        from,
                        to.get(),
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
        final TileData[] potentialMoves = getPotentialMoves(map, origin);

        if (previousMove == null && potentialMoves.length > 0) {
            return Optional.of(potentialMoves[0]);
        }

        if (potentialMoves.length == 0) {
            return Optional.empty();


        } else {
            TileData[] nonBackTrack = getNonBacktrackMoves(potentialMoves);

            if (nonBackTrack.length >= 1) {
                // If many moves possible pick one randomly
                final Random r = new Random();
                final int size = nonBackTrack.length;
                return Optional.of(nonBackTrack[r.nextInt(size)]);

            } else {
                return getBackTrack(potentialMoves);
            }
        }
    }

    /**
     * Gets the previous tile data position from the set of moves.
     *
     * @param moves The moves to get the previous position from.
     * @return Previous position/move.
     */
    private Optional<TileData> getBackTrack(final TileData[] moves) {
        for (TileData d : moves) {
            final Coordinates<Integer> pos
                    = new Coordinates<>(d.getRow(), d.getCol());

            if (pos.equals(previousMove)) {
                return Optional.of(d);
            }
        }

        return Optional.empty();
    }

    /**
     * Get all the non backtrack moves; those that are not the previous move.
     *
     * @param potentialMoves The set of moves to extract non backtrack moves
     *                       from.
     * @return All moves that are not backtracking.
     */
    private TileData[] getNonBacktrackMoves(final TileData[] potentialMoves) {

        final List<TileData> nonBacktrack = new ArrayList<>();

        for (TileData move : potentialMoves) {
            final Coordinates<Integer> coordinates
                    = new Coordinates<>(move.getRow(), move.getCol());

            if (!coordinates.equals(previousMove)) {
                nonBacktrack.add(move);
            }
        }

        return nonBacktrack.toArray(new TileData[0]);
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
