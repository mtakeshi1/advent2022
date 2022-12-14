package main.kotlin

object D11 : Solver {

    data class Monkey(
        val id: Int,
        var items: List<Long>,
        var inspected: Long = 0,
        val inspect: (Long) -> Long,
        val test: Long,
        val onTrue: Int,
        val onFalse: Int
    ) {

        fun turn(monkeys: Map<Int, Monkey>) {
            for (item in items) {
                val newWorry = inspect(item) / 3
                if ((newWorry % test) == 0L) monkeys[onTrue]!!.append(newWorry)
                else monkeys[onFalse]!!.append(newWorry)
                inspected++
            }
            items = listOf()
        }

        fun turnb(monkeys: Map<Int, Monkey>, modulo: Long) {
            for (item in items) {
                val newWorry = inspect(item) % modulo
                if ((newWorry % test) == 0L) monkeys[onTrue]!!.append(newWorry)
                else monkeys[onFalse]!!.append(newWorry)
                inspected++
            }
            items = listOf()
        }

        fun append(item: Long) {
            this.items += item
        }
    }

    fun parseOp(line: String): (Long) -> Long {
        val rhs = line.substring(line.indexOf('=') + 1).trim().split(" ")
        return { x ->
            val left = if (rhs[0] == "old") x else rhs[0].toLong()
            val right = if (rhs[2] == "old") x else rhs[2].toLong()
            when (rhs[1].trim()) {
                "+" -> left + right
                "*" -> left * right
                else -> TODO()
            }
        }
    }

    fun parseMonkey(input: List<String>): Monkey {
        val id = input[0].substring(7, 8).toInt()
        val items = input[1].split(":")[1].trim().split(",").map { it.trim().toLong() }
        val op = parseOp(input[2])
        val test = input[3].substring(input[3].lastIndexOf(' ') + 1).toLong()
        val onTrue = input[4].substring(input[4].lastIndexOf(' ') + 1).toInt()
        val onFalse = input[5].substring(input[5].lastIndexOf(' ') + 1).toInt()
        return Monkey(id, items, 0, op, test, onTrue, onFalse)
    }

    override fun solve(input: List<String>): Any {
        val monkeys = input.map { it.trim() }.partitionOnEmpty().map { parseMonkey(it) }
        val map = monkeys.associateBy { it.id }
        for (i in 0 until 20) {
            for (monkey in map.values) {
                monkey.turn(map)
//                println(monkey)
            }
        }
        map.values.forEach {
            println("monkey ${it.id} inspected ${it.inspected}")
        }
        return map.values.sortedBy { it.inspected }.reversed().take(2).map { it.inspected }.reduce { a, b -> a * b }
    }


    override fun solveb(input: List<String>): Any {
        val monkeys = input.map { it.trim() }.partitionOnEmpty().map { parseMonkey(it) }
        val map = monkeys.associateBy { it.id }
        val modulo = monkeys.map { it.test }.reduce { a, b -> a * b }
        for (i in 0 until 10000) {
            for (monkey in map.values) {
                monkey.turnb(map, modulo)
//                println(monkey)
            }
        }
        map.values.forEach {
            println("monkey ${it.id} inspected ${it.inspected}")
        }
        return map.values.sortedBy { it.inspected }.reversed().take(2).map { it.inspected }.reduce { a, b -> a * b }
    }

}

fun main() {
    println(
        D11.solveb(
            """
        Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
    """.trimIndent()
        )
    )

    println(D11.solveb("day11.txt"))

}

