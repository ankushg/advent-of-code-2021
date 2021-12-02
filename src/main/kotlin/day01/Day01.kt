package day01

import AdventOfCodeDaySolution

fun main() {
    Day01.main()
}

object Day01 : AdventOfCodeDaySolution<Int>(
    dayNumber = "01",
    part1ExpectedAnswer = 7,
    part2ExpectedAnswer = 5
) {
    override fun part1(input: List<String>): Int {
        return input.asSequence()
            .asIntSequence()
            .countIncreasing()
    }

    override fun part2(input: List<String>): Int {
        return input.asSequence()
            .asIntSequence()
            .windowed(
                size = 3,
                step = 1
            ).map { it.sum() }
            .countIncreasing()
    }


    private fun Sequence<String>.asIntSequence() = this.map(String::toInt)

    private fun Sequence<Int>.countIncreasing() = this.windowed(
        size = 2,
        step = 1
    ).count { (prev, cur) -> cur > prev }
}
