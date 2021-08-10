package jonahshader.systems.ui.menu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ScalingViewport
import jonahshader.systems.sound.SoundSystem
import jonahshader.systems.ui.CustomShapes
import jonahshader.systems.assets.Assets
import jonahshader.systems.ui.TextRenderer
import space.earlygrey.shapedrawer.ShapeDrawer
import kotlin.math.max
import kotlin.math.min

class Slider : MenuItem {

    var sliderAction : (i: Float) -> Unit
    var position = 1F
    var sliderBodyRadius = 10f
    var sliderRadius = sliderBodyRadius * 1.5f

    constructor(action: (i: Float) -> Unit, x: Float, y: Float, width: Float, height: Float, label: String, font: TextRenderer.Font, camera: Camera, initialPos: Float)
            : super({}, x, y, width, height, label, font, camera){
        sliderAction = action
        position = initialPos
    }

    constructor(action: (i: Float) -> Unit, label: String, previousMenuItem: MenuItem, font: TextRenderer.Font, camera: Camera, initialPos: Float)
            : super({}, label, previousMenuItem, font, camera) {
        sliderAction = action
        position = initialPos
    }

    override fun run(offset: Vector2, dt: Float, viewport: ScalingViewport) {
        val xo = offset.x + x
        val yo = offset.y + y
        val m = mouseWorld(viewport)
        if (m.x >= xo && m.y >= yo && m.x <= xo + width && m.y <= yo + height) {
            if (Gdx.input.justTouched()) {
                SoundSystem.playSoundStandalone(Assets.manager.get(Assets.MENU_OPEN_SOUND, Sound::class.java), .8f, 0f)
                position = (m.x-xo)/width
                position = min(position, 1f)
                position = max(position, 0f)
                sliderAction(position)
            }
            if (!mouseOver) {
                mouseOver = true
                // play mouseOver sound
                SoundSystem.playSoundStandalone(Assets.manager.get(Assets.MENU_MOUSE_OVER_SOUND, Sound::class.java), .8f, 0f)
            }

            progress += dt * progressPerSecond
        } else {
            mouseOver = false
            progress -= dt * progressPerSecond
        }
        progress = progress.coerceIn(0f, 1f)
    }

    override fun draw(batch: SpriteBatch, shapeDrawer: ShapeDrawer, viewport: ScalingViewport, offset: Vector2) {
        val xo = offset.x + x
        val yo = offset.y + y

        // slider body shadow
        shapeDrawer.setColor(0.0f, 0.0f, 0.0f, .5f)
        CustomShapes.filledRoundedRect(shapeDrawer, xo, yo + height/2, width, sliderBodyRadius * 2, sliderBodyRadius)

        var progressMapped = if (mouseOver) {
            Interpolation.circleOut.apply(0f, 1f, progress)
        } else {
            Interpolation.circleOut.apply(1f, 0f, 1-progress)
        }
        progressMapped *= .5f

        shapeDrawer.setColor(0.8f, 0.8f, 0.8f, 1f)
        CustomShapes.filledRoundedRect(shapeDrawer, xo + MOUSE_OVER_INDENT * progressMapped, yo + MOUSE_OVER_INDENT * progressMapped + height/2, width, sliderBodyRadius * 2, sliderBodyRadius)
        shapeDrawer.setColor(0.4f, 0.4f, 0.4f, 1f)
        CustomShapes.filledRoundedRect(shapeDrawer, xo + MOUSE_OVER_INDENT * progressMapped+position*width-sliderRadius, (yo + MOUSE_OVER_INDENT * progressMapped) + (height/2f) - sliderRadius*.25f, sliderRadius * 2, sliderRadius * 2, sliderRadius)
        TextRenderer.begin(batch, viewport, font, height * .25f, 0.05f)
        TextRenderer.color = Color.WHITE
        TextRenderer.drawTextCentered(xo + (width/2f) + MOUSE_OVER_INDENT * progressMapped, yo + MOUSE_OVER_INDENT * progressMapped + height/2 - 25, label, height * (0.02f + progressMapped/32f), .66f)
        TextRenderer.end()


    }
}