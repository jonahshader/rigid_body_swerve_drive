package jonahshader.systems.pathing

import java.util.*

class CarControlStep(var steer: Float, var throttle: Float, var balance: Float) {
    constructor(toCopy: CarControlStep) : this(toCopy.steer, toCopy.throttle, toCopy.balance)
    companion object {
        fun generateRandom(rand: Random) = CarControlStep(rand.nextFloat() * 2 - 1, rand.nextFloat() * 1.2f - .2f, -.5f)
//fun generateRandom(rand: Random) = CarControlStep(rand.nextFloat() * 2 - 1, rand.nextFloat(), rand.nextFloat() * 2 - 1)
    }

    fun set(carControlStep: CarControlStep) {
        steer = carControlStep.steer
        throttle = carControlStep.throttle
        balance = carControlStep.balance
    }

    fun mutate(rand: Random, amount: Float) {
        steer += (rand.nextFloat() * 2 - 1) * amount
        throttle += (rand.nextFloat() * 2 - 1) * amount
//        balance += (rand.nextFloat() * 2 - 1) * amount
        restrict()
    }

    fun restrict() {
        steer = steer.coerceIn(-1f, 1f)
        throttle = throttle.coerceIn(-1f, 1f)
        balance = balance.coerceIn(-1f, 1f)
    }
}