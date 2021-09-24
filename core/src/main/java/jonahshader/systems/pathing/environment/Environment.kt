package jonahshader.systems.pathing.environment

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import jonahshader.systems.dst
import jonahshader.systems.pathing.CarControlStep
import jonahshader.systems.pathing.SoftBodyCollider
import jonahshader.systems.softbody.SoftBodyCar

// assuming car starts at origin
class Environment(val carBuilder: () -> SoftBodyCar = {SoftBodyCar(Vector3(1f, 2f, .8f), 50f)}) {
    // partial copy constructor. waypoints, walls, circleWals, collideables are all shallow copied since they don't change
    constructor(toCopy: Environment) : this(toCopy.carBuilder) {
        waypoints += toCopy.waypoints
        walls += toCopy.walls
        circleWalls += toCopy.circleWalls
        collidables += toCopy.collidables
    }

    private val waypoints = mutableListOf<Waypoint>()
    private val walls = mutableListOf<Wall>()
    private val circleWalls = mutableListOf<CircleWall>()
    private val collidables = mutableListOf<SoftBodyCollider>()

    private var targetWaypointIndex: Int = 0
    private var partialWaypointAccumScore = 0.0f
    private var car = carBuilder()
    var time = 0
    private var collided = false

    fun reset() {
        targetWaypointIndex = 0
        partialWaypointAccumScore = 0.0f
        car = carBuilder()
        time = 0
        collided = false
    }

    fun addWall(wall: Wall) {
        walls += wall
        collidables += wall
    }

    fun addCircleWall(circleWall: CircleWall) {
        circleWalls += circleWall
        collidables += circleWall
    }

    fun addWaypoint(waypoint: Waypoint) {
        waypoints += waypoint
    }

    fun draw() {
        waypoints.forEach { it.render() }
        walls.forEach { it.render() }
        circleWalls.forEach { it.render() }
        car.render()
    }

    fun update(step: CarControlStep, dt: Float) {
        update(step.steer, step.throttle, step.balance, dt)
    }

    fun update(steer: Float, throttle: Float, balance: Float, dt: Float) {
        if (targetWaypointIndex < waypoints.size) {
            val targetWaypoint = waypoints[targetWaypointIndex]
            if (targetWaypoint.isColliding(car)) {
                val pWaypointPos = Vector2()
                if (targetWaypointIndex > 0) {
                    pWaypointPos.set(waypoints[targetWaypointIndex-1].position)
                }
                partialWaypointAccumScore += dst(targetWaypoint.position, pWaypointPos)
                targetWaypointIndex++
            }
        }

        car.setDrive(steer, throttle, balance)
        car.update(dt)

        for (sbc in collidables) {
            if (sbc.isColliding(car)) {
                collided = true
                break
            }
        }

        if (!isDone())
            time++
    }

    fun isDone() = targetWaypointIndex == waypoints.size || collided

    fun getCurrentFitness() : Float {
        if (collided) return 0f
        return if (targetWaypointIndex < waypoints.size) {
            if (targetWaypointIndex == 0) {
                partialWaypointAccumScore + dst(waypoints[targetWaypointIndex].position, Vector2.Zero) - dst(car.getCenter(), waypoints[targetWaypointIndex].position)
            } else {
                partialWaypointAccumScore + dst(waypoints[targetWaypointIndex].position, waypoints[targetWaypointIndex - 1].position) - dst(car.getCenter(), waypoints[targetWaypointIndex].position)
            }
        } else {
            partialWaypointAccumScore + 100f / time
        }
    }

    fun getBoundingBox(): Rectangle {
        var xMin = 9999f
        var yMin = 9999f
        var xMax = -9999f
        var yMax = -9999f

        for (w in walls) {
            if (w.p1.x < xMin) xMin = w.p1.x
            if (w.p2.x < xMin) xMin = w.p2.x
            if (w.p1.x > xMax) xMax = w.p1.x
            if (w.p2.x > xMax) xMax = w.p2.x
            if (w.p1.y < yMin) yMin = w.p1.y
            if (w.p2.y < yMin) yMin = w.p2.y
            if (w.p1.y > yMax) yMax = w.p1.y
            if (w.p2.y > yMax) yMax = w.p2.y
        }

        for (c in circleWalls) {
            if (c.position.x - c.radius < xMin) xMin = c.position.x - c.radius
            if (c.position.x + c.radius > xMax) xMax = c.position.x + c.radius
            if (c.position.y - c.radius < yMin) yMin = c.position.y - c.radius
            if (c.position.y + c.radius > yMax) yMax = c.position.y + c.radius
        }

        for (c in waypoints) {
            if (c.position.x - c.radius < xMin) xMin = c.position.x - c.radius
            if (c.position.x + c.radius > xMax) xMax = c.position.x + c.radius
            if (c.position.y - c.radius < yMin) yMin = c.position.y - c.radius
            if (c.position.y + c.radius > yMax) yMax = c.position.y + c.radius
        }

        return Rectangle(xMin, yMin, xMax - xMin, yMax - yMin)
    }
}