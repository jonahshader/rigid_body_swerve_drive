package jonahshader.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FillViewport
import jonahshader.RigidBodyApp
import jonahshader.systems.softbody.PointMass
import jonahshader.systems.softbody.SoftBody
import jonahshader.systems.softbody.SoftBodyCar
import jonahshader.systems.softbody.SpringConstants
import jonahshader.systems.ui.TextRenderer
import ktx.app.KtxInputAdapter
import ktx.app.KtxScreen
import ktx.graphics.use
import ktx.math.plus
import ktx.math.times
import kotlin.math.pow

class SoftBodyCarScreen: KtxScreen {
    val iterations: Int = 1 shl 0
    private val mouseForce = 100f
    private val camera = OrthographicCamera()
    private val viewport = FillViewport(90f, 60f, camera)
    private val sb = SoftBodyCar(Vector3(1f, 2f, .8f), 50f)
    private lateinit var selectedPointMass: PointMass
    private var steerScalar = .5f

    private val alwaysRunning = true

    init {
        selectedPointMass = sb.pointMasses[0]

        Gdx.input.inputProcessor = object : KtxInputAdapter {
            override fun scrolled(amountX: Float, amountY: Float): Boolean {
                steerScalar *= .5f.pow(amountY)
                return true
            }
        }
    }

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

        if (steer != 0f && throttle == 0f) {
            sb.setDrive(steer * steerScalar * .5f, .5f, -0f)
        } else {
            sb.setDrive(steer * steerScalar, throttle, -0f)
        }

//        val dt = if (delta == 0f) (1/60f) else delta
        if (Gdx.input.justTouched()) {
            val mousePos = viewport.unproject(Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat()))
            var distance = Vector2.len(mousePos.x - selectedPointMass.position.x, mousePos.y - selectedPointMass.position.y)

            for (p in sb.pointMasses) {
                val dist = Vector2.len(mousePos.x - p.position.x, mousePos.y - p.position.y)
                if (dist < distance) {
                    distance = dist
                    selectedPointMass = p
                }

            }
        }


        if (Gdx.input.isTouched) {
            val mousePos = viewport.unproject(Vector2(Gdx.input.x.toFloat(), Gdx.input.y.toFloat()))
            mousePos.sub(selectedPointMass.position)
            if (Gdx.input.isKeyPressed(Input.Keys.F)) {
                selectedPointMass.velocity.setZero()
            } else {
                selectedPointMass.force.add(mousePos.scl(mouseForce))
            }
        }

        val dt = 1/165f
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || alwaysRunning) {
            for (i in 0 until iterations) {
                sb.update(dt / iterations)
                if (Gdx.input.isKeyPressed(Input.Keys.C)) {
                    sb.setAverageVelocity(Vector2.Zero)
                }
            }
        }

//        println("average velocity: ${sb.getAverageVelocity()}")
//        println("average angular vel: ${sb.getRotationalVelocityAroundCenter()}")
//        println("kinetic energy pointwise: ${sb.getTotalKineticEnergyPointWise()}")
//        println("kinetic energy bodywise: ${sb.getTotalKineticEnergyBodyWise()}")
//        println("angular kinetic energy bodywise: ${sb.getAngularKineticEnergyBodyWise()}")
//        println("translational kinetic energy: ${sb.getTranslationalKineticEnergyBodyWise()}")

//        camera.position.x = camera.position.x * .99f + sb.getCenter().x * .01f
//        camera.position.y = camera.position.y * .99f + sb.getCenter().y * .01f
        camera.position.x = sb.getCenter().x
        camera.position.y = sb.getCenter().y

        camera.update()

        ScreenUtils.clear(.1f, .1f, .1f, 1f)
        viewport.apply()
        RigidBodyApp.batch.use(camera) {
            RigidBodyApp.shapeDrawer.setColor(1f, 1f, 1f, 1f)
//            RigidBodyApp.shapeDrawer.line(sb.getCenter(), Vector2(camera.position.x, camera.position.y), 0.03f)
            RigidBodyApp.shapeDrawer.line(Vector2(sb.getCenter()), Vector2(sb.getCenter()) + sb.getAverageVelocity(), 0.03f)
            sb.render()

            TextRenderer.begin(RigidBodyApp.batch, viewport, TextRenderer.Font.LIGHT, 1f, 0f)
            TextRenderer.drawText(sb.getCenter().x - 2, sb.getCenter().y - 2, "Steer factor: $steerScalar")
            TextRenderer.end()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    fun randPlusMinusRange(range: Float): Float = ((Math.random() - .5) * 2 * range).toFloat()
}