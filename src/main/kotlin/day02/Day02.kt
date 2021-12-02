package day02

import AdventOfCodeDaySolution
import java.lang.IllegalArgumentException

fun main() {
    Day02.main()
}

object Day02 : AdventOfCodeDaySolution<Int>(
    dayNumber = "02",
    part1ExpectedAnswer = 150,
    part2ExpectedAnswer = 900
) {
    override fun part1(input: List<String>): Int {
        val state = State()
        input.forEach(state::apply1)
        return state.horizontalPosition * state.depth
    }

    override fun part2(input: List<String>): Int {
        val state = State()
        input.forEach(state::apply2)
        return state.horizontalPosition * state.depth    }

    class State(
        var depth: Int = 0,
        var horizontalPosition: Int = 0,
        var aim: Int = 0
    ) {
        fun apply1(command: String) {
            val (direction, quantityString) = command.split(" ")
            val quantity = quantityString.toInt()
            when (direction) {
                "forward" -> horizontalPosition +=quantity
                "down" -> depth += quantity
                "up" -> depth -= quantity
                else -> throw IllegalArgumentException("Cannot handle command: $command")
            }
        }

        fun apply2(command: String) {
            val (direction, unitsString) = command.split(" ")
            val units = unitsString.toInt()
            when (direction) {
                "forward" -> {
                    horizontalPosition += units
                    depth += (aim * units)
                }
                "down" -> {
                    aim += units
                }
                "up" -> {
                    aim -= units
                }
                else -> throw IllegalArgumentException("Cannot handle command: $command")
            }
        }
    }
}
