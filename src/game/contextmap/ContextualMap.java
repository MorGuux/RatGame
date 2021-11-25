package game.contextmap;

import game.entity.Entity;
import game.tile.Tile;
import gui.game.dependant.tilemap.Coordinates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Contextual Map gives a place where a {@link Tile} can have any
 * number of {@link Entity} objects on it, and where said Entity objects can
 * interact with one another.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class ContextualMap {

    // todo Implement exceptions properly. Primarily the usage of custom, and
    //  using proper exception message.

    // todo TileData could have been an inner class that way we could have
    //  just grabbed the Node directly from there.

    /**
     * Underlying game node map, wraps a {@link Tile} with all {@link Entity}
     * 's that exist on the tile.
     */
    private final TileDataNode[][] tileMap;

    /**
     * Map of Entity -> Nodes they occupy, this allows semi-random access.
     */
    private final HashMap<Entity, List<TileDataNode>> entityOccupationMap;

    /**
     * @param gameMap The Tile map for the game.
     * @param rows    The number of rows the Tile map has.
     * @param columns The number of columns the Tile map has.
     */
    public ContextualMap(final Tile[][] gameMap,
                         final int rows,
                         final int columns) {
        this(gameMap, rows, columns, new HashMap<>());
    }

    /**
     * @param gameMap Tiles to use for the Contextual Map.
     * @param rows    Number of rows in the Tile map.
     * @param columns Number of columns in the Tile map.
     * @param e       Existing entities to load into the Game Map.
     */
    public ContextualMap(final Tile[][] gameMap,
                         final int rows,
                         final int columns,
                         final HashMap<Entity, List<Coordinates<Integer>>> e) {
        this.tileMap = new TileDataNode[rows][columns];
        this.populateMap(gameMap);
        this.entityOccupationMap = new HashMap<>();

        // Setup existing occupation map
        e.forEach((entity, positions) -> {
            this.placeIntoGame(entity);
            this.occupyTiles(entity, positions);
        });
    }

    /**
     * Populates the TileDataNode map with the provided Tiles wrapped with an
     * newly constructed {@link TileDataNode}.
     *
     * @param map Tiles to map.
     */
    private void populateMap(final Tile[][] map) {
        // For all Rows
        for (int row = 0; row < map.length; ++row) {

            // For all columns
            for (int col = 0; col < map[row].length; ++col) {
                tileMap[row][col] = new TileDataNode(map[row][col]);
            }
        }
    }

    /**
     * Gets the Entities Origin position tile data node. This is the position
     * that is referred to by the {@link Entity#getRow()} and
     * {@link Entity#getCol()}.
     *
     * @param e Entity to get the Origin Tile Data Node for.
     * @return Tile Data Node for the Entity.
     * @throws IndexOutOfBoundsException If the Entities Origin is out of
     *                                   bounds for the game map.
     */
    private TileDataNode getTileDataNodeAt(final Entity e) {
        if (isIndexInBounds(e)) {
            return tileMap[e.getRow()][e.getCol()];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks to see if the coordinates of the provided TileData are inbounds
     * for the tile map.
     *
     * @param data Tile data to check.
     * @return {@code true} if the x, y, of the data refers to some node in
     * our tile map. Otherwise, {@code false}.
     */
    private boolean isTileDataInbounds(final TileData data) {
        final int row = data.getRow();
        final int col = data.getCol();

        // Check row size
        if (tileMap.length > row) {
            // Check col size
            return tileMap[row].length > col;
        } else {
            return false;
        }
    }

    /**
     * @param data The data to get the underlying node of.
     * @return The underlying node wrapped by the TileData.
     * @throws IndexOutOfBoundsException If the data x, y, is out of bounds
     *                                   for the game map.
     */
    private TileDataNode getUnderlyingNode(final TileData data) {
        if (isTileDataInbounds(data)) {
            return tileMap[data.getRow()][data.getCol()];

        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Checks to see if an Entities X, and Y value are inbounds locally for
     * our TileMap.
     *
     * @param e The entity to check.
     * @return {@code true} iff the x and y are inbounds, else {@code false}.
     */
    private boolean isIndexInBounds(final Entity e) {
        final int row = e.getRow();
        final int col = e.getCol();

        // If row ok, check if column exists.
        if (tileMap.length > row) {
            return tileMap[row].length > col;
        }

        // Default exit.
        return false;
    }

    /**
     * @param e    The entity to move.
     * @param data The place to move the entity to.
     * @throws IllegalStateException     If the provided Entity does not exist
     *                                   in the map.
     * @throws IndexOutOfBoundsException If the provided data refers to a
     *                                   position out of bounds for the map.
     */
    public void moveToTile(final Entity e,
                           final TileData data) {
        if (isExistingEntity(e)) {
            final TileDataNode node = getUnderlyingNode(data);
            detachEntity(e);

            final List<TileDataNode> list = new ArrayList<>();
            list.add(node);
            node.addEntity(e);
            entityOccupationMap.put(e, list);

        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Unlinks an Entity from all the nodes that it occupies.
     *
     * @param e The entity to detach.
     */
    private void detachEntity(final Entity e) {
        final List<TileDataNode> attached = entityOccupationMap.get(e);
        // Detach the entity from the node
        attached.forEach(i -> i.removeEntity(e));
    }

    /**
     * Has the Entity occupy the given Tile data so that other Entities can
     * interact with it.
     *
     * @param e    The entity to add to the tile data.
     * @param data The tile data to add the entity to.
     * @throws IllegalStateException     If the Entity does not exist in the map
     *                                   currently.
     * @throws IndexOutOfBoundsException If the index of the data does not
     *                                   refer to a matching map position.
     */
    public void occupyTile(final Entity e,
                           final TileData data) {
        if (isExistingEntity(e)) {
            final TileDataNode n = getUnderlyingNode(data);
            n.addEntity(e);
            entityOccupationMap.get(e).add(n);

        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Convenience method for an Entity to occupy many tiles using random
     * access time complexity.
     *
     * @param e     The entity which is occupying many tiles.
     * @param tiles The tile positions (Row, Col) to occupy.
     * @throws IllegalStateException     If the Entity does not exist in the map
     *                                   currently.
     * @throws IndexOutOfBoundsException If the index of any positions are not
     *                                   valid indexes.
     */
    private void occupyTiles(final Entity e,
                            final List<Coordinates<Integer>> tiles) {
        if (isExistingEntity(e)) {
            for (Coordinates<Integer> pos : tiles) {
                final TileDataNode n = this.tileMap[pos.getRow()][pos.getCol()];
                n.addEntity(e);
                entityOccupationMap.get(e).add(n);
            }
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * @param e Entity to check for existence.
     * @return {@code true} if the entity exists in our game map. Otherwise,
     * {@code false} is returned.
     */
    public boolean isExistingEntity(final Entity e) {
        return entityOccupationMap.containsKey(e);
    }

    /**
     * Gets the TileData that is referred to by the {@link Entity#getRow()} and
     * {@link Entity#getCol()}.
     *
     * @param e Entity to gather data for.
     * @return Tile data of the origin tile (Tile the entity is considered
     * standing on).
     * @throws IndexOutOfBoundsException If the origin values for the entity are
     *                                   out of bounds.
     */
    public TileData getOriginTile(final Entity e) {
        final TileDataNode node = getTileDataNodeAt(e);
        return new TileData(node);
    }

    /**
     * Gets all the TileData that the given Entity exists on, either by
     * occupying or movement.
     *
     * @param e The entity to get data about.
     * @return The data about the entity.
     * @throws IllegalStateException If the Entity does not exist in the map.
     */
    public TileData[] getTilesOccupied(final Entity e) {
        if (isExistingEntity(e)) {
            final List<TileDataNode> nodes = entityOccupationMap.get(e);
            final List<TileData> data = new ArrayList<>();

            // Map the nodes to TileData
            nodes.forEach(i -> data.add(new TileData(i)));
            return data.toArray(new TileData[0]);

        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Checks to see if the provided traversal is possible without producing
     * an {@link IndexOutOfBoundsException}.
     *
     * @param dir  Direction to travel.
     * @param data Base position to travel from.
     * @return {@code true} if it is possible to make this traversal.
     * Otherwise, if not {@code false} is returned.
     */
    public boolean isTraversePossible(final CardinalDirection dir,
                                      final TileData data) {
        final Coordinates<Integer> pos
                = dir.traverse(data.getRow(), data.getCol());

        final int row = pos.getRow();
        final int col = pos.getCol();

        if (tileMap.length > row) {
            return tileMap[row].length > col;
        } else {
            return false;
        }
    }

    /**
     * Note that all traverse calls should be checked with
     * {@link #isTraversePossible(CardinalDirection, TileData)}.
     *
     * @param dir  The direction to traverse.
     * @param data The origin point to traverse from.
     * @return The new position resulting from the traversal.
     * @throws IndexOutOfBoundsException If the provided cardinal traversal
     *                                   produces a value out of bounds.
     */
    public TileData traverse(final CardinalDirection dir,
                             final TileData data) {
        final Coordinates<Integer> pos
                = dir.traverse(data.getRow(), data.getCol());

        final int row = pos.getRow();
        final int col = pos.getCol();

        // Index oob will be thrown if so
        return new TileData(tileMap[row][col]);
    }

    /**
     * Places the provided entity into the game if index constraints allow it
     * to be placed into the game safely. This places the entity at its
     * origin x and y coordinates specified by {@link Entity#getRow()} and
     * {@link Entity#getCol()} where x refers to the column and y refers to the
     * row.
     *
     * @param e The entity to put into the map.
     * @return The Tile that the Entity was placed onto.
     * @throws IllegalStateException     If the entity already exists in the
     *                                   map.
     * @throws IndexOutOfBoundsException If the entity x, y, is out of bounds
     *                                   for the map.
     * @see #isIndexInBounds(Entity)
     * @see #isExistingEntity(Entity)
     */
    public Tile placeIntoGame(final Entity e) {
        // Entity already exists
        if (isExistingEntity(e)) {
            throw new IllegalStateException();

            // Add the entity to the game
        } else {
            // Throws index oob exception
            final TileDataNode n = getTileDataNodeAt(e);
            n.addEntity(e);
            final List<TileDataNode> list = new ArrayList<>();
            list.add(n);
            entityOccupationMap.put(e, list);
            return n.getTile();
        }
    }

    /**
     * Primitive garbage collection to release outstanding references to dead
     * entities.
     * <p>
     * This function should be called by the controller class of this Object.
     */
    public void collectDeadEntities() {
        // Collect dead entities from all tile data nodes
        Arrays.stream(tileMap)
                .forEach(nodes -> Arrays.stream(nodes)
                        .forEach(TileDataNode::collectDeadEntities));

        // Remove from entity occupation map dead entries.
        final List<Entity> queueDeletion = new ArrayList<>();
        entityOccupationMap.forEach((entity, nodes) -> {
            if (entity.isDead()) {
                queueDeletion.add(entity);
            }
        });
        queueDeletion.forEach(entityOccupationMap::remove);
    }
}
