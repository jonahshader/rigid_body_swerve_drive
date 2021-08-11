package jonahshader.systems.softbody

import com.badlogic.gdx.math.Vector2

interface Component {
    val force: Vector2
    fun update(sbCenter: Vector2, dt: Float)
    fun render()
    fun applyForce()
}