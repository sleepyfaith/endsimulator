package lgbt.faith.rand

class Rand(var seed: Long = 0) {

    private val multiplier = 0x5DEECE66DL
    private val addend = 0xBL
    private val mask = (1L shl 48) - 1

    fun advance(n: Int) {
        repeat(n) {
            nextInt()
        }
    }

    fun next(bits: Int): Int {
        seed = (seed * multiplier + addend) and mask
        return (seed ushr (48-bits)).toInt()
    }

    fun nextFloat(): Float {
        return next(24) / (1 shl 24).toFloat()
    }

    fun nextDouble(): Double {
        return (((next(26).toLong() shl 27) + next(27).toLong()) * 1.1102230246251565E-16)
    }

    fun nextInt(): Int {
        return next(32)
    }
    fun nextLong(): Long {
        return (next(32).toLong() shl 32) + next(32).toLong()
    }

    fun setSeed(newSeed: Long): Long {
        seed = (newSeed xor multiplier) and mask
        return seed
    }

    fun nextInt(bound: Int): Int {
        require(bound > 0)
        if ((bound and -bound) == bound) {
            // power of two
            return ((bound.toLong() * next(31)) shr 31).toInt()
        }
        var bits: Int
        var value: Int
        do {
            bits = next(31)
            value = bits % bound
        } while (bits - value + (bound - 1) < 0)

        return value
    }

    fun setRegionSeed(worldSeed: Long, regionX: Int, regionZ: Int, salt: Int): Long {
        val seedVal: Long = regionX.toLong() * 341873128712L + regionZ.toLong() * 132897987541L + worldSeed + salt.toLong()
        setSeed(seedVal)
        return seedVal and mask
    }

    fun setCarverSeed(worldSeed: Long, chunkX: Int, chunkZ: Int): Long {
        setSeed(worldSeed)
        val a = nextLong()
        val b = nextLong()
        val seedVal = chunkX * a xor chunkZ * b xor worldSeed
        setSeed(seedVal)
        return seedVal and  mask
    }
}