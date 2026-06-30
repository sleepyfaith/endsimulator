package lgbt.faith.structures

import lgbt.faith.biome.BiomeSource
import lgbt.faith.block.BPos
import lgbt.faith.block.CPos
import lgbt.faith.rand.Rand
import lgbt.faith.rand.Xoroshiro128PlusPlus

class EndGateway() {
    val linkedGateways =  arrayOf(
        BPos(96, 0), BPos(91, 29), BPos(77, 56), BPos(56, 77), BPos(29, 91),
        BPos(-1, 96), BPos(-30, 91), BPos(-57, 77), BPos(-78, 56), BPos(-92, 29),
        BPos(-96, -1), BPos(-92, -30), BPos(-78, -57), BPos(-57, -78), BPos(-30, -92),
        BPos(0, -96), BPos(29, -92), BPos(56, -78), BPos(77, -57), BPos(91, -30)
    )

    val spacing = 1

    val salt = 40000
    val rarity = 1f/700

    fun getEndIslandGatewayOrder(biomeSource: BiomeSource): IntArray {
        val order = IntArray(linkedGateways.size) { it }

        val rand = Rand().apply { setSeed(biomeSource.worldSeed) }

        for (i in order.indices) {
            val j = rand.nextInt(order.size - i) + i
            val tmp = order[i]
            order[i] = order[j]
            order[j] = tmp
        }

        return order
    }


    fun canStart(chunkX: Int, chunkZ: Int, worldSeed: Long): Boolean {
        val x = chunkX * 16
        val z = chunkZ * 16

        var xr = Xoroshiro128PlusPlus(worldSeed)

        val a = xr.nextLongJ() or 1L
        val b = xr.nextLongJ() or 1L

        val populationSeed = (x.toLong() * a + z.toLong() * b) xor worldSeed

        xr = Xoroshiro128PlusPlus(populationSeed + salt)

        if (xr.nextFloat() >= rarity) {
            return false
        }

        return true
    }
}