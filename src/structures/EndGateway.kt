package lgbt.faith.structures

import lgbt.faith.biome.BiomeSource
import lgbt.faith.block.CPos
import lgbt.faith.rand.Rand

class EndGateway() {
    val linkedGateways =  arrayOf(
        CPos(96, 0), CPos(91, 29), CPos(77, 56), CPos(56, 77), CPos(29, 91),
        CPos(-1, 96), CPos(-30, 91), CPos(-57, 77), CPos(-78, 56), CPos(-92, 29),
        CPos(-96, -1), CPos(-92, -30), CPos(-78, -57), CPos(-57, -78), CPos(-30, -92),
        CPos(0, -96), CPos(29, -92), CPos(56, -78), CPos(77, -57), CPos(91, -30)
    )

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
}