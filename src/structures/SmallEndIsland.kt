package lgbt.faith.structures

import lgbt.faith.biome.BiomeSource
import lgbt.faith.block.BPos
import lgbt.faith.rand.Rand
import lgbt.faith.rand.Xoroshiro128PlusPlus

class SmallEndIsland() {
    data class Pos(val x: Int, val offsetY: Int, val z: Int)

    val spacing = 1

    val salt = 0
    val rarity = 1f/14

    fun getPos(chunkX: Int, chunkZ: Int, worldSeed: Long): Pos? {
        val xr = Xoroshiro128PlusPlus()
        xr.setDecoratorSeed(chunkX * 16, chunkZ * 16, worldSeed, salt)

        if (xr.nextFloat() >= rarity) {
            return null
        }

        val blockX  = (chunkX * 16) + xr.nextIntJ(16)
        val blockZ  = (chunkZ * 16) + xr.nextIntJ(16)
        val offsetY = xr.nextInt(7) + 3

        return Pos(blockX, offsetY, blockZ)
    }

    fun canStart(chunkX: Int, chunkZ: Int, worldSeed: Long): Boolean {
        getPos(chunkX, chunkZ, worldSeed) ?: return false

        return BiomeSource(worldSeed).sampleBiome((chunkX shl 2) + 2, (chunkZ shl 2) + 2) == "small_end_island"
    }
}