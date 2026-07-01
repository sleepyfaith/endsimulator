package lgbt.faith.rand

class Xoroshiro128PlusPlus(seed: Long = 0) {
    private var lo = 0L
    private var hi = 0L

    init {
        setSeed(seed)
    }

    // helpers
    private fun rotl(x: Long, k: Int): Long = (x shl k) or (x ushr (64 - k))

    private fun mix64(x: ULong): Long {
        var z = x

        z = (z xor (z shr 30)) * 0xbf58476d1ce4e5b9UL
        z = (z xor (z shr 27)) * 0x94d049bb133111ebUL

        return (z xor (z shr 31)).toLong()
    }

    // seed setting
    fun setSeed(value: Long) {
        val XL = 0x9e3779b97f4a7c15uL
        val XH = 0x6a09e667f3bcc909uL
        val A  = 0xbf58476d1ce4e5b9uL
        val B  = 0x94d049bb133111ebuL

        var l = value.toULong() xor XH
        var h = l + XL

        l = (l xor (l shr 30)) * A
        h = (h xor (h shr 30)) * A

        l = (l xor (l shr 27)) * B
        h = (h xor (h shr 27)) * B

        lo = (l xor (l shr 31)).toLong()
        hi = (h xor (h shr 31)).toLong()
    }

    fun setPopulationSeed(x: Int, z: Int, worldSeed: Long): Long {
        val xr = Xoroshiro128PlusPlus(worldSeed)

        val a = xr.nextLongJ() or 1L
        val b = xr.nextLongJ() or 1L

        val popSeed = (x.toLong() * a + z.toLong() * b) xor worldSeed
        setSeed(popSeed)
        return popSeed
    }

    fun setDecoratorSeed(x: Int, z: Int, worldSeed: Long, salt: Int): Long {
        val popSeed = setPopulationSeed(x, z, worldSeed)
        val decSeed = popSeed + salt

        setSeed(decSeed)
        return decSeed
    }


    // normal xoroshiro128++ advancing


    fun nextLong(): Long {
        val l = lo
        var h = hi

        val result = rotl(l + h, 17) + l

        h = h xor l
        lo = rotl(l, 49) xor h xor (h shl 21)
        hi = rotl(h, 28)

        return result
    }

    fun nextBits(bits: Int) = (nextLong() ushr (64 - bits)).toInt()

    fun nextFloat() = (nextLong() ushr 40) * 5.9604645E-8F

    fun nextDouble() = (nextLong() ushr 11) * 1.1102230246251565E-16

    fun nextInt() =  (nextLong() ushr 32).toInt()

    fun nextInt(bound: Int): Int {
        require(bound > 0)

        val m = bound - 1

        if ((bound and m) == 0) {
            val r = (nextLong() ushr 31)
            return ((bound.toLong() * r) shr 31).toInt()
        }

        while (true) {
            val bits = (nextLong() ushr 33).toInt()
            val valMod = bits % bound

            if (bits - valMod + m >= 0) {
                return valMod
            }
        }
    }

    // LCG style advancing
    fun nextIntJ(bound: Int): Int {
        require(bound > 0)

        val m = bound - 1

        if ((bound and m) == 0) {
            val x = bound.toLong() * (nextLong() ushr 33)
            return (x shr 31).toInt()
        }

        while (true) {
            val bits = (nextLong() ushr 33).toInt()
            val valMod = bits % bound

            if (bits - valMod + m >= 0) {
                return valMod
            }
        }
    }

    fun nextLongJ(): Long {
        val a = (nextLong() shr 32).toInt()
        val b = (nextLong() shr 32).toInt()

        return (a.toLong() shl 32) + b
    }


}