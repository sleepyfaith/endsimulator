package lgbt.faith.structures

import lgbt.faith.biome.BiomeSource
import lgbt.faith.block.BPos
import lgbt.faith.rand.Rand

class EndGateway() {
    val linkedGateways =  arrayOf(
        BPos(96, 0), BPos(91, 29), BPos(77, 56), BPos(56, 77), BPos(29, 91),
        BPos(-1, 96), BPos(-30, 91), BPos(-57, 77), BPos(-78, 56), BPos(-92, 29),
        BPos(-96, -1), BPos(-92, -30), BPos(-78, -57), BPos(-57, -78), BPos(-30, -92),
        BPos(0, -96), BPos(29, -92), BPos(56, -78), BPos(77, -57), BPos(91, -30)
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