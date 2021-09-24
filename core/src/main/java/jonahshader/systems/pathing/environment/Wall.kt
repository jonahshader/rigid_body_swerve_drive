package jonahshader.systems.pathing.environment

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import external.isIntersecting
import jonahshader.RigidBodyApp
import jonahshader.systems.pathing.SoftBodyCollider
import jonahshader.systems.softbody.SoftBody

class Wall(val p1: Vector2, val p2: Vector2, val color: Color = Color.WHITE) : SoftBodyCollider {
    fun render() {
        RigidBodyApp.shapeDrawer.setColor(color)
        RigidBodyApp.shapeDrawer.line(p1, p2, .075f)
    }

    override fun isColliding(softBody: SoftBody): Boolean {
        for (edge in softBody.springs) {
            if (isIntersecting(p1, p2, edge.startPointMass.position, edge.endPointMass.position))
                return true
        }
        return false
    }
}