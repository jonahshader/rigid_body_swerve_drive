package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.systems.physics.WheelParams
import jonahshader.systems.physics.iceSurface
import jonahshader.systems.physics.tarmacSurface
import kotlin.math.PI

class SoftBodyCar(private val size: Vector2, mass: Float): SoftBody() {
    private val topLeftPoint = PointMass(mass/4, Vector2(-size.x/2, size.y/2))
    private val topRightPoint = PointMass(mass/4, Vector2(size.x/2, size.y/2))
    private val bottomLeftPoint = PointMass(mass/4, Vector2(-size.x/2, -size.y/2))
    private val bottomRightPoint = PointMass(mass/4, Vector2(size.x/2, -size.y/2))
//    private val centerPoint = PointMass(mass/5, Vector2())

    private val sc = SpringConstants(100 * mass, mass * 10)
    private val surface = tarmacSurface
    private val wp = WheelParams(8f * 0.0254f, 80f, 8000f, 1/3f)

    private val topLeftWheel = SoftBodyWheel(this, topLeftPoint, wp, surface)
    private val topRightWheel = SoftBodyWheel(this, topRightPoint, wp, surface)
    private val bottomLeftWheel = SoftBodyWheel(this, bottomLeftPoint, wp, surface)
    private val bottomRightWheel = SoftBodyWheel(this, bottomRightPoint, wp, surface)

    init {
        addFullyConnectedPoint(topLeftPoint, sc)
        addFullyConnectedPoint(topRightPoint, sc)
        addFullyConnectedPoint(bottomLeftPoint, sc)
        addFullyConnectedPoint(bottomRightPoint, sc)
//        addFullyConnectedPoint(centerPoint, sc)

        addComponent(topLeftWheel)
        addComponent(topRightWheel)
        addComponent(bottomLeftWheel)
        addComponent(bottomRightWheel)
//        val tempForce = SoftBodyForce(topLeftPoint)
//        tempForce.force.y = 1f
//        addComponent(tempForce)
//
//        val tempForce2 = SoftBodyForce(bottomRightPoint)
//        tempForce2.force.y = -1f
//        addComponent(tempForce2)
    }

    fun setDrive(steer: Float, throttle: Float) {
        val throttleVector = Vector2(0f, throttle)
        val bottomMiddlePos = Vector2(0f, -size.y/2f)
        val topLeftForce = Vector2(topLeftPoint.originalPosition).sub(bottomMiddlePos)
        val longestLength = topLeftForce.len()

        topLeftForce.scl(1/longestLength).rotateRad(-PI.toFloat() / 2f).scl(steer)
        val topRightForce = Vector2(topRightPoint.originalPosition).sub(bottomMiddlePos).scl(1/longestLength).rotateRad(-PI.toFloat() / 2f).scl(steer)
        val bottomLeftForce = Vector2(bottomLeftPoint.originalPosition).sub(bottomMiddlePos).scl(1/longestLength).rotateRad(-PI.toFloat() / 2f).scl(steer)
        val bottomRightForce = Vector2(bottomRightPoint.originalPosition).sub(bottomMiddlePos).scl(1/longestLength).rotateRad(-PI.toFloat() / 2f).scl(steer)

        topLeftForce.add(throttleVector)
        topRightForce.add(throttleVector)
        bottomLeftForce.add(throttleVector)
        bottomRightForce.add(throttleVector)

        var maxMagnitude = topLeftForce.len()
        if (topRightForce.len2() > maxMagnitude * maxMagnitude) {
            maxMagnitude = topRightForce.len()
        }
        if (bottomLeftForce.len2() > maxMagnitude * maxMagnitude) {
            maxMagnitude = bottomLeftForce.len()
        }
        if (bottomRightForce.len2() > maxMagnitude * maxMagnitude) {
            maxMagnitude = bottomRightForce.len()
        }

        if (maxMagnitude > 1f) {
            val invMaxMag = 1/maxMagnitude
            topLeftForce.scl(invMaxMag)
            topRightForce.scl(invMaxMag)
            bottomLeftForce.scl(invMaxMag)
            bottomRightForce.scl(invMaxMag)
        }

        topLeftForce.scl(topLeftWheel.stallForce)
        topRightForce.scl(topLeftWheel.stallForce)
        bottomLeftForce.scl(topLeftWheel.stallForce)
        bottomRightForce.scl(topLeftWheel.stallForce)

        topLeftWheel.updateTargetForce(topLeftForce)
        topRightWheel.updateTargetForce(topRightForce)
        bottomLeftWheel.updateTargetForce(bottomLeftForce)
        bottomRightWheel.updateTargetForce(bottomRightForce)
    }
}