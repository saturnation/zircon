package org.hexworks.zircon.internal.component.impl

import org.assertj.core.api.Assertions.assertThat
import org.hexworks.zircon.api.builder.component.ComponentStyleSetBuilder
import org.hexworks.zircon.api.builder.graphics.StyleSetBuilder
import org.hexworks.zircon.api.builder.graphics.TileGraphicsBuilder
import org.hexworks.zircon.api.color.ANSITileColor
import org.hexworks.zircon.api.component.ColorTheme
import org.hexworks.zircon.api.component.ComponentStyleSet
import org.hexworks.zircon.api.component.renderer.impl.DefaultComponentRenderingStrategy
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.input.Input
import org.hexworks.zircon.api.input.MouseAction
import org.hexworks.zircon.api.input.MouseActionType
import org.hexworks.zircon.api.kotlin.onMousePressed
import org.hexworks.zircon.api.resource.BuiltInCP437TilesetResource
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.util.Maybe
import org.hexworks.zircon.internal.behavior.impl.DefaultBoundable
import org.hexworks.zircon.internal.component.renderer.DefaultRadioButtonGroupRenderer
import org.junit.Before
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("MemberVisibilityCanBePrivate")
class DefaultComponentTest {

    lateinit var target: DefaultComponent
    lateinit var tileset: TilesetResource

    @Before
    fun setUp() {
        tileset = FONT
        target = object : DefaultComponent(
                size = SIZE,
                position = POSITION,
                componentStyles = STYLES,
                tileset = tileset,
                renderer = DefaultComponentRenderingStrategy(
                        decorationRenderers = listOf(),
                        componentRenderer = DefaultRadioButtonGroupRenderer())) {
            override fun render() {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun applyColorTheme(colorTheme: ColorTheme): ComponentStyleSet {
                TODO("not implemented")
            }

            override fun acceptsFocus(): Boolean {
                TODO("not implemented")
            }

            override fun giveFocus(input: Maybe<Input>): Boolean {
                TODO("not implemented")
            }

            override fun takeFocus(input: Maybe<Input>) {
                TODO("not implemented")
            }
        }
    }

    @Test
    fun shouldUseFontFromComponentWhenTransformingToLayer() {
        val result = target.transformToLayers()
        result.forEach {
            assertThat(it.currentTileset().id).isEqualTo(tileset.id)
        }
    }

    @Test
    fun shouldProperlyApplyStylesOnInit() {
        assertThat(target.componentStyleSet().currentStyle())
                .isEqualTo(STYLES.currentStyle())
    }

    @Test
    fun shouldProperlySetNewPosition() {
        target.moveTo(NEW_POSITION)

        assertThat(target.position()).isEqualTo(NEW_POSITION)
    }

    @Test
    fun shouldContainBoundableWhichIsContained() {
        assertThat(target.containsBoundable(DefaultBoundable(SIZE - Size.one(), POSITION))).isTrue()
    }

    @Test
    fun shouldNotContainBoundableWhichIsContained() {
        assertThat(target.containsBoundable(DefaultBoundable(SIZE + Size.one(), POSITION))).isFalse()
    }

    @Test
    fun shouldContainPositionWhichIsContained() {
        assertThat(target.containsPosition(POSITION)).isTrue()
    }

    @Test
    fun shouldNotContainPositionWhichIsContained() {
        assertThat(target.containsPosition(POSITION - Position.offset1x1())).isFalse()
    }

    @Test
    fun shouldProperlyDrawOntoTileGraphic() {
        val image = TileGraphicsBuilder.newBuilder()
                .size(SIZE + Size.create(POSITION.x, POSITION.y))
                .build()
        target.drawOnto(image, POSITION)

        assertThat(image.getTileAt(POSITION - Position.offset1x1()).get())
                .isEqualTo(Tile.empty())

        target.size().fetchPositions().forEach {
            assertThat(image.getTileAt(it + POSITION).get())
                    .isEqualTo(target.tileGraphics().getTileAt(it).get())
        }
    }

    @Test
    fun shouldProperlyFetchByPositionWhenContainsPosition() {
        assertThat(target.fetchComponentByPosition(POSITION).get()).isEqualTo(target)
    }

    @Test
    fun shouldNotFetchByPositionWhenDoesNotContainPosition() {
        assertThat(target.fetchComponentByPosition(Position.create(100, 100)).isPresent).isFalse()
    }

    @Test
    fun shouldProperlyListenToMousePress() {
        val pressed = AtomicBoolean(false)
        target.onMousePressed {
            pressed.set(true)
        }

        target.inputEmitted(MouseAction(MouseActionType.MOUSE_PRESSED, 1, POSITION))

        assertThat(pressed.get()).isTrue()
    }

    @Test
    fun shouldNotListenToMousePressOnOtherComponents() {
        // TODO: move this test to component container!
    }

    @Test
    fun shouldProperlyListenToMouseRelease() {
        // TODO: move this test to component container!
    }

    @Test
    fun shouldNotListenToMouseReleaseOnOtherComponents() {
        // TODO: move this test to component container!
    }

    @Test
    fun shouldProperlyTransformToLayers() {
        val result = target.transformToLayers()
        assertThat(result).hasSize(1)
        assertThat(result.first().size()).isEqualTo(target.size())
        assertThat(result.first().position()).isEqualTo(target.position())
    }

    @Test
    fun shouldBeEqualToItself() {
        assertThat(target).isEqualTo(target)
    }

    companion object {
        val FONT = BuiltInCP437TilesetResource.ROGUE_YUN_16X16
        val SIZE = Size.create(4, 4)
        val POSITION = Position.create(2, 3)
        val NEW_POSITION = Position.create(6, 7)
        val DEFAULT_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITileColor.BLUE)
                .foregroundColor(ANSITileColor.RED)
                .build()
        val ACTIVE_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITileColor.GREEN)
                .foregroundColor(ANSITileColor.YELLOW)
                .build()
        val DISABLED_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITileColor.MAGENTA)
                .foregroundColor(ANSITileColor.BLUE)
                .build()
        val FOCUSED_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITileColor.YELLOW)
                .foregroundColor(ANSITileColor.CYAN)
                .build()
        val MOUSE_OVER_STYLE = StyleSetBuilder.newBuilder()
                .backgroundColor(ANSITileColor.RED)
                .foregroundColor(ANSITileColor.CYAN)
                .build()
        val STYLES = ComponentStyleSetBuilder.newBuilder()
                .defaultStyle(DEFAULT_STYLE)
                .activeStyle(ACTIVE_STYLE)
                .disabledStyle(DISABLED_STYLE)
                .focusedStyle(FOCUSED_STYLE)
                .mouseOverStyle(MOUSE_OVER_STYLE)
                .build()
    }
}
