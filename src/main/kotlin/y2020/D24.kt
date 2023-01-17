package y2020

import advent2020.Solver2020
import y2022.D2

object D24 :Solver2020 {
    override fun sample(): String = """
        sesenwnenenewseeswwswswwnenewsewsw
        neeenesenwnwwswnenewnwwsewnenwseswesw
        seswneswswsenwwnwse
        nwnwneseeswswnenewneswwnewseswneseene
        swweswneswnenwsewnwneneseenw
        eesenwseswswnenwswnwnwsewwnwsene
        sewnenenenesenwsewnenwwwse
        wenwwweseeeweswwwnwwe
        wsweesenenewnwwnwsenewsenwwsesesenwne
        neeswseenwwswnwswswnw
        nenwswwsewswnenenewsenwsenwnesesenew
        enewnwewneswsewnwswenweswnenwsenwsw
        sweneswneswneneenwnewenewwneswswnese
        swwesenesewenwneswnwwneseswwne
        enesenwswwswneneswsenwnewswseenwsese
        wnwnesenesenenwwnenwsewesewsesesew
        nenewswnwewswnenesenwnesewesw
        eneswnwswnwsenenwnwnwwseeswneewsenese
        neswnwewnwnwseenwseesewsenwsweewe
        wseweeenwnesenwwwswnew
    """.trimIndent()

    data class Move(val sym: String, val delta: Pair<Int, Int>)
    val moves = listOf(
        Move("e", Pair(2, 0)),
        Move("se", Pair(1, 1)),
        Move("sw", Pair(-1, 1)),
        Move("w", Pair(-2, 0)),
        Move("nw", Pair(-1, -1)),
        Move("ne", Pair(1, -1))
    )

    private tailrec fun move(line: String, initial: Pair<Int, Int> = Pair(0, 0)): Pair<Int, Int> {
        if(line.isEmpty()) return initial
        val p = moves.find { line.startsWith(it.sym) }!!
        return move(line.substring(p.sym.length), p.delta + initial)
    }

    data class Floor(val blackTiles: Set<Pair<Int, Int>>) {

        fun whiteTiles() = blackTiles.flatMap { tile -> moves.map { it.delta + tile } }.toSet() - blackTiles


        companion object {
            tailrec fun round(floor: Floor, n: Int): Floor {
                if(n == 0) return floor

                fun countBlackNeighbours(p: Pair<Int, Int>) = moves.map { p + it.delta }.count { floor.blackTiles.contains(it) }
                val whitesToBlack = floor.whiteTiles().filter { countBlackNeighbours(it) == 2 }.toSet()
                val blackToWhites = floor.blackTiles.filter {
                    val bn = countBlackNeighbours(it)
                    bn == 0 || bn > 2
                }.toSet()
                return round(Floor((floor.blackTiles - blackToWhites) + whitesToBlack), n-1)
            }
        }
    }

    override fun solve(input: List<String>): Any {
        val identified = input.map { move(it.trim()) }
        return identified.fold(emptySet<Pair<Int, Int>>()) {blackTiles, tile ->
            if(blackTiles.contains(tile)) blackTiles - tile
            else blackTiles + tile
        }.size
    }

    override fun solveb(input: List<String>): Any {
        val identified = input.map { move(it.trim()) }
        val blackTiles = identified.fold(emptySet<Pair<Int, Int>>()) {blackTiles, tile ->
            if(blackTiles.contains(tile)) blackTiles - tile
            else blackTiles + tile
        }
        return Floor.round(Floor(blackTiles), 100).blackTiles.size
    }

}

fun main() {
//    D24.solveSample(10)
//    D24.solve("day24.txt")
    D24.solveSampleB(2208)
    D24.solveb("day24.txt")
}