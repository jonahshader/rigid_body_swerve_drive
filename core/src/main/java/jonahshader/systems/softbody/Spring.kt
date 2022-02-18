package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.RigidBodyApp
import ktx.math.minus

class Spring(val startPointMass: PointMass, val endPointMass: PointMass,
             val targetLength: Float = (startPointMass.position - endPointMass.position).len(),
             val sc: SpringConstants) {
    private var startToEnd = Vector2(endPointMass.position).sub(startPointMass.position)
    private var pLength = targetLength
    private val direction = Vector2(startToEnd).nor()

    fun update(dt: Float) {
        startToEnd.set(endPointMass.position)
        startToEnd.sub(startPointMass.position)
        direction.set(startToEnd).nor()
        val length = startToEnd.len()
        val lengthVelocity = (length - pLength) / dt // in meters per second
        pLength = length
        val error = length - targetLength

        var force = (error * -sc.newtonsPerMeter) + (lengthVelocity * -sc.newtonsPerMeterPerSecond)

        endPointMass.force.add(direction.x * force, direction.y * force)
        startPointMass.force.add(-direction.x * force, -direction.y * force)
    }

    fun render() {
        RigidBodyApp.shapeDrawer.setColor(.5f, .5f, .5f, 1f)
        RigidBodyApp.shapeDrawer.line(startPointMass.position, endPointMass.position, 0.05f)
    }
}