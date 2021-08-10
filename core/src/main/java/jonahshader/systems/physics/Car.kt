package jonahshader.systems.physics

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import jonahshader.RigidBodyApp
import jonahshader.systems.assets.Assets
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max

class Car: RigidBody(Vector2(1f, 2f), 80f, Vector2(0f, 0f)) {
    private val maxWheelTorque = 100f
    private val maxWheelRPM = 8000f
    private val wheelDiameter = 0.1f
    private val maxSidewaysForce = 1000f
    private val dynamicFrictionLoss = 0.9f
    private val motorToWheelRatio = 0.5f
    private val frontLeftWheel = BLDCMotor(Vector2(-size.x/2f, size.y/2f), maxWheelRPM, maxWheelTorque, wheelDiameter, motorToWheelRatio)
    private val frontRightWheel = BLDCMotor(Vector2(size.x/2f, size.y/2f), maxWheelRPM, maxWheelTorque, wheelDiameter, motorToWheelRatio)
    private val backLeftWheel = BLDCMotor(Vector2(-size.x/2f, -size.y/2f), maxWheelRPM, maxWheelTorque, wheelDiameter, motorToWheelRatio)
    private val backRightWheel = BLDCMotor(Vector2(size.x/2f, -size.y/2f), maxWheelRPM, maxWheelTorque, wheelDiameter, motorToWheelRatio)

    private val sidewaysForce = SimpleForce(Vector2())

    private var drifting = false

    val maxForce: Float
    get() = frontLeftWheel.maxForce

    private val carFront = Sprite(Assets.getSprites().findRegion("white_pixel"))

    init {
        this += frontLeftWheel
        this += frontRightWheel
        this += backLeftWheel
        this += backRightWheel

        this += sidewaysForce
        carFront.setSize(size.x, size.y/2f)
        carFront.setOrigin(carFront.width/2f, 0f)
    }

    fun setDrive(steer: Float, throttle: Float) {
        val steerForce = Vector2(0f, throttle)

        backLeftWheel.setTargetForce(steerForce)
        backRightWheel.setTargetForce(steerForce)
        steerForce.rotateRad(- steer * Math.PI.toFloat())
        frontLeftWheel.setTargetForce(steerForce)
        frontRightWheel.setTargetForce(steerForce)
    }

    override fun update(dt: Float) {
        // get left right force vector

        // first unrotate
        val accelUnrotated = Vector2(vel).rotateRad(-rotation)
        // now just take the x component and multiply with mass
        val sidewaysForceNewtons = accelUnrotated.x * mass / max(dt, 1/300f)
        var counterForce = (-sidewaysForceNewtons).coerceIn(-maxSidewaysForce, maxSidewaysForce)
        drifting = (abs(counterForce) == maxSidewaysForce)
        if (drifting) {
            counterForce *= dynamicFrictionLoss
            frontLeftWheel.force.scl(dynamicFrictionLoss)
            frontRightWheel.force.scl(dynamicFrictionLoss)
            backLeftWheel.force.scl(dynamicFrictionLoss)
            backRightWheel.force.scl(dynamicFrictionLoss)
        }
        println(counterForce)
        sidewaysForce.force.set(counterForce, 0f)
        super.update(dt)
    }

    override fun draw() {
        super.draw()
        carFront.setColor(if (drifting) 1f else 0f, 1f, 0f, 1f)
        carFront.rotation = rotation * (180 / PI.toFloat())
        carFront.setOriginBasedPosition(pos.x, pos.y)
        carFront.draw(RigidBodyApp.batch)
    }

}