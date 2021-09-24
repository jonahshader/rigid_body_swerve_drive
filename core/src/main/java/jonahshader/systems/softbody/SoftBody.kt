package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2
import jonahshader.systems.dst2
import ktx.math.component1
import ktx.math.component2
import ktx.math.times
import kotlin.math.pow

open class SoftBody {
    val pointMasses = mutableListOf<PointMass>()
    val springs = mutableListOf<Spring>()
    private val components = mutableListOf<Component>()
    private var totalMass = 0f
    private var returnVector = Vector2()
    private var tempVector = Vector2()

    private var structureModified = false

    companion object {
        fun makeRectangle(width: Int, height: Int, mass: Float, pointMassesPerMeter: Int, fullyConnected: Boolean, sc: SpringConstants): SoftBody {
            val sb = SoftBody()
            val pointCount = width * height * pointMassesPerMeter * pointMassesPerMeter
            val massPerPointMass = mass / pointCount
            val xOffset = -(width * pointMassesPerMeter) * .5f
            val yOffset = -(height * pointMassesPerMeter) * .5f

            for (y in 0 until (width * pointMassesPerMeter)) for (x in 0 until (height * pointMassesPerMeter)) {
                if (fullyConnected)
                    sb.addFullyConnectedPoint(PointMass(massPerPointMass, Vector2((x + xOffset) / pointMassesPerMeter, (y + yOffset) / pointMassesPerMeter)), sc)
                else
                    sb.addPointConnectedRadius(PointMass(massPerPointMass, Vector2((x + xOffset) / pointMassesPerMeter, (y + yOffset) / pointMassesPerMeter)), 1.5f / pointMassesPerMeter, sc)
            }
            return sb
        }
    }

    fun addFullyConnectedPoint(pointMass: PointMass, sc: SpringConstants) {
        pointMasses.forEach {
            springs += Spring(it, pointMass, sc = sc)
        }
        pointMasses += pointMass
        totalMass += pointMass.mass
        structureModified = true
    }

    fun addPointConnectedRadius(pointMass: PointMass, radius: Float, sc: SpringConstants) {
        pointMasses.forEach {
            if (Vector2.len(pointMass.position.x - it.position.x, pointMass.position.y - it.position.y) <= radius) {
                springs += Spring(it, pointMass, sc = sc)
            }
        }
        pointMasses += pointMass
        totalMass += pointMass.mass
        structureModified = true
    }

    fun addComponent(component: Component) {
        components += component
    }

    open fun update(dt: Float) {
        if (structureModified) {
            updatePointMassAngles()
            structureModified = false
        }
        val center = getCenter()
        springs.forEach{ it.update(dt) }
        components.forEach { it.update(center, dt) }
        components.forEach { it.applyForce() }
        pointMasses.forEach{ it.update(dt) }
    }

    fun render() {
        springs.forEach{ it.render() }
        pointMasses.forEach{ it.render() }
        components.forEach{ it.render() }
    }

    fun getAverageVelocity(): Vector2 {
        val avgVel = returnVector.setZero()
        pointMasses.forEach {
            avgVel.add(it.velocity.x * it.mass / totalMass, it.velocity.y * it.mass / totalMass)
        }
        return avgVel
    }

    fun getAverageAcceleration(): Vector2 {
        val avgAccel = returnVector.setZero()
        pointMasses.forEach {
            avgAccel.add(it.acceleration.x * it.mass / totalMass, it.acceleration.y * it.mass / totalMass)
        }
        return avgAccel
    }

    fun getAngleVec(): Vector2 {
        val center = Vector2(getCenter())
        val angleVec = returnVector.setZero()
        val radToAngleVec = tempVector.setZero()
        pointMasses.forEach {
            radToAngleVec.set(1f, 0f).rotateRad(it.getAngle(center))
            angleVec.add(radToAngleVec)
        }
        angleVec.nor()
        return angleVec
    }

    fun getAngleRad(): Float {
        return getAngleVec().angleRad()
    }

    fun getTotalKineticEnergyPointWise() = pointMasses.fold(0f) { acc, point ->  acc + .5f * point.mass * point.velocity.len2() }
    fun getTranslationalKineticEnergyBodyWise() = .5f * totalMass * getAverageVelocity().len2()
    fun getAngularKineticEnergyBodyWise() = .5f * getInertiaAroundCenter() * getRotationalVelocityAroundCenter().pow(2)
    fun getTotalKineticEnergyBodyWise() = getTranslationalKineticEnergyBodyWise() + getAngularKineticEnergyBodyWise()

    fun getCentralAngularKineticEnergy(): Float {
        val center = getCenter()
        return pointMasses.fold(0f) { acc, point -> acc + point.mass * dst2(center, point.position) }
    }

    fun getInertiaAroundCenter() = getInertiaAroundPosition(getCenter())

    fun getInertiaAroundPosition(position: Vector2): Float {
        return pointMasses.fold(0f) { acc, point -> acc + point.mass * dst2(position, point.position) }
    }

    fun getCenter(): Vector2 {
        val center = returnVector.setZero()
        pointMasses.forEach {
            center.add(it.position.x * it.mass / totalMass, it.position.y * it.mass / totalMass)
        }
        return center
    }

    fun getRotationalVelocityAroundCenter(): Float {
        val (xCenter, yCenter) = getCenter()
        var rotationalVel = 0f
        pointMasses.forEach {
            tempVector.set(it.position).sub(xCenter, yCenter)

            rotationalVel += (tempVector.crs(it.velocity) / tempVector.len2()) * (it.mass/ totalMass)
        }
        return rotationalVel
    }

    fun setAverageVelocity(averageVel: Vector2) {
        val currAvgVel = getAverageVelocity()
        pointMasses.forEach {
            it.velocity.sub(currAvgVel).add(averageVel)
        }
    }

    private fun updatePointMassAngles() {
        val center = getCenter()
        pointMasses.forEach {
            it.angleFromCenterOfSoftBody = tempVector.set(it.position).sub(center).angleRad()
        }
    }
}