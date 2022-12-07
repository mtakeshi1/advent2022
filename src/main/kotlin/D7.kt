package main.kotlin

object D7 : Solver {

    private fun parseCommands(input: List<String>): Map<List<String>, Long> {
        val map: MutableMap<List<String>, Long> = HashMap()
        val root = listOf<String>()
        var cwd = root
        var rem = input
        while (rem.isNotEmpty()) {
            val (c, a) = rem.first().split(" ").let { if (it.size > 2) Pair(it[1], it[2]) else Pair(it[1], "") }
            val response = rem.drop(1).takeWhile { !it.startsWith("$") }
            rem = rem.drop(1 + response.size)
            when (c) {
                "cd" -> cwd = if (a == "/") root else if (a == "..") cwd.dropLast(1) else cwd + a
                "ls" -> {
                    val total = response.filter { !it.startsWith("dir") }.sumOf { it.split(" ")[0].toLong() }
                    var tmp = cwd
                    while (tmp != root) {
                        map[tmp] = map.getOrDefault(tmp, 0) + total
                        tmp = tmp.dropLast(1)
                    }
                    map[root] = map.getOrDefault(root, 0) + total
                }
            }
        }
        return map
    }


    override fun solve(input: List<String>): Any {
        return parseCommands(input).values.filter { it < 100000 }.sum()
    }

    override fun solveb(input: List<String>): Any {
        val folders = parseCommands(input)
        val totalSpace = 70000000
        val usedSpace = folders[listOf()]!!
        val unused = totalSpace - usedSpace
        val toFree = 30000000 - unused
        return folders.values.filter { it >= toFree }.sorted().first()
    }

}
fun main() {
    println(D7.solve("""
        ${'$'} cd /
        ${'$'} ls
        dir a
        14848514 b.txt
        8504156 c.dat
        dir d
        ${'$'} cd a
        ${'$'} ls
        dir e
        29116 f
        2557 g
        62596 h.lst
        ${'$'} cd e
        ${'$'} ls
        584 i
        ${'$'} cd ..
        ${'$'} cd ..
        ${'$'} cd d
        ${'$'} ls
        4060174 j
        8033020 d.log
        5626152 d.ext
        7214296 k
    """.trimIndent()))

    println(D7.solve("day7.txt")) // 2157214 is too high
    println(D7.solveb("day7.txt")) // 2157214 is too high

}