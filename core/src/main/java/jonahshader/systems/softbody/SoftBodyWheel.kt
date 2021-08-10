package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.systems.physics.DrivingSurface
import jonahshader.systems.physics.WheelParams
import kotlin.math.PI

class SoftBodyWheel(private val softBody: SoftBody, private val pointMass: PointMass, wp: WheelParams, var surface: DrivingSurface): Component {
    private val wheelCircumference = (wp.wheelDiameter * PI).toFloat()
    private val maxSpeed = ((wp.motorMaxRPM / 60) * wheelCircumference) * wp.motorToWheelRatio
    private val maxWheelTorque = wp.motorMaxTorque / wp.motorToWheelRatio
    val stallForce = (maxWheelTorque / wheelCircumference)
    var maxStaticFrictionForce = surface.staticCoefficient * stallForce
    var maxKineticFrictionForce = surface.kineticCoefficient * stallForce

    override val force = Vector2()
    private val targetForce = Vector2()
    private val direction = Vector2(1f, 0f)

    fun updateTargetForce(targetForce: Vector2) {
        this.targetForce.set(targetForce)

        this.targetForce.set(targetForce)
        // if target force is non-zero, update direction with target force
        if (targetForce.len2() != 0.0f)
            direction.set(targetForce).nor()
    }

    override fun update(dt: Float) {
        // update static and kinetic
        // copy target force to force
        val sbCenter = softBody.getCenter()
        force.set(targetForce)
        val currentVelocity = Vector2(pointMass.velocity).rotateRad(-pointMass.getAngle(sbCenter)).dot(direction)
        val maxForceAtVelocity = calculateMaxForceAtVelocity(currentVelocity)

        // restrict force
        if (force.len2() > maxForceAtVelocity * maxForceAtVelocity) {
            force.setLength(maxForceAtVelocity)
        }

//        // apply sideways friction force
//        // get sideways force
//        val sidewaysDirection = Vector2(direction).rotateRad(PI.toFloat()/2f)
//        val sidewaysForce = Vector2(pointMass.force).dot(sidewaysDirection)
        val pointMassForceUnrotated = Vector2(pointMass.force).rotateRad(-pointMass.getAngle(sbCenter))

        // counteract force
        force.add(-pointMassForceUnrotated.x, 0f)

        // check friction
        if (force.len2() > maxStaticFrictionForce * maxStaticFrictionForce) {
            force.setLength(maxKineticFrictionForce)
            // TODO: drifting = true
        }

        force.rotateRad(pointMass.getAngle(sbCenter))


    }

    override fun render() {

    }

    override fun applyForce() {
        pointMass.force.add(force)
    }

    private fun calculateMaxForceAtVelocity(velocity: Float) = (1-(velocity / maxSpeed)) * stallForce
}