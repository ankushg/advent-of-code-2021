package day07

import AdventOfCodeDaySolution
import day07.Day07.triangularFuelForPoint
import kotlin.math.abs

fun main() {
    Day07.main()
}

object Day07 : AdventOfCodeDaySolution<Int>(
    dayNumber = "07",
    part1ExpectedAnswer = 37,
    part2ExpectedAnswer = 168
) {
    override fun part1(input: List<String>): Int {
        return calculateMinFuel(input) { list, point ->
            list.linearFuelForPoint(point)
        }
    }

    override fun part2(input: List<String>): Int {
        return calculateMinFuel(input) { list, point ->
            list.triangularFuelForPoint(point)
        }
    }

    private fun calculateMinFuel(
        input: List<String>,
        fuelConsumptionFunction: (List<Int>, Int) -> Int
    ): Int {
        val positions = input.single()
            .split(",")
            .map { Integer.parseInt(it) }

        val start = positions.minOf { it }
        val end = positions.maxOf { it }

        return (start..end)
            .map { fuelConsumptionFunction.invoke(positions, it) }
            .minOf { it }
    }

    private fun List<Int>.linearFuelForPoint(point: Int): Int {
        return this.sumOf { abs(it - point) }
    }

    private fun List<Int>.triangularFuelForPoint(point: Int): Int {
        return this.sumOf { triangular(abs(it - point)) }
    }

    private fun triangular(n: Int): Int {
        return n * (n + 1) / 2
    }
}
