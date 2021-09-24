package external

import com.badlogic.gdx.math.Vector2

// modified from https://gamedev.stackexchange.com/questions/26004/how-to-detect-2d-line-on-line-collision
fun isIntersecting(a: Vector2, b: Vector2, c: Vector2, d: Vector2) : Boolean {
    val denominator = ((b.x - a.x) * (d.y - c.y)) - ((b.y - a.y) * (d.x - c.x))
    val numerator1 = ((a.y - c.y) * (d.x - c.x)) - ((a.x - c.x) * (d.y - c.y))
    val numerator2 = ((a.y - c.y) * (b.x - a.x)) - ((a.x - c.x) * (b.y - a.y))

    if (denominator == 0f) return numerator1 == 0f && numerator2 == 0f

    val r = numerator1 / denominator
    val s = numerator2 / denominator

    return (r in 0.0..1.0) && (s in 0.0..1.0)
}