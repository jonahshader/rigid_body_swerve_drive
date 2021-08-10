package jonahshader.systems.physics

import com.badlogic.gdx.math.Vector2

class SimpleForce(override val relativePos: Vector2) : Force {
    override val force = Vector2()

    override fun update(dt: Float, parentVelocityAtRelativePos: Vector2) {}

//    fun setForce(newForce: Vector2) {
//        force.set(newForce)
//    }
}