package jonahshader.systems.physics

import com.badlogic.gdx.math.Vector2
import kotlin.math.PI

class Wheel(override val relativePos: Vector2, private val wp: WheelParams, var surface: DrivingSurface): Force {
    private val wheelCircumference = (wp.wheelDiameter * PI).toFloat()
    private val maxSpeed = ((wp.motorMaxRPM / 60) * wheelCircumference) * wp.motorToWheelRatio
    private val maxWheelTorque = wp.motorMaxTorque / wp.motorToWheelRatio
    val stallForce = (maxWheelTorque / wheelCircumference)
    var load = 0f
    var maxStaticFrictionForce = surface.staticCoefficient * stallForce
    var maxKineticFrictionForce = surface.kineticCoefficient * stallForce
    override val force = Vector2()
    private val targetForce = Vector2()

    private val direction = Vector2(0f, 1f) // initial direction is forward



    fun updateStats(load: Float, targetForce: Vector2) {
        this.load = load
        this.targetForce.set(targetForce)
        // if target force is non-zero, update direction with target force
        if (targetForce.len2() != 0.0f)
            direction.set(targetForce).nor()
    }

    override fun update(dt: Float, parentVelocityAtRelativePos: Vector2) {
        // update static and dynamic
        // copy target force to force
        force.set(targetForce)
        val currentVelocity = Vector2(parentVelocityAtRelativePos).dot(direction)
        val maxForceAtVelocity = calculateMaxForceAtVelocity(currentVelocity)

        // restrict force
        if (force.len2() > maxForceAtVelocity * maxForceAtVelocity) {
            force.setLength(maxForceAtVelocity)
        }

        // apply sideways friction force
        // get sideways velocity
        val sidewaysDirection = Vector2(direction).rotateRad(PI.toFloat()/2f)
        val sidewaysVelocity = Vector2(parentVelocityAtRelativePos).dot(sidewaysDirection)
    }


    private fun calculateMaxForceAtVelocity(velocity: Float) = (1-(velocity / maxSpeed)) * stallForce

}