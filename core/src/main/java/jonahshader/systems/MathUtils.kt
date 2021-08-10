package jonahshader.systems

import com.badlogic.gdx.math.Vector2

fun dst2(v1: Vector2, v2: Vector2): Float = Vector2.len2(v1.x - v2.x, v1.y - v2.y)
fun dst(v1: Vector2, v2: Vector2): Float = Vector2.len(v1.x - v2.x, v1.y - v2.y)