package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.RigidBodyApp
import jonahshader.systems.physics.DrivingSurface
import jonahshader.systems.physics.WheelParams
import jonahshader.systems.softbody.SoftBodyCar.Companion.GRAVITY
import kotlin.math.PI
import kotlin.math.absoluteValue

class SoftBodyWheel(private val pointMass: PointMass, wp: WheelParams, var surface: DrivingSurface): SoftBodyForce(pointMass) {

    private val wheelCircumference = (wp.wheelDiameter * PI).toFloat()
    private val maxSpeed = ((wp.motorMaxRPM / 60) * wheelCircumference) * wp.motorToWheelRatio
    private val maxWheelTorque = wp.motorMaxTorque / wp.motorToWheelRatio
    val stallForce = (maxWheelTorque / wheelCircumference)
    var load = pointMass.mass
//    var maxStaticFrictionForce = surface.staticCoefficient * stallForce
//    var maxKineticFrictionForce = surface.kineticCoefficient * stallForce
//    var maxStaticFrictionForce = surface.staticCoefficient * g * load
//    var maxKineticFrictionForce = surface.kineticCoefficient * g * load
    private val maxStaticFrictionForce: Float
    get() = surface.staticCoefficient * GRAVITY * load
    private val maxKineticFrictionForce: Float
    get() = surface.kineticCoefficient * GRAVITY * load

    private val targetForce = Vector2()
    private val direction = Vector2(1f, 0f)

    init {
        lineMagnitudeScalar = .25f
    }

    fun updateDirection(direction: Vector2) {
        this.direction.set(direction).nor()
        val forceMag = targetForce.len()
        targetForce.set(this.direction).scl(forceMag)
    }

    fun updateForce(force: Float) {
        targetForce.set(this.direction).scl(force)
    }

    override fun render() {
        super.render()
        val scaler = ((load / pointMass.mass) * .5f).coerceIn(0f, 1f)
        RigidBodyApp.shapeDrawer.setColor(1f, 1f, 1f, scaler)
        RigidBodyApp.shapeDrawer.filledCircle(pointMass.position, scaler)
    }

    override fun update(sbCenter: Vector2, dt: Float) {
        // copy target force into force
        force.set(targetForce)

//        println("maxStaticFrictionForce $maxStaticFrictionForce")
        println("load $load")

        // transform everything into local space. force is already in local space. need to unrotate massPoint things
        val pointMassAngle = pointMass.getAngle(sbCenter)
        val velocityLocal = Vector2(pointMass.velocity).rotateRad(-pointMassAngle)
        val springForceLocal = Vector2(pointMass.force).rotateRad(-pointMassAngle)
        val lateralDirection = Vector2(direction).rotateRad(PI.toFloat()/2f)

        val velocityInWheelDirection = velocityLocal.dot(direction)
        val maxForceAtVelocity = calculateMaxForceAtVelocity(velocityInWheelDirection)

        // restrict force
        if (force.len2() > maxForceAtVelocity * maxForceAtVelocity) {
            if (maxForceAtVelocity < 0) {
                force.rotateRad(PI.toFloat())
            }
            force.setLength(maxForceAtVelocity.absoluteValue)
            println("restricting force")
        }

//        var requiredLateralFrictionForce = lateralDirection.dot(springForceLocal)
//        requiredLateralFrictionForce += lateralDirection.dot(velocityLocal) * pointMass.mass / dt
        val requiredLateralFrictionForce = ((lateralDirection.dot(velocityLocal) * pointMass.mass * .5f / dt) + lateralDirection.dot(springForceLocal) * .5f) * .4f

        val lateralForceVector = Vector2(lateralDirection).scl(requiredLateralFrictionForce)
        force.sub(lateralForceVector)

        // check friction
        if (force.len2() > maxStaticFrictionForce * maxStaticFrictionForce) {
            force.setLength(maxKineticFrictionForce)
            color.set(1f, 1f, 0f, 1f)
        } else {
            color.set(0f, 1f, 0f, 1f)
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