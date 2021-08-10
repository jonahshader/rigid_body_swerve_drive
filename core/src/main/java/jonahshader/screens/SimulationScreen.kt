package jonahshader.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import jonahshader.RigidBodyApp
import jonahshader.systems.physics.BLDCMotor
import jonahshader.systems.physics.RigidBody
import ktx.app.KtxScreen
import ktx.graphics.begin

class SimulationScreen: KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(320f, 240f, camera)
    private val rigidBody = RigidBody(Vector2(8f, 8f), 20f, Vector2())

    init {
        with (rigidBody) {
            val motor1 = BLDCMotor(Vector2(4f, 0f), 600f, 1.2f, .5f, 10f)
            motor1.setTargetForce(Vector2(0f, 5f))
            this += motor1

//            val motor2 = BLDCMotor(Vector2(4f, 0f), 60f, 3f, .5f)
//            motor2.setTargetForce(Vector2(0f, 5f))
//            this += motor2
        }
    }

    override fun show() {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun render(delta: Float) {
        rigidBody.update(delta)
        ScreenUtils.clear(.1f, .1f, .1f, 1f)
        viewport.apply()
        RigidBodyApp.batch.begin(camera)
        rigidBody.draw()
        RigidBodyApp.batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}