package day06

import AdventOfCodeDaySolution

fun main() {
    Day06.main()
}

object Day06 : AdventOfCodeDaySolution<Long>(
    dayNumber = "06",
    part1ExpectedAnswer = 5934,
    part2ExpectedAnswer = 26984457539
) {
    override fun part1(input: List<String>): Long {
        var fish = input.single().split(",").map { Fish(Integer.parseInt(it)) }

        repeat(times = 80) {
            fish = fish.flatMap { it.advance() }
        }

        return fish.size.toLong()
    }

    override fun part2(input: List<String>): Long {
        // hardcoded from question
        val maxAge = 8

        val fishCountByAgeMap = input.single().split(",")
            .map { Integer.parseInt(it) }
            .groupBy { it }
            .map { (key, value) -> key to value.size }
            .toMap()


        val fishCountByAge = LongArray(maxAge + 1) {
            fishCountByAgeMap[it]?.toLong() ?: 0L
        }

        val school = School(fishCountByAge)

        repeat(times = 256) {
            school.advance()
        }

        return school.totalFish
    }
}

class School(
    /**
     * Index represents timer value
     * value represents count of fish with given value
     */
    private val fish: LongArray
) {
    fun advance() {
        val reproducedFishCount = fish[0]

        for (i in fish.indices) {
            fish[i] = fish.getOrNull(i + 1) ?: 0
        }
        
        fish[6] = fish[6] + reproducedFishCount // fish that just reproduced
        fish[8] = fish[8] + reproducedFishCount // fish that were just born

    }

    val totalFish: Long get() = fish.sum()
}

@JvmInline
value class Fish(private val timer: Int) {
    fun advance(): List<Fish> {
        return if (timer == 0) {
            listOf(
                Fish(8), // new fish
                Fish(6) // this fish
            )
        } else {
            listOf(Fish(timer - 1))
        }
    }
}