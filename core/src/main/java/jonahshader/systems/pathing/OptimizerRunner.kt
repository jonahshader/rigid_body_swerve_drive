package jonahshader.systems.pathing

import jonahshader.systems.pathing.sequenceoptimizers.SequenceOptimizer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

class OptimizerRunner(private val sequenceOptimizer: SequenceOptimizer) {
    private var currentEpoch = AtomicInteger(0)
    private val running = AtomicBoolean(false)

    /*
    TODO: run shit in another thread and have the main thread just keep displaying the current best
    sequence. use a lock or sumthin to prevent the best sequence from being written to while it is
    copied into the main thread.
     */

    fun start() {
        if (!running.get()) {
            thread {
                running.set(true)
                while (running.get()) {
                    sequenceOptimizer.runEpoch()
                    println(sequenceOptimizer.getFitness())
                    currentEpoch.incrementAndGet()
                }
            }
        }
    }

    fun stop() {
        running.set(false)
    }

    fun getCurrentBestSequence() : CarControlSequence = sequenceOptimizer.getCurrentBest()
}