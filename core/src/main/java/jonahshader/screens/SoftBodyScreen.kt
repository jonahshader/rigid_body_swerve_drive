package jonahshader.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FillViewport
import jonahshader.RigidBodyApp
import jonahshader.systems.softbody.PointMass
import jonahshader.systems.softbody.SoftBody
import jonahshader.systems.softbody.SpringConstants
import ktx.app.KtxScreen
import ktx.graphics.use

class SoftBodyScreen: KtxScreen {
    val iterations: Int = 1 shl 2
    private val mouseForce = 100f
    private val camera = OrthographicCamera()
    private val viewport = FillViewport(90f, 60f, camera)
    private lateinit var sb: SoftBody
    private lateinit var selectedPointMass: PointMass

    private val alwaysRunning = true

    init {
        val impulse = 5f
        val sc = SpringConstants((1 shl 12).toFloat(), 10f)
        sb = SoftBody()
        sb.addFullyConnectedPoint(PointMass(.5f, Vector2(-.5f, 0f)), sc)
        sb.addFullyConnectedPoint(PointMass(.5f, Vector2(0f, 0f)), sc)
        sb.addFullyConnectedPoint(PointMass(.5f, Vector2(.5f, 0f)), sc)
        sb.pointMasses[0].velocity.y = 1f
//        for (y in -30..30) for (x in -30..30) {
//            if (x*x+y*y < 15*15) {
//                val newPointMass = PointMass(10f, Vector2(x.toFloat(), y.toFloat()))
//                sb.addPointConnectedRadius(newPointMass, 1.9f, sc)
//                newPointMass.addImpulse(Vector2(y * impulse, x * -impulse))
////                if (x == -3 && y == -3) {
////                    newPoint.addImpulse(Vector2(0f, 500f))
////                } else if (x == 3 && y == 3) {
////                    newPoint.addImpulse(Vector2(0f, -500f))
////                }
//            }
//
//        }


//        val ringPointCount = 240
//        for (i in 0 until ringPointCount) {
//            val f = (i / ringPointCount.toFloat()) * PI.toFloat() * 1
//            sb.addPointConnectedRadius(Point(1f, Vector2(cos(f) * 10, sin(f) * 10)), 5f, sc)
//        }

//        for (i in 0 until 120) {
//            sb.addPointConnectedRadius(Point(1f, Vector2(randPlusMinusRange(30f), randPlusMinusRange(30f))), Math.random().toFloat() * 30, sc)
//        }

        selectedPointMass = sb.pointMasses[0]
    }

    override fun show() {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun render(delta: Float) {
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

        println("average velocity: ${sb.getAverageVelocity()}")
        println("average angular vel: ${sb.getRotationalVelocityAroundCenter()}")
        println("kinetic energy pointwise: ${sb.getTotalKineticEnergyPointWise()}")
        println("kinetic energy bodywise: ${sb.getTotalKineticEnergyBodyWise()}")
        println("angular kinetic energy bodywise: ${sb.getAngularKineticEnergyBodyWise()}")
        println("translational kinetic energy: ${sb.getTranslationalKineticEnergyBodyWise()}")


        ScreenUtils.clear(.1f, .1f, .1f, 1f)
        viewport.apply()
        RigidBodyApp.batch.use(camera) {
            sb.render()
        }
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    fun randPlusMinusRange(range: Float): Float = ((Math.random() - .5) * 2 * range).toFloat()
}