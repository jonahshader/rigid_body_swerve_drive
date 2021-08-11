package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.RigidBodyApp
import ktx.math.plusAssign

class PointMass(val mass: Float, val position: Vector2) {
    val originalPosition = Vector2(position)
    val velocity = Vector2()
    private val acceleration = Vector2()
    val force = Vector2()
    var angleFromCenterOfSoftBody = 0f

    fun update(dt: Float) {
        // avoiding new memory allocations
        force.scl(1/mass)
        acceleration.set(force)
        acceleration.scl(dt)
        velocity += acceleration
        position.add(velocity.x * dt, velocity.y * dt)

        force.setZero()
    }

    // newton seconds
    fun addImpulse(impulse: Vector2) {
        velocity.add(impulse.scl(1/mass))
    }

    fun render() {
        RigidBodyApp.shapeDrawer.setColor(1f, 1f, 1f, 1f)
        RigidBodyApp.shapeDrawer.filledCircle(position, .1f)
    }

    fun getAngle(center: Vector2): Float = Vector2(position).sub(center).angleRad() - angleFromCenterOfSoftBody
}