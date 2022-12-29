package y2020

import advent2020.Solver2020

object D19 : Solver2020 {

    interface Rule {
        val id: Int
        fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int>
    }

    data class OrRule(override val id: Int, val left: List<Int>, val right: List<Int>) : Rule {
        override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> {
            val l = AppendRule(id, left).matches(input, from, rules)
            if (l.first) return l
            return AppendRule(id, right).matches(input, from, rules)
        }
    }

    data class AppendRule(override val id: Int, val ruleList: List<Int>) : Rule {
        override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> {
            val eaten = ruleList.fold(from) { prev, ruleNum ->
                val p = rules[ruleNum]!!.matches(input, prev, rules)
                if (!p.first) return Pair(false, from)
                p.second
            }
            return Pair(true, eaten)
        }
    }

    data class LiteralRule(override val id: Int, val literal: String) : Rule {
        override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> =
            Pair(input.regionMatches(from, literal, 0, literal.length), from + literal.length)
    }

    fun parseRule(line: String): Rule {
        val (idStr, ruleText) = line.split(":")
        return if (ruleText.contains("|")) {
            val (left, right) = ruleText.split("|")
            OrRule(idStr.toInt(), left.trim().split(" ").ints(), right.trim().split(" ").ints())
        } else if (ruleText.contains("\"")) {
            val lit = ruleText.trim().replace("\"", "")
            LiteralRule(idStr.toInt(), lit)
        } else {
            AppendRule(idStr.toInt(), ruleText.trim().split(" ").toList().ints())
        }

    }

    override fun solve(input: List<String>): Any {
        val (rules, msgs) = input.splitOnEmpty()
        val allRules = rules.map { parseRule(it) }.associateBy { it.id }
        val rule0 = allRules[0]!!
        return msgs.filter { rule0.matches(it, 0, allRules).first }
    }

    override fun sample(): String = """
        0: 4 1 5
        1: 2 3 | 3 2
        2: 4 4 | 5 5
        3: 4 5 | 5 4
        4: "a"
        5: "b"

        aaaabbb
    """.trimIndent()

}

fun main() {
    D19.solveSample(2)
}