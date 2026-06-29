package lgbt.faith

import lgbt.faith.biome.BiomeSource
import lgbt.faith.terrain.TerrainGenerator
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.File
import javax.imageio.ImageIO

fun renderEndMap(
    width: Int, height: Int,
    centreX: Int, centreZ: Int,
    terrainGenerator: TerrainGenerator
): BufferedImage {
    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val data = (img.raster.dataBuffer as DataBufferInt).data

    val startBlockX = centreX - width  / 2
    val startBlockZ = centreZ - height / 2

    for (z in 0 until width) {
        val wz = startBlockZ + z
        val row = z * width

        for (x in 0 until height) {

            val wx = startBlockX + x

            val h = terrainGenerator.getHeight(wx, wz)

            val t = (h.toFloat() / 128f).coerceIn(0f, 1f)
            val brightness = (t * 255).toInt()

            val r = (brightness * 0.968f).toInt().coerceIn(0, 255)
            val g = (brightness * 0.913f).toInt().coerceIn(0, 255)
            val b = (brightness * 0.639f).toInt().coerceIn(0, 255)

            data[row + x] = (r shl 16) or (g shl 8) or (b)
        }
    }

    return img
}

fun main() {

    val source = BiomeSource(1)
    val terrain = TerrainGenerator(source)

    val img = renderEndMap(512, 512, 0, 0, terrain)

    ImageIO.write(img, "png", File("end_map.png"))
    println("complete!")

}