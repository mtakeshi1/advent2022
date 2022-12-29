package y2022

import common.Solver2022

object D10 : Solver2022 {

    interface Cmd {
        fun apply(x: Int): Pair<Int, Cmd>
        fun nextCmd(queue: List<Cmd>): Pair<Cmd, List<Cmd>>
    }

    object NoOp : Cmd {
        override fun apply(x: Int): Pair<Int, Cmd> = Pair(x, this)
        override fun nextCmd(queue: List<Cmd>) = Pair(queue.firstOrNull() ?: NoOp, queue.drop(1))
    }

    data class Inc(var turns: Int = 1, val inc: Int) : Cmd {
        override fun apply(x: Int): Pair<Int, Cmd> =
            if (turns > 0) Pair(x, Inc(turns - 1, inc))
            else Pair(x + inc, NoOp)

        override fun nextCmd(queue: List<Cmd>) = Pair(this, queue)
    }

    class CPU(val x: Int = 2, val turn: Int = 1, val cmd: Cmd? = null, val pendingCommands: List<Cmd>) {

        fun tick(): CPU {
            if (pendingCommands.isEmpty() && cmd == null) {
                return this
            }
            //beggining of turn $turn
            var nx = x;
            if (cmd != null) {

            } else {

            }
            TODO()
        }


    }


    fun runCommands(commands: List<String>): List<Pair<Int, Int>> {
        var x: Int = 1
        var turn: Int = 0
        var r: List<Pair<Int, Int>> = emptyList()

        var pendingCommands: List<Cmd> = commands.map {
            when (it) {
                "noop" -> NoOp
                else -> Inc(inc = it.split(" ")[1].toInt())
            }
        }
        var currentCommand: Cmd = NoOp
        while (pendingCommands.isNotEmpty() || currentCommand != NoOp) {
            // begginning of cycle
            r += Pair(turn, x)
            val next = currentCommand.apply(x)
            x = next.first
            currentCommand = next.second
            val nextQueue = currentCommand.nextCmd(pendingCommands)
            currentCommand = nextQueue.first
            pendingCommands = nextQueue.second

            turn++
        }
        r += Pair(turn, x)
        return r.drop(1)
    }

    fun strength(p: Pair<Int, Int>) = p.first * p.second

    override fun solve(input: List<String>): Any {
        val r = runCommands(input)
        var i = 19;
        var sum = 0;
        while (i < r.size) {
            sum += strength(r[i])
            i += 40
        }
        return sum
    }

    override fun solveb(input: List<String>): Any {
        val r = runCommands(input)
        for (row in 0 until 6) {
            println((0 until 40).map { pos ->
                val x = r[row * 40 + pos].second
                if(x >= pos - 1 && x <= pos + 1) '#' else '.'
            }.joinToString(separator = ""))
        }
        return ""
    }

}

fun main() {
    D10.solve(
        """
        noop
        addx 3
        addx -5
    """.trimIndent()
    )

    println(
        D10.solveb(
            """
        addx 15
        addx -11
        addx 6
        addx -3
        addx 5
        addx -1
        addx -8
        addx 13
        addx 4
        noop
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx 5
        addx -1
        addx -35
        addx 1
        addx 24
        addx -19
        addx 1
        addx 16
        addx -11
        noop
        noop
        addx 21
        addx -15
        noop
        noop
        addx -3
        addx 9
        addx 1
        addx -3
        addx 8
        addx 1
        addx 5
        noop
        noop
        noop
        noop
        noop
        addx -36
        noop
        addx 1
        addx 7
        noop
        noop
        noop
        addx 2
        addx 6
        noop
        noop
        noop
        noop
        noop
        addx 1
        noop
        noop
        addx 7
        addx 1
        noop
        addx -13
        addx 13
        addx 7
        noop
        addx 1
        addx -33
        noop
        noop
        noop
        addx 2
        noop
        noop
        noop
        addx 8
        noop
        addx -1
        addx 2
        addx 1
        noop
        addx 17
        addx -9
        addx 1
        addx 1
        addx -3
        addx 11
        noop
        noop
        addx 1
        noop
        addx 1
        noop
        noop
        addx -13
        addx -19
        addx 1
        addx 3
        addx 26
        addx -30
        addx 12
        addx -1
        addx 3
        addx 1
        noop
        noop
        noop
        addx -9
        addx 18
        addx 1
        addx 2
        noop
        noop
        addx 9
        noop
        noop
        noop
        addx -1
        addx 2
        addx -37
        addx 1
        addx 3
        noop
        addx 15
        addx -21
        addx 22
        addx -6
        addx 1
        noop
        addx 2
        addx 1
        noop
        addx -10
        noop
        noop
        addx 20
        addx 1
        addx 2
        addx 2
        addx -6
        addx -11
        noop
        noop
        noop
    """.trimIndent()
        )
    )

    println(D10.solveb("day10.txt"))

}