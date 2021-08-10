package jonahshader.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import jonahshader.RigidBodyApp
import jonahshader.systems.physics.Car
import ktx.app.KtxScreen
import ktx.graphics.begin

class CarSimScreen: KtxScreen {
    private val camera = OrthographicCamera()
    private val viewport = FillViewport(90f, 60f, camera)
    private val car = Car()

    override fun show() {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun render(delta: Float) {
        val steer =
            (if (Gdx.input.isKeyPressed(Input.Keys.A)) -1f else 0f) +
                    (if (Gdx.input.isKeyPressed(Input.Keys.D)) 1f else 0f)
        val throttle =
            (if (Gdx.input.isKeyPressed(Input.Keys.W)) 1f else 0f) +
                    (if (Gdx.input.isKeyPressed(Input.Keys.S)) -1f else 0f)

        car.setDrive(steer * .5f, throttle * car.maxForce * 2f)
        car.update(delta)
        ScreenUtils.clear(.1f, .1f, .1f, 1f)
        viewport.apply()
        RigidBodyApp.batch.begin(camera)
        car.draw()
        RigidBodyApp.batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }
}