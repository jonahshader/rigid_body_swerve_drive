package jonahshader.systems.pathing

import java.util.*

class CarControlSequence {
    constructor()
    // copy constructor
    constructor(toCopy: CarControlSequence) {
        toCopy.sequence.forEach {
            sequence += CarControlStep(it)
        }
    }

    val sequence = mutableListOf<CarControlStep>()

    fun set(carControlSequence: CarControlSequence) {
        assert(carControlSequence.sequence.size == sequence.size)
        for (i in 0 until sequence.size) {
            sequence[i].set(carControlSequence.sequence[i])
        }
    }

    fun mutate(rand: Random, amount: Float) {
        sequence.forEach { it.mutate(rand, amount) }
    }

    fun generateRandom(rand: Random, size: Int) {
        sequence.clear()
        for (i in 0 until size)
            sequence += CarControlStep.generateRandom(rand)
    }
}