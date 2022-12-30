package y2020

import advent2020.Solver2020

object D19 : Solver2020 {

    interface Rule {
        val id: Int
        fun matchesEntire(input: String, rules: Map<Int, Rule>) = branches(input, 0, rules).any { it.first && it.second == input.length }
        fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int>
        fun branches(input: String, from: Int, rules: Map<Int, Rule>): Sequence<Pair<Boolean, Int>>
    }

    data class OrRule(override val id: Int, val left: List<Int>, val right: List<Int>) : Rule {
        override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> {
            val l = AppendRule(id, left).matches(input, from, rules)
            if (l.first) return l
            return AppendRule(id, right).matches(input, from, rules)
        }

        override fun branches(input: String, from: Int, rules: Map<Int, Rule>): Sequence<Pair<Boolean, Int>> {
            return AppendRule(-1, left).branches(input, from, rules) + AppendRule(-1, right).branches(input, from, rules)
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

        override fun branches(input: String, from: Int, rules: Map<Int, Rule>): Sequence<Pair<Boolean, Int>> {
            val first = rules[ruleList.first()]!!
            val rem = ruleList.drop(1).map { rules[it]!! }
            return rem.fold(first.branches(input, from, rules)) {lastMatches, nextRule ->
                lastMatches.filter { it.first }.flatMap { nextRule.branches(input, it.second, rules) }
            }
        }
    }

    data class LiteralRule(override val id: Int, val literal: String) : Rule {
        override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> =
            Pair(input.regionMatches(from, literal, 0, literal.length), from + literal.length)

        override fun branches(input: String, from: Int, rules: Map<Int, Rule>): Sequence<Pair<Boolean, Int>> = sequenceOf(matches(input, from, rules))
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
        return msgs.count { rule0.matchesEntire(it,  allRules) }
    }

    override fun solveb(input: List<String>): Any {
        val (rules, msgs) = input.splitOnEmpty()
        val allRules = rules.map { parseRule(it) }.associateBy { it.id }.toMutableMap()
        //8: 42 | 42 8
        allRules[8] = OrRule(8, listOf(42), listOf(42, 8))

//        allRules[8] = object : Rule {
//            override val id: Int = 8
//            // 42 -> 9 14 | 10 1
//            // 9  -> 14 27 | 1 26
//            // 14 -> b
//            // 27 -> 1 6 | 14 18
//            // 1  -> a
//            // 10 -> 23 14 | 28 1
//            // 6  -> 14 14 | 1 14
//            //
//            // 42 -> b b a
//            override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> {
//                val rule42 = rules[42]!!
//                var lastMatch = Pair(false, from)
//                while (true) {
//                    val m = rule42.matches(input, lastMatch.second, rules)
//                    if(!m.first) break
//                    lastMatch = m
//                }
//                return lastMatch
//            }
//        }

        //11: 42 31 | 42 11 31
        allRules[11] = OrRule(11, listOf(42, 31), listOf(42, 11, 31))
//        allRules[11] = object : Rule {
//            override val id: Int = 11
//
//            override fun matches(input: String, from: Int, rules: Map<Int, Rule>): Pair<Boolean, Int> {
//                val rule42 = rules[42]!!
//                val rule31 = rules[31]!!
//                val n = rule42.matches(input, from, rules)
//                if(!n.first) {
//                    return n
//                }
//                val part2 = rule31.matches(input, n.second, rules)
//                if(part2.first) {
//                    return part2
//                }
//                return matches(input, n.second, rules)
//            }
//        }

        val rule0 = allRules[0]!!
        return msgs.count { rule0.matchesEntire(it,  allRules) }
    }

    override fun sample(): String = """
        0: 4 1 5
        1: 2 3 | 3 2
        2: 4 4 | 5 5
        3: 4 5 | 5 4
        4: "a"
        5: "b"

        ababbb
        bababa
        abbbab
        aaabbb
        aaaabbb
    """.trimIndent()

    override fun sampleB(): String = """
        42: 9 14 | 10 1
        9: 14 27 | 1 26
        10: 23 14 | 28 1
        1: "a"
        11: 42 31
        5: 1 14 | 15 1
        19: 14 1 | 14 14
        12: 24 14 | 19 1
        16: 15 1 | 14 14
        31: 14 17 | 1 13
        6: 14 14 | 1 14
        2: 1 24 | 14 4
        0: 8 11
        13: 14 3 | 1 12
        15: 1 | 14
        17: 14 2 | 1 7
        23: 25 1 | 22 14
        28: 16 1
        4: 1 1
        20: 14 14 | 1 15
        3: 5 14 | 16 1
        27: 1 6 | 14 18
        14: "b"
        21: 14 1 | 1 14
        25: 1 1 | 1 14
        22: 14 14
        8: 42
        26: 14 22 | 1 20
        18: 15 15
        7: 14 5 | 1 21
        24: 14 1

        bbabbbbaabaabba
        abbbbbabbbaaaababbaabbbbabababbbabbbbbbabaaaa
        babbbbaabbbbbabbbbbbaabaaabaaa
        aaabbbbbbaaaabaababaabababbabaaabbababababaaa
        bbbbbbbaaaabbbbaaabbabaaa
        bbbababbbbaaaaaaaabbababaaababaabab
        ababaaaaaabaaab
        ababaaaaabbbaba
        baabbaaaabbaaaababbaababb
        abbbbabbbbaaaababbbbbbaaaababb
        aaaaabbaabaaaaababaa
        aaaabbaaaabbaaa
        aaaabbaabbaaaaaaabbbabbbaaabbaabaaa
        babaaabbbaaabaababbaabababaaab
        aabbbbbaabbbaaaaaabbbbbababaaaaabbaaabba
    """.trimIndent()

}

fun main() {
//    D19.solveSample(2)
//    check( D19.solve("day19.txt") == 109 )
//    D19.solveSampleB(12)
    D19.solveb("day19.txt")
}