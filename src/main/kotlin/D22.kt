package main.kotlin

object D22 : Solver {

    override fun sample(): String = """
                ...#
                .#..
                #...
                ....
        ...#.......#
        ........#...
        ..#....#....
        ..........#.
                ...#....
                .....#..
                .#......
                ......#.

        10R5L5R10L4R5L5
    """.trimIndent()

    enum class Direction(val x: Horizontal, val y: Vertical) {


        EAST(Horizontal(1), Vertical.ZERO), SOUTH(Horizontal.ZERO, Vertical(1)), WEST(Horizontal(-1), Vertical.ZERO), NORTH(
            Horizontal.ZERO,
            Vertical(-1)
        );

        fun simpleMove(p: Pair<Horizontal, Vertical>) = Pair(p.first + x, p.second + y)

        fun move(
            p: Pair<Horizontal, Vertical>,
            map: Map<Pair<Horizontal, Vertical>, Char>,
            xRanges: Map<Vertical, IntRange>,
            yRanges: Map<Horizontal, IntRange>
        ): Pair<Horizontal, Vertical> {
            val next: Pair<Horizontal, Vertical> = Pair(p.first + x, p.second + y)
            return when (map[next]) {
                null, ' ' -> {
                    val xRange = xRanges[p.second]!!
                    val yRange = yRanges[p.first]!!
                    val nextX: Horizontal =
                        if (next.first.x > xRange.last) Horizontal(xRange.first) else if (next.first.x < xRange.first) Horizontal(xRange.last) else next.first
                    val nextY: Vertical =
                        if (next.second.y > yRange.last) Vertical(yRange.first) else if (next.second.y < yRange.first) Vertical(yRange.last) else next.second

                    val nextNext = Pair(nextX, nextY)
                    if (map[nextNext] == '#') p
                    else nextNext
                }
                '.' -> next
                '#' -> p
                else -> {
                    TODO("wtf? ${map[next]}")
                }
            }
        }

        fun turnRight() = values()[(this.ordinal + 1).mod(values().size)]
        fun turnLeft() = values()[(this.ordinal - 1).mod(values().size)]

    }

    @JvmInline
    value class Vertical(val y: Int) {
        operator fun plus(other: Vertical) = Vertical(this.y + other.y)

        companion object {
            val ZERO = Vertical(0)
        }

    }

    @JvmInline
    value class Horizontal(val x: Int) {
        operator fun plus(other: Horizontal) = Horizontal(this.x + other.x)

        companion object {
            val ZERO = Horizontal(0)
        }
    }

    override fun solve(input: List<String>): Any {
        val (maze, moves) = input.splitOnEmpty()
        val map = mutableMapOf<Pair<Horizontal, Vertical>, Char>()
        maze.mapIndexed { y, row -> Pair(y + 1, row) }.forEach { (y, row) ->
            row.mapIndexed { index, c ->
                if (c != ' ') {
                    val x = index + 1
                    map.put(Pair(Horizontal(x), Vertical(y)), c)
                }
            }
        }
        val startingY: Vertical = map.filter { it.value == '.' }.map { it.key }.minBy { it.second.y }.second
        val startingX: Horizontal = map.filter { it.value == '.' && it.key.second == startingY }.minBy { it.key.first.x }.key.first
        val xRanges: Map<Vertical, IntRange> = map.keys.groupBy { it.second }.mapValues { entry ->
            val xs = entry.value.map { it.first.x }
            xs.min()..xs.max()
        }
        val yRanges: Map<Horizontal, IntRange> = map.keys.groupBy { it.first }.mapValues { entry ->
            val ys = entry.value.map { it.second.y }
            ys.min()..ys.max()
        }
        val startingDirection = Direction.EAST

        fun moveRecursive(list: String, p: Pair<Horizontal, Vertical>, dir: Direction): Pair<Pair<Horizontal, Vertical>, Direction> {
            if (list.isEmpty()) return Pair(p, dir)
            val turns = list.takeWhile { it == 'R' || it == 'L' }
            val newDir = turns.fold(dir) { oldDir, c -> if (c == 'R') oldDir.turnRight() else oldDir.turnLeft() }
            val rest = list.drop(turns.length)
            val toMove = rest.takeWhile { it != 'R' && it != 'L' }
            val newPos = if (toMove.isEmpty()) p else (0 until toMove.toInt()).fold(p) { pos, _ -> newDir.move(pos, map, xRanges, yRanges) }
            val rem = rest.drop(toMove.length)
            if (map[newPos] != '.') {
                throw RuntimeException("expected . but was: ${map[newPos]} at: $newPos")
            }
            return moveRecursive(rem, newPos, newDir)
        }

        val finishLine = moveRecursive(moves.joinToString(""), Pair(startingX, startingY), startingDirection)
        //In the above example, the final row is 6, the final column is 8, and the final facing is 0. So, the final password is 1000 * 6 + 4 * 8 + 0: 6032.
        return finishLine.first.second.y * 1000 + finishLine.first.first.x * 4 + finishLine.second.ordinal
    }

    override fun solveb(input: List<String>): Any {
        return solveBManual(input)
    }

    fun solveBManual(input: List<String>, length: Int = 50): Int {
        val (maze, moves) = input.splitOnEmpty()
        val map = mutableMapOf<Pair<Horizontal, Vertical>, Cell>()
        maze.mapIndexed { y, row -> Pair(y + 1, row) }.forEach { (y, row) ->
            row.mapIndexed { index, c ->
                val x = index + 1
                when (c) {
                    ' ' -> map.put(Pair(Horizontal(x), Vertical(y)), Void)
                    '.' -> map.put(Pair(Horizontal(x), Vertical(y)), Path(Horizontal(x), Vertical(y)))
                    else -> map.put(Pair(Horizontal(x), Vertical(y)), Wall)
                }
            }
        }
        val startingY: Vertical = map.filter { it.value is Path }.map { it.key }.minBy { it.second.y }.second
        val startingX: Horizontal = map.filter { it.value is Path && it.key.second == startingY }.minBy { it.key.first.x }.key.first
        val startingDirection = Direction.EAST

        map.values.forEach { it.linkDefault(map) }
        // link wrapping sides

        TODO("Link wrapping sides")


        fun moveRecursive(list: String, p: Cell, dir: Direction): Pair<Cell, Direction> {
            if (list.isEmpty()) return Pair(p, dir)
            check(p is Path)
            val turns = list.takeWhile { it == 'R' || it == 'L' }
            val newDir = turns.fold(dir) { oldDir, c -> if (c == 'R') oldDir.turnRight() else oldDir.turnLeft() }
            val rest = list.drop(turns.length)
            val toMove = rest.takeWhile { it != 'R' && it != 'L' }

            val (newPos:Cell, dir2: Direction) = if (toMove.isEmpty()) Pair(p, newDir) else {
                (0 until toMove.toInt()).fold(Pair(p, newDir)) { (pp, dd), _ ->
                    when (dd) {
                        Direction.NORTH -> pp.north
                        Direction.SOUTH -> pp.south
                        Direction.WEST -> pp.west
                        Direction.EAST -> pp.east
                    }
                }
            }
            val rem = rest.drop(toMove.length)
//            check("", map[newPos] is Path)
            return moveRecursive(rem, newPos, dir2)
        }

        val (finalPos, direction) = moveRecursive(moves.joinToString(""), map[Pair(startingX, startingY)]!!, startingDirection)
        //In the above example, the final row is 6, the final column is 8, and the final facing is 0. So, the final password is 1000 * 6 + 4 * 8 + 0: 6032.
        return finalPos.y.y * 1000 + finalPos.x.x * 4 + direction.ordinal
    }

    interface Cell {
        fun linkDefault(map: Map<Pair<Horizontal, Vertical>, Cell>)
        var east: Pair<Cell, Direction>
        var south: Pair<Cell, Direction>
        var west: Pair<Cell, Direction>
        var north: Pair<Cell, Direction>
        val x: Horizontal
        val y: Vertical
    }
    object Wall : Cell {
        override fun linkDefault(map: Map<Pair<Horizontal, Vertical>, Cell>) {}
        override var east: Pair<Cell, Direction> = TODO()
        override var south: Pair<Cell, Direction> = TODO()
        override var west: Pair<Cell, Direction> = TODO()
        override var north: Pair<Cell, Direction> = TODO()
        override val x: Horizontal by lazy { TODO() }
        override val y: Vertical by lazy { TODO() }
    }
    object Void : Cell {
        override fun linkDefault(map: Map<Pair<Horizontal, Vertical>, Cell>) {}
        override var east: Pair<Cell, Direction> = TODO()
        override var south: Pair<Cell, Direction> = TODO()
        override var west: Pair<Cell, Direction> = TODO()
        override var north: Pair<Cell, Direction> = TODO()
        override val x: Horizontal by lazy { TODO() }
        override val y: Vertical by lazy { TODO() }
    }
    data class Path(override val x: Horizontal, override val y: Vertical) : Cell {
        override var east: Pair<Cell, Direction> = Pair(this, Direction.EAST)
        override var south: Pair<Cell, Direction> = Pair(this, Direction.EAST)
        override var west: Pair<Cell, Direction> = Pair(this, Direction.EAST)
        override var north: Pair<Cell, Direction> = Pair(this, Direction.EAST)

        fun linkSimple(dir: Direction, map: Map<Pair<Horizontal, Vertical>, Cell>): Pair<Cell, Direction> {
            val current = Pair(this.x, this.y)
            val np = dir.simpleMove(current)
            return when (val existing = map[np]) {
                is Wall -> Pair(this, dir)
                is Void, null -> Pair(Void, dir)
                else -> Pair(existing, dir)
            }
        }

        override fun linkDefault(map: Map<Pair<Horizontal, Vertical>, Cell>) {
            east = linkSimple(Direction.EAST, map)
            south = linkSimple(Direction.SOUTH, map)
            west = linkSimple(Direction.WEST, map)
            north = linkSimple(Direction.NORTH, map)
        }
    }

}

fun main() {
//    D22.solveSample(6032)
    D22.solve("day22.txt") // 64210 is too low
}