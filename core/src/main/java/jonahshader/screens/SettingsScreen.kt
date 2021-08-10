package jonahshader.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FillViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import jonahshader.RigidBodyApp
import jonahshader.systems.screen.ScreenManager
import jonahshader.systems.settings.Settings
import jonahshader.systems.sound.SoundSystem
import jonahshader.systems.ui.TextRenderer
import jonahshader.systems.ui.menu.Menu
import ktx.app.KtxScreen
import ktx.graphics.begin

class SettingsScreen : KtxScreen {

    private val camera = OrthographicCamera()
    private val viewport = FitViewport(640f, 900f, camera)
    private val menu = Menu(TextRenderer.Font.HEAVY, camera, Vector2(0f, 900f/6f), Vector2(500f, 90f))

    init {
        with (menu) {
            addSliderItem("Overall Volume", SoundSystem.overallVolume) {
                    t->SoundSystem.overallVolume = t
                Settings.settings["overall-volume"] = t.toString()
                Settings.save()
            }
            addSliderItem("Music Volume", SoundSystem.musicVolume) {
                    t->SoundSystem.musicVolume = t
                Settings.settings["music-volume"] = t.toString()
                Settings.save()
            }
            addSliderItem("Sound Effect Volume", SoundSystem.soundVolume) {
                    t->SoundSystem.soundVolume = t
                Settings.settings["sound-volume"] = t.toString()
                Settings.save()
            }
            addMenuItem("Fullscreen") {
                if(!Gdx.graphics.isFullscreen) Gdx.graphics.setFullscreenMode(Gdx.graphics.displayMode)
                else Gdx.graphics.setWindowedMode(Gdx.graphics.width - 40, Gdx.graphics.height - 40)

                Settings.settings["fullscreen"] = Gdx.graphics.isFullscreen.toString()
                Settings.save()

            }
            addMenuItem("Back") { ScreenManager.pop() }
        }

    }

    override fun show() {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(.2f, .5f, 1f, 1f)
        menu.run(delta, viewport)

        viewport.apply()

        RigidBodyApp.batch.begin(camera)

        menu.draw(RigidBodyApp.batch, RigidBodyApp.shapeDrawer, viewport)

        RigidBodyApp.batch.end()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

}