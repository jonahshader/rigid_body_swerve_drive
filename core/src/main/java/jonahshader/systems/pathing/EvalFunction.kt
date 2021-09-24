package jonahshader.systems.pathing

import jonahshader.systems.pathing.environment.Environment


typealias CarControlFun = (Environment) -> CarControlStep
class EvalFunction(private val env: Environment, private val maxTime: Int, private val timeStep: Float) {
    lateinit var carControlFun: CarControlFun

    fun runFullEpisode() : Float {
        env.reset()
        while (!env.isDone() && env.time < maxTime) {
            // get control step from fun
            env.update(carControlFun(env), timeStep)
        }

        return env.getCurrentFitness()
    }

    fun restart() {
        env.reset()
    }

    fun render() {
        env.draw()
    }

    fun getFitness() = env.getCurrentFitness()

    fun isDone() = env.isDone() || env.time >= maxTime

    fun runStep() {
        if (!env.isDone()) {
            env.update(carControlFun(env), timeStep)
        }
    }
}