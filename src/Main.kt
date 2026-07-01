package lgbt.faith

import lgbt.faith.biome.BiomeSource
import lgbt.faith.block.BPos
import lgbt.faith.block.CPos
import lgbt.faith.block.RPos
import lgbt.faith.structures.EndCity
import lgbt.faith.structures.EndCityGenerator
import lgbt.faith.structures.EndGateway
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

    val endCity = EndCity()
    val endCityGenerator = EndCityGenerator()
    val gateway = EndGateway()
    val seed = terrainGenerator.worldSeed

    val regionCache = mutableMapOf<RPos, CPos>()
    for (z in 0 until width) {
        val wz = startBlockZ + z
        val row = z * width

        for (x in 0 until height) {

            val wx = startBlockX + x

            val pixel = BPos(wx, 0, wz)
            val pixelChunk = pixel.toChunkPos()

            val region = pixelChunk.toRegionPos(endCity.spacing)
            val chunk = regionCache.getOrPut(region) {
                endCity.getInRegion(region.x, region.z, seed)
            }

            var endCityCanSpawn = false
            var endCityCanGen = false

            if (chunk == pixelChunk) {
                endCityCanSpawn = endCity.canSpawn(chunk.x, chunk.z, terrainGenerator.biomeSource)
                endCityCanGen = endCityGenerator.generate(terrainGenerator, chunk)
            }

            val endGatewayCanStart = gateway.canStart(pixelChunk.x, pixelChunk.z, terrainGenerator.worldSeed)

            if (endCityCanSpawn && endCityCanGen) {
                data[row + x] = (178 shl 16) or (76 shl 8) or (216)
            } else if (endGatewayCanStart) {
                data[row + x] = (58 shl 16) or (142 shl 8) or (140)

            } else {
                val h = terrainGenerator.getHeight(wx, wz)

                val t = (h.toFloat() / 128f).coerceIn(0f, 1f)
                val brightness = (t * 255).toInt()

                val r = (brightness * 0.968f).toInt().coerceIn(0, 255)
                val g = (brightness * 0.913f).toInt().coerceIn(0, 255)
                val b = (brightness * 0.639f).toInt().coerceIn(0, 255)

                data[row + x] = (r shl 16) or (g shl 8) or (b)

            }
        }
    }

    return img
}

fun main() {
    val seed: Long = 4

    val endCity = EndCity()
    val endCityGenerator = EndCityGenerator()
    val endGateway = EndGateway()

    val source = BiomeSource(seed)
    val terrain = TerrainGenerator(source)

    val region = BPos(1024, 0, 1696).toChunkPos().toRegionPos(endCity.spacing)

    // show information about an end city on seed 1
    val chunk = endCity.getInRegion(region.x, region.z, seed)
    val block = chunk.toBlockPos()

    val canSpawn = endCity.canSpawn(chunk.x, chunk.z, source)
    val canGen = endCityGenerator.generate(terrain, chunk)
    val hasShip = endCityGenerator.hasShip()

    println("chunk: $chunk")
    println("block: $block")
    println("canSpawn: $canSpawn")
    println("canGen: $canGen")
    if (canGen) println("hasShip: $hasShip")

    // print the generation order of end gateways on the main end island
    println(endGateway.getEndIslandGatewayOrder(source).contentToString())

    // can a random gateway spawn at these chunks?
    val gateways = listOf(
        CPos(60, -95),
        CPos(84, 140),
        CPos(66, 107)
    )
    for ((i, gateway) in gateways.withIndex()) {
        val canSpawn = endGateway.canStart(gateway.x, gateway.z, seed)
        println("gateway ${i+1}: $canSpawn")
    }

    // generate image of end islands around the city
    println("creating end island image....")
    val img = renderEndMap(512, 512, 1044, 1708, terrain)
    ImageIO.write(img, "png", File("end_map.png"))
    println("complete!")

}