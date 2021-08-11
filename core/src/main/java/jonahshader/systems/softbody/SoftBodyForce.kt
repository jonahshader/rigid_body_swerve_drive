package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.RigidBodyApp
import ktx.math.plus
import ktx.math.times

open class SoftBodyForce(private val pointMass: PointMass): Component {
    override val force = Vector2()
    override fun update(sbCenter: Vector2, dt: Float) {
        globalForce.set(force).rotateRad(pointMass.getAngle(sbCenter))
    }

    override fun render() {
        RigidBodyApp.shapeDrawer.setColor(1f, 0f, 0f, 1f)
        RigidBodyApp.shapeDrawer.line(pointMass.position, pointMass.position + globalForce, .1f)
    }

    override fun applyForce() {
        pointMass.force.add(globalForce)
    }

    private val globalForce = Vector2()
}