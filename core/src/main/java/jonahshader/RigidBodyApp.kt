package jonahshader

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import jonahshader.screens.CarSimScreen
import jonahshader.screens.OptimizerDisplayerScreen
import jonahshader.screens.SoftBodyCarScreen
import jonahshader.screens.SoftBodyScreen
import jonahshader.systems.assets.Assets
import jonahshader.systems.pathing.environment.Environment
import jonahshader.systems.pathing.environment.Wall
import jonahshader.systems.pathing.environment.Waypoint
import jonahshader.systems.pathing.sequenceoptimizers.GeneticAlgorithm
import jonahshader.systems.screen.ScreenManager
import space.earlygrey.shapedrawer.ShapeDrawer

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class RigidBodyApp : Game() {
    companion object {
        lateinit var batch: SpriteBatch
            private set
        lateinit var shapeDrawer: ShapeDrawer
            private set
    }
    override fun create() {
        Assets.startLoading()
        Assets.finishLoading()
        batch = SpriteBatch()
        shapeDrawer = ShapeDrawer(batch, Assets.getSprites().findRegion("white_pixel"))
        ScreenManager.game = this
//        ScreenManager.push(SimulationScreen())

        val track = Environment()
//        track.addWall(Wall(Vector2(2f, 0f), Vector2(2f, 10f)))
        track.addWall(Wall(Vector2(-2f, 0f), Vector2(-2f, 10f)))
        track.addWaypoint(Waypoint(Vector2(1f, 11f), 1f))
        track.addWaypoint(Waypoint(Vector2(10f, 12f), 1f))
//        track.addWaypoint(Waypoint(Vector2(5f, 6f), 1f))
        track.addWaypoint(Waypoint(Vector2(15f, 0f), 1f))

        ScreenManager.push(OptimizerDisplayerScreen(GeneticAlgorithm(track, 10 * 10, 1/10f, 50000, 1f)))
//        ScreenManager.push(SoftBodyCarScreen())
//        ScreenManager.push(SoftBodyScreen())
    }

    override fun dispose() {
        batch.dispose()
        super.dispose()
    }
}