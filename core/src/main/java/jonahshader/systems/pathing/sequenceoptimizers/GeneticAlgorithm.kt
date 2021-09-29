package jonahshader.systems.pathing.sequenceoptimizers

import jonahshader.systems.pathing.CarControlSequence
import jonahshader.systems.pathing.CarControlStep
import jonahshader.systems.pathing.EvalFunction
import jonahshader.systems.pathing.environment.Environment
import jonahshader.systems.pathing.playbackFunctor
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock
import kotlin.math.max
import kotlin.math.pow

class GeneticAlgorithm(private val env: Environment, private val maxTimeSteps: Int,
                       private var timeStep: Float, private val popSize: Int,
                       private val maxMutationRate: Float, private val rand: Random = Random()) : SequenceOptimizer{
    private val evaluator = EvalFunction(env, maxTimeSteps, timeStep)
    private val sequences = mutableListOf<CarControlSequence>()
    private val fitnesses = mutableListOf<Float>()

    private val copyLock = ReentrantLock()

    init {
        for (j in 0 until popSize) {
            val s = CarControlSequence()
            s.generateRandom(rand, maxTimeSteps)
            sequences += s
            fitnesses += 0.0f
        }

    }

    private var bestSequence = CarControlSequence()
    private var bestFitness = 0f

    // really run generation but interface needs runEpoch as it is more general
    override fun runEpoch() {
        var maxFitness = 0f
        var maxFitnessIndex = 0
        for (i in sequences.indices) {
            evaluator.carControlFun = playbackFunctor(sequences[i])
            val fitness = evaluator.runFullEpisode()
            fitnesses[i] = fitness
            if (fitness > maxFitness) {
                maxFitness = fitness
                maxFitnessIndex = i
            }
        }

        // copy and mutate
        copyLock.lock()
        bestSequence = CarControlSequence(sequences[maxFitnessIndex])
        copyLock.unlock()
        sequences[0].set(bestSequence)
        for (i in 1 until sequences.size) {
            val it = sequences[i]
            it.set(bestSequence)
            it.mutate(rand, maxMutationRate * (i.toFloat() / sequences.size).pow(2))
        }

        bestFitness = maxFitness
    }

    override fun getCurrentBest(): CarControlSequence {
        copyLock.lock()
        val copy = CarControlSequence(bestSequence)
        copyLock.unlock()
        return copy
    }

    override fun getMaxTimeSteps(): Int {
        return maxTimeSteps
    }

    override fun getTimeStep(): Float {
        return timeStep
    }

    override fun getEnv(): Environment {
        return env
    }

    override fun getFitness(): Float {
        return bestFitness
    }
}