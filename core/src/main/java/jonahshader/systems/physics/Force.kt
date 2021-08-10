package jonahshader.systems.physics

import com.badlogic.gdx.math.Vector2

interface Force{
    val relativePos: Vector2
    val force: Vector2
    fun update(dt: Float, parentVelocityAtRelativePos: Vector2)
    fun calculateTorqueAroundRigidBodyCenter() = relativePos.crs(force)
}