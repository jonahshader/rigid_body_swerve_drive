package jonahshader.systems.pathing

import jonahshader.systems.softbody.SoftBody

interface SoftBodyCollider {
    fun isColliding(softBody: SoftBody): Boolean
}