package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import jonahshader.systems.physics.WheelParams
import jonahshader.systems.physics.iceSurface
import jonahshader.systems.physics.tarmacSurface
import kotlin.math.PI
import kotlin.math.max

// z is height
class SoftBodyCar(private val size: Vector3, private val mass: Float): SoftBody() {
    companion object {
        const val GRAVITY = 9.80665f
    }
    private val topLeftPoint = PointMass(mass/4, Vector2(-size.x/2, size.y/2))
    private val topRightPoint = PointMass(mass/4, Vector2(size.x/2, size.y/2))
    private val bottomLeftPoint = PointMass(mass/4, Vector2(-size.x/2, -size.y/2))
    private val bottomRightPoint = PointMass(mass/4, Vector2(size.x/2, -size.y/2))
//    private val centerPoint = PointMass(mass/5, Vector2())

//    private val sc = SpringConstants(150 * mass, mass * 15)
private val sc = SpringConstants(20 * mass, mass * 1)
    private val surface = tarmacSurface
    private val wp = WheelParams(8f * 0.0254f, 500f, 8000f, 1/3f)

    private val topLeftWheel = SoftBodyWheel(topLeftPoint, wp, surface)
    private val topRightWheel = SoftBodyWheel(topRightPoint, wp, surface)
    private val bottomLeftWheel = SoftBodyWheel(bottomLeftPoint, wp, surface)
    private val bottomRightWheel = SoftBodyWheel(bottomRightPoint, wp, surface)

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

    override fun update(dt: Float) {
        val accelAlignedScaled = Vector2(getAverageAcceleration()).rotateRad(-getAngleRad()).scl(1/GRAVITY)
        var xLoadChangePerAccelRatio = size.z / size.x
        var yLoadChangePerAccelRatio = size.z / size.y

        var baseLoad = mass / 4f // four wheel design, assuming center of gravity is in the center of the car

        var xLoadDelta = (xLoadChangePerAccelRatio * baseLoad) * accelAlignedScaled.x
        val yLoadDelta = (yLoadChangePerAccelRatio * baseLoad) * accelAlignedScaled.y

        topLeftWheel.load = ((baseLoad + xLoadDelta) - yLoadDelta).coerceAtLeast(0f)
        topRightWheel.load = ((baseLoad - xLoadDelta) - yLoadDelta).coerceAtLeast(0f)
        bottomLeftWheel.load = ((baseLoad + xLoadDelta) + yLoadDelta).coerceAtLeast(0f)
        bottomRightWheel.load = ((baseLoad - xLoadDelta) + yLoadDelta).coerceAtLeast(0f)


        super.update(dt)
    }

    // steer, throttle, balance all in [-1, 1]
    fun setDrive(steer: Float, throttle: Float, balance: Float) {
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

        val frontScale: Float
        val backScale: Float
        if (balance < 0) {
            frontScale = max(1f + balance, 0.01f)
            backScale = 1f
        } else {
            frontScale = 1f
            backScale = max(1f - balance, 0.01f)
        }

        topLeftForce.scl(topLeftWheel.stallForce * frontScale)
        topRightForce.scl(topLeftWheel.stallForce * frontScale)
        bottomLeftForce.scl(topLeftWheel.stallForce * backScale)
        bottomRightForce.scl(topLeftWheel.stallForce * backScale)

        if (throttle != 0f) {
            topLeftWheel.updateDirection(topLeftForce)
            topRightWheel.updateDirection(topRightForce)
            bottomLeftWheel.updateDirection(bottomLeftForce)
            bottomRightWheel.updateDirection(bottomRightForce)
        }
        topLeftWheel.updateForce(topLeftForce.len())
        topRightWheel.updateForce(topRightForce.len())
        bottomLeftWheel.updateForce(bottomLeftForce.len())
        bottomRightWheel.updateForce(bottomRightForce.len())
    }
}