package jonahshader.systems.physics

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import jonahshader.RigidBodyApp
import jonahshader.systems.assets.Assets
import ktx.assets.dispose
import ktx.math.divAssign
import ktx.math.plusAssign
import ktx.math.times
import ktx.math.timesAssign
import space.earlygrey.shapedrawer.JoinType
import kotlin.math.PI

//TODO: units??? mass probably kg, idk about size and pos though
open class RigidBody(val size: Vector2, val mass: Float, val pos: Vector2) {
    private val rotationalInertia = (1/12f) * mass * size.len2()
    val vel = Vector2()
    val accel = Vector2()
    var rotation = 0f
    private var rotationalVelocity = 0f // rads per second
    val forces = mutableListOf<Force>()

    private val sprite = Sprite(Assets.getSprites().findRegion("white_pixel"))

    init {
        sprite.setSize(size.x, size.y)
        sprite.setOriginCenter()
        sprite.setColor(0.8f, 0.8f, 0.8f, 1.0f)
    }

    operator fun plusAssign(force: Force) {
        if (force !in forces) forces += force
    }

    operator fun minusAssign(force: Force) {
        forces -= force
    }

    open fun update(dt: Float) {
        forces.forEach {
            // calculate velocity at force
            val velAtPos = getLocalVelocityAtLocalPos(it.relativePos)
            it.update(dt, velAtPos)
        }
        accel.set(0f, 0f)
        forces.forEach{accel += it.force}
        accel.rotateRad(rotation)
        accel /= mass
//        accel *= dt
        vel += accel * dt
        pos += vel * dt

        val torqueAroundCenter = forces.fold(0f) { acc, force -> acc + force.calculateTorqueAroundRigidBodyCenter() }
        val rotationalAcceleration = torqueAroundCenter / rotationalInertia
        rotationalVelocity += rotationalAcceleration * dt
        rotation += rotationalVelocity * dt
    }

    fun getLocalVelocityAtLocalPos(localPos: Vector2): Vector2 {
        val velAtPos = Vector2(vel)
        val momentArmRotated = Vector2(localPos).rotateRad(rotation + PI.toFloat()/2f).scl(rotationalVelocity)
        velAtPos += momentArmRotated
        velAtPos.rotateRad(-rotation)
        return velAtPos
    }

    open fun draw() {
        sprite.setOriginBasedPosition(pos.x, pos.y)
        sprite.rotation = rotation * (180 / PI.toFloat())
        sprite.draw(RigidBodyApp.batch)
//        RigidBodyApp.shapeDrawer.setColor(1f, 1f, 1f, 1f)
//        RigidBodyApp.shapeDrawer.filledRectangle(pos.x, pos.y, size.x, size.y, rotation)
//        RigidBodyApp.shapeDrawer.setColor(1f, 0f, 0f, 1f)
//        RigidBodyApp.shapeDrawer.ellipse(pos.x, pos.y, .25f, .25f)
    }
}