package gui.editor.module.grid.tileview;

import game.tile.Tile;
import util.CardinalPair;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Tile
 * info which takes in a given set of tiles and a center point then maps
 * information around that tile.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class TileInfo extends CardinalPair<Tile, Tile, Tile, Tile, Tile> {

    /**
     * Constructs the tile info from the provided args.
     *
     * @param row      The row position of the target tile (center tile).
     * @param col      The col position of the target tile (center tile).
     * @param possible All possible tiles that exist.
     */
    public TileInfo(final int row,
                    final int col,
                    final Tile[][] possible) {
        super(
                getCenter(possible, row, col),
                getNorth(possible, row, col),
                getEast(possible, row, col),
                getSouth(possible, row, col),
                getWest(possible, row, col)
        );
    }

    /**
     * Populates a stream of all the tiles in the info. Specifically in the
     * order: Centre, N, E, S, W.
     *
     * @return A stream of all the optionals C, N, E, S, W.
     */
    public Stream<Optional<Tile>> stream() {
        return Stream.of(
                getCenter(),
                getNorth(),
                getEast(),
                getSouth(),
                getWest()
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Could merge these methods into a single one.
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the target tile is present in its specified cardinality.
     *
     * @param p   The tiles to check through.
     * @param row The center row position in p.
     * @param col The center col position in p.
     * @return Tile if present, else null.
     */
    private static Tile getCenter(final Tile[][] p,
                                  final int row,
                                  final int col) {
        if ((row >= 0)
                && (p.length > row)
                && (col >= 0)
                && (p[row].length > col)) {
            return p[row][col];
        } else {
            return null;
        }
    }

    /**
     * Checks if the target tile is present in its specified cardinality.
     *
     * @param p   The tiles to check through.
     * @param row The center row position in p.
     * @param col The center col position in p.
     * @return Tile if present, else null.
     */
    private static Tile getNorth(final Tile[][] p,
                                 final int row,
                                 final int col) {
        final int northRow = row - 1;

        if ((northRow >= 0)
                && (p.length > northRow)
                && (col >= 0)
                && (p[northRow].length > col)) {
            return p[northRow][col];
        } else {
            return null;
        }
    }

    /**
     * Checks if the target tile is present in its specified cardinality.
     *
     * @param p   The tiles to check through.
     * @param row The center row position in p.
     * @param col The center col position in p.
     * @return Tile if present, else null.
     */
    private static Tile getEast(final Tile[][] p,
                                final int row,
                                final int col) {
        final int eastCol = col + 1;

        if ((row >= 0)
                && (p.length > row)
                && (eastCol >= 0)
                && (p[row].length > eastCol)) {
            return p[row][eastCol];
        } else {
            return null;
        }
    }

    /**
     * Checks if the target tile is present in its specified cardinality.
     *
     * @param p   The tiles to check through.
     * @param row The center row position in p.
     * @param col The center col position in p.
     * @return Tile if present, else null.
     */
    private static Tile getSouth(final Tile[][] p,
                                 final int row,
                                 final int col) {
        final int southRow = row + 1;

        if ((southRow >= 0)
                && (p.length > southRow)
                && (col >= 0)
                && (p[southRow].length > col)) {
            return p[southRow][col];
        } else {
            return null;
        }
    }

    /**
     * Checks if the target tile is present in its specified cardinality.
     *
     * @param p   The tiles to check through.
     * @param row The center row position in p.
     * @param col The center col position in p.
     * @return Tile if present, else null.
     */
    private static Tile getWest(final Tile[][] p,
                                final int row,
                                final int col) {
        final int westCol = col - 1;

        if ((row >= 0)
                && (p.length > row)
                && (westCol >= 0)
                && (p[row].length > westCol)) {
            return p[row][westCol];

        } else {
            return null;
        }
    }

    /**
     * Compiles this object into a null check string. The resulting string
     * should look something like:
     * <p>
     * _X_<br>
     * XTX<br>
     * _X_<br>
     * <p>
     * Where the X represents an Empty optional, and T represents the first
     * name of the Class type held within that specific slot. In this case T
     * represents the Center and is a Tunnel tile. And _ represents an
     * unknown slot.
     *
     * @return Formatted string of this object.
     */
    @Override
    public String toString() {
        final String base = "_%s_%n%s%s%s%n_%s_";

        final Function<Optional<Tile>, String> fn = (t) -> {
            if (t.isPresent()) {
                final Class<?> c = t.get().getClass();
                return String.valueOf(c.getSimpleName().toCharArray()[0]);

            } else {
                return "X";
            }
        };

        return String.format(
                base,
                fn.apply(this.getNorth()),
                fn.apply(this.getWest()),
                fn.apply(this.getCenter()),
                fn.apply(this.getEast()),
                fn.apply(this.getSouth())
        );
    }
}
