package jonahshader.systems.pathing.sequenceoptimizers

import jonahshader.systems.pathing.CarControlSequence
import jonahshader.systems.pathing.environment.Environment

interface SequenceOptimizer {
    fun runEpoch()
    fun getCurrentBest() : CarControlSequence
    fun getMaxTimeSteps() : Int
    fun getTimeStep() : Float
    fun getEnv() : Environment
    fun getFitness() : Float
}