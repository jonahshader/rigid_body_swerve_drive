package jonahshader.systems.pathing.environment

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import jonahshader.RigidBodyApp
import jonahshader.systems.dst2
import jonahshader.systems.pathing.SoftBodyCollider
import jonahshader.systems.softbody.SoftBody

class CircleWall(val position: Vector2, val radius: Float, val color: Color = Color.WHITE) : SoftBodyCollider {
    fun render() {
        RigidBodyApp.shapeDrawer.setColor(color)
        RigidBodyApp.shapeDrawer.circle(position.x, position.y, radius, 0.075f)
    }

    override fun isColliding(softBody: SoftBody): Boolean {
        for (pointMass in softBody.pointMasses) {
            if (dst2(pointMass.position, position) <= radius * radius)
                return true
        }
        return false
    }
}