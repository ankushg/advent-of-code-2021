package day00

import AdventOfCodeDaySolution

fun main() {
    Day00.main()
}

object Day00 : AdventOfCodeDaySolution<Int>(
    dayNumber = "00",
    part1ExpectedAnswer = 0,
    part2ExpectedAnswer = null
) {
    override fun part1(input: List<String>): Int {
        return input.size
    }

    override fun part2(input: List<String>): Int {
        return input.size
    }
}
