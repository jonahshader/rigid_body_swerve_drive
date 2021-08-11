package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.systems.physics.DrivingSurface
import jonahshader.systems.physics.WheelParams
import jonahshader.systems.physics.iceSurface
import jonahshader.systems.physics.tarmacSurface

class SoftBodyCar(size: Vector2, mass: Float): SoftBody() {
    private val topLeftPoint = PointMass(mass/4, Vector2(-size.x/2, size.y/2))
    private val topRightPoint = PointMass(mass/4, Vector2(size.x/2, size.y/2))
    private val bottomLeftPoint = PointMass(mass/4, Vector2(-size.x/2, -size.y/2))
    private val bottomRightPoint = PointMass(mass/4, Vector2(size.x/2, -size.y/2))
//    private val centerPoint = PointMass(mass/5, Vector2())

    private val sc = SpringConstants(100 * mass, mass)
    private val surface = iceSurface
    private val wp = WheelParams(8f * 0.0254f, 32f, 8000f, 1/40f)

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
        val force = Vector2(0f, throttle * topLeftWheel.stallForce * 2)
        bottomLeftWheel.updateTargetForce(force)
        bottomRightWheel.updateTargetForce(force)
        force.rotateRad(-steer * Math.PI.toFloat() / 2)
        topLeftWheel.updateTargetForce(force)
        topRightWheel.updateTargetForce(force)
    }
}