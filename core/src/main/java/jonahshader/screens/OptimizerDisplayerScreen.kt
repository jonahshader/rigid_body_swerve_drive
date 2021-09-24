package jonahshader.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import jonahshader.RigidBodyApp
import jonahshader.systems.pathing.CarControlSequence
import jonahshader.systems.pathing.EvalFunction
import jonahshader.systems.pathing.OptimizerRunner
import jonahshader.systems.pathing.environment.Environment
import jonahshader.systems.pathing.playbackFunctor
import jonahshader.systems.pathing.sequenceoptimizers.SequenceOptimizer
import ktx.app.KtxScreen
import ktx.app.clearScreen
import ktx.graphics.use

class OptimizerDisplayerScreen(sequenceOptimizer: SequenceOptimizer) : KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport: FitViewport

    private val runner = OptimizerRunner(sequenceOptimizer)
    private val displayEnv = Environment(sequenceOptimizer.getEnv())
    private val evaluator = EvalFunction(displayEnv, sequenceOptimizer.getMaxTimeSteps(), sequenceOptimizer.getTimeStep())
    private var bestSequence = CarControlSequence()

    init {
        val envBoundingBox = displayEnv.getBoundingBox()
        viewport = FitViewport(envBoundingBox.width + 1f, envBoundingBox.height + 1f, camera)
        camera.position.set(envBoundingBox.x + envBoundingBox.width/2f, envBoundingBox.y + envBoundingBox.height/2f, 0f)
        camera.update()

        runner.start()
    }

    override fun render(delta: Float) {

        if (evaluator.isDone() || bestSequence.sequence.size == 0) {
            bestSequence = runner.getCurrentBestSequence()
            evaluator.carControlFun = playbackFunctor(bestSequence)
        }

        camera.update()
        ScreenUtils.clear(.1f, .1f, .1f, 1f)
        viewport.apply()

        RigidBodyApp.batch.use(camera) {
            if (bestSequence.sequence.size > 0) {
                evaluator.runStep()


                displayEnv.draw()
            } else {
                println("still processing")
            }
        }


    }

    override fun show() {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}