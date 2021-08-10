package jonahshader.systems.physics

import com.badlogic.gdx.math.Vector2
import kotlin.math.PI

open class BLDCMotor(override val relativePos: Vector2, maxRPM: Float, maxTorque: Float, wheelDiameter: Float, motorToWheelRatio: Float) : Force {
    private val wheelCircumference = (wheelDiameter * PI).toFloat()
    private val maxSpeed = ((maxRPM / 60) * wheelCircumference) * motorToWheelRatio
    val maxForce = maxTorque / (wheelCircumference * motorToWheelRatio)
    override val force = Vector2()

    private val direction = Vector2()

    override fun update(dt: Float, parentVelocityAtRelativePos: Vector2) {
        val currentVelocity = parentVelocityAtRelativePos.dot(direction)
        val maxForceAtVelocity = calculateMaxForceAtVelocity(currentVelocity)

        if (force.len2() > maxForceAtVelocity * maxForceAtVelocity) {
            force.setLength(maxForceAtVelocity)
            println("limited force to $maxForceAtVelocity")
        }


    }

    fun setTargetForce(force: Vector2) {
        this.force.set(force)
        setDirection(force)
    }

    private fun setDirection(direction: Vector2) {
        this.direction.set(direction)
        this.direction.nor()
    }

    private fun calculateMaxForceAtVelocity(velocity: Float) = (1-(velocity / maxSpeed)) * maxForce

}