package jonahshader

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import jonahshader.screens.CarSimScreen
import jonahshader.screens.SoftBodyCarScreen
import jonahshader.screens.SoftBodyScreen
import jonahshader.systems.assets.Assets
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
        ScreenManager.push(SoftBodyCarScreen())
//        ScreenManager.push(SoftBodyScreen())
    }

    override fun dispose() {
        batch.dispose()
        super.dispose()
    }
}