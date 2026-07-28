package com.github.mich8bsp.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.github.mich8bsp.logic.*
import ktx.graphics.copy


class TextureManager{
    val cellSize: Int = 85
    private val emptyCellTexture: Texture = Texture(Gdx.files.internal("images/empty.png"))
    val laserTexture: Texture = createSingleColorTexture(cellSize, cellSize, Color.YELLOW)
    val selectedTexture: Texture = createSingleColorTexture(cellSize, cellSize, Color.BROWN.copy(alpha = 0.5f))
    val deathTexture: Texture = createSingleColorTexture(cellSize, cellSize, Color.ORANGE)
    private val texturesRepo: Map<TextureKey, Texture> = mapOf(
            TextureKey("anubis", EPlayerColor.RED) to Texture(Gdx.files.internal("images/anubis_red.png")),
            TextureKey("anubis", EPlayerColor.GREY) to Texture(Gdx.files.internal("images/anubis_grey.png")),
            TextureKey("pharaoh", EPlayerColor.RED) to Texture(Gdx.files.internal("images/pharaoh_red.png")),
            TextureKey("pharaoh", EPlayerColor.GREY) to Texture(Gdx.files.internal("images/pharaoh_grey.png")),
            TextureKey("pyramid", EPlayerColor.RED) to Texture(Gdx.files.internal("images/pyramid_red.png")),
            TextureKey("pyramid", EPlayerColor.GREY) to Texture(Gdx.files.internal("images/pyramid_grey.png")),
            TextureKey("scarab", EPlayerColor.RED) to Texture(Gdx.files.internal("images/scarab_red.png")),
            TextureKey("scarab", EPlayerColor.GREY) to Texture(Gdx.files.internal("images/scarab_grey.png")),
            TextureKey("sphinx", EPlayerColor.RED) to Texture(Gdx.files.internal("images/sphinx_red.png")),
            TextureKey("sphinx", EPlayerColor.GREY) to Texture(Gdx.files.internal("images/sphinx_grey.png")),
            TextureKey("empty", EPlayerColor.RED) to Texture(Gdx.files.internal("images/empty_red.png")),
            TextureKey("empty", EPlayerColor.GREY) to Texture(Gdx.files.internal("images/empty_grey.png"))
    )

    fun getTexture(color: EPlayerColor?, piece: Piece?): Texture? {
        return when {
            piece!=null && !piece.isDead() -> {
                when(piece){
                    is AnubisPiece -> texturesRepo[TextureKey("anubis", piece.color)]
                    is PharaohPiece -> texturesRepo[TextureKey("pharaoh", piece.color)]
                    is PyramidPiece -> texturesRepo[TextureKey("pyramid", piece.color)]
                    is ScarabPiece -> texturesRepo[TextureKey("scarab", piece.color)]
                    is SphinxPiece -> texturesRepo[TextureKey("sphinx", piece.color)]
                    else -> emptyCellTexture
                }
            }
            color!=null -> {
                texturesRepo[TextureKey("empty", color)]
            }
            else -> {
                emptyCellTexture
            }
        }
    }

    private fun createSingleColorTexture(width: Int, height: Int, color: Color): Texture {
        val pixmap = Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(color)
        pixmap.fillRectangle(0, 0, width, height)
        val texture = Texture(pixmap)
        pixmap.dispose()
        return texture
    }

    fun dispose() {
        texturesRepo.values.forEach { texture ->
            texture.dispose()
        }
    }
}
data class TextureKey(val pieceType: String, val playerColor: EPlayerColor)