package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.systems.physics.DrivingSurface
import jonahshader.systems.physics.WheelParams
import kotlin.math.PI

class SoftBodyWheel(private val softBody: SoftBody, private val pointMass: PointMass, wp: WheelParams, var surface: DrivingSurface): SoftBodyForce(pointMass) {
    private val wheelCircumference = (wp.wheelDiameter * PI).toFloat()
    private val maxSpeed = ((wp.motorMaxRPM / 60) * wheelCircumference) * wp.motorToWheelRatio
    private val maxWheelTorque = wp.motorMaxTorque / wp.motorToWheelRatio
    val stallForce = (maxWheelTorque / wheelCircumference)
    var maxStaticFrictionForce = surface.staticCoefficient * stallForce
    var maxKineticFrictionForce = surface.kineticCoefficient * stallForce

    private val targetForce = Vector2()
    private val direction = Vector2(1f, 0f)

    fun updateTargetForce(targetForce: Vector2) {
        this.targetForce.set(targetForce)

        this.targetForce.set(targetForce)
        // if target force is non-zero, update direction with target force
        if (targetForce.len2() != 0.0f)
            direction.set(targetForce).nor()
    }

    override fun update(sbCenter: Vector2, dt: Float) {
        // copy target force into force
        force.set(targetForce)


        // transform everything into local space. force is already in local space. need to unrotate massPoint things
        val directionAngle = direction.angleRad()
        val pointMassAngle = pointMass.getAngle(sbCenter)
        val velocityLocal = Vector2(pointMass.velocity).rotateRad(-pointMassAngle)
        val springForceLocal = Vector2(pointMass.force).rotateRad(-pointMassAngle)
        val lateralDirection = Vector2(direction).rotateRad(PI.toFloat()/2f)

        val velocityInWheelDirection = velocityLocal.dot(direction)
        val maxForceAtVelocity = calculateMaxForceAtVelocity(velocityInWheelDirection)

        // restrict force
        if (force.len2() > maxForceAtVelocity * maxForceAtVelocity) {
            force.setLength(maxForceAtVelocity)
        }

//        var requiredLateralFrictionForce = lateralDirection.dot(springForceLocal)
//        requiredLateralFrictionForce += lateralDirection.dot(velocityLocal) * pointMass.mass / dt
        val requiredLateralFrictionForce = lateralDirection.dot(velocityLocal) * pointMass.mass * .25f / dt

        val lateralForceVector = Vector2(lateralDirection).scl(requiredLateralFrictionForce)
        force.sub(lateralForceVector)

        // check friction
        if (force.len2() > maxStaticFrictionForce * maxStaticFrictionForce) {
            force.setLength(maxKineticFrictionForce)
        }
//        force.rotateRad(pointMassAngle)
        super.update(sbCenter, dt)

//        // update static and kinetic
//        // copy target force to force
//        val sbCenter = softBody.getCenter()
//        force.set(targetForce)
//        val currentVelocity = Vector2(pointMass.velocity).rotateRad(-pointMass.getAngle(sbCenter)).dot(direction)
//        val maxForceAtVelocity = calculateMaxForceAtVelocity(currentVelocity)
//
//        // restrict force
//        if (force.len2() > maxForceAtVelocity * maxForceAtVelocity) {
//            force.setLength(maxForceAtVelocity)
//        }
//
////        // apply sideways friction force
////        // get sideways force
////        val sidewaysDirection = Vector2(direction).rotateRad(PI.toFloat()/2f)
////        val sidewaysForce = Vector2(pointMass.force).dot(sidewaysDirection)
//        val pointMassForceUnrotated = Vector2(pointMass.force).rotateRad(-pointMass.getAngle(sbCenter))
//
//        // counteract force
//        force.add(-pointMassForceUnrotated.x, 0f)
//
//        // check friction
//        if (force.len2() > maxStaticFrictionForce * maxStaticFrictionForce) {
//            force.setLength(maxKineticFrictionForce)
//            // TODO: drifting = true
//        }
//
//        force.rotateRad(pointMass.getAngle(sbCenter))
//

    }

    private fun calculateMaxForceAtVelocity(velocity: Float) = (1-(velocity / maxSpeed)) * stallForce
}