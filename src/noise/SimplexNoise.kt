package lgbt.faith.noise

import lgbt.faith.Utils
import lgbt.faith.rand.Rand
import kotlin.math.floor
import kotlin.math.sqrt

class SimplexNoise(rand: Rand) {

    private val permutations = ByteArray(256)

    init {

        rand.advance(3) // origin xyz, unused in simplex

        for (i in 0 until 256) {
            permutations[i] = i.toByte()
        }

        for (i in 0 until 256) {
            val j = i + rand.nextInt(256 - i)

            val tmp = permutations[i]
            permutations[i] = permutations[j]
            permutations[j] = tmp
        }
    }

    fun lookup(hash: Int): Int {
        return permutations[hash and 0xFF].toInt() and 0xFF
    }

    val SQRT_3 = sqrt(3.0)
    val SKEW_FACTOR_2D = 0.5 * (SQRT_3 - 1.0)
    val UNSKEW_FACTOR_2D = (3.0 - SQRT_3) / 6.0

    // sample3d unused in end simulation

    fun sample(x: Int, z: Int): Double {
        val hairyFactor = (x + z) * SKEW_FACTOR_2D

        val hairyX: Int = floor(x + hairyFactor).toInt()
        val hairyZ: Int = floor(z + hairyFactor).toInt()
        val mixedHairyXz = (hairyX + hairyZ).toDouble() * UNSKEW_FACTOR_2D
        val diffXToXz = hairyX.toDouble() - mixedHairyXz
        val diffZToXz = hairyZ.toDouble() - mixedHairyXz
        val x0 = x - diffXToXz
        val y0 = z - diffZToXz
        val offsetSecondCornerX: Byte
        val offsetSecondCornerZ: Byte

        if (x0 > y0) {
            offsetSecondCornerX = 1
            offsetSecondCornerZ = 0
        } else {
            offsetSecondCornerX = 0
            offsetSecondCornerZ = 1
        }

        val x1 = x0 - offsetSecondCornerX.toDouble() + UNSKEW_FACTOR_2D
        val y1 = y0 - offsetSecondCornerZ.toDouble() + UNSKEW_FACTOR_2D
        val x3 = x0 - 1.0 + 2.0 * UNSKEW_FACTOR_2D
        val y3 = y0 - 1.0 + 2.0 * UNSKEW_FACTOR_2D
        val ii = hairyX and 255
        val jj = hairyZ and 255
        val gi0: Int = lookup(ii + lookup(jj)) % 12
        val gi1: Int = lookup(ii + offsetSecondCornerX + lookup(jj + offsetSecondCornerZ)) % 12
        val gi2: Int = lookup(ii + 1 + lookup(jj + 1)) % 12
        val t0: Double = cornerNoise3d(gi0, x0, y0, 0.0, 0.5)
        val t1: Double = cornerNoise3d(gi1, x1, y1, 0.0, 0.5)
        val t2: Double = cornerNoise3d(gi2, x3, y3, 0.0, 0.5)
        return 70.0 * (t0 + t1 + t2)
    }

    fun cornerNoise3d(hash: Int, x: Double, y: Double, z: Double, max: Double): Double {
        var contr = max - x * x - y * y - z * z
        var result: Double
        if (contr < 0.0) {
            result = 0.0
        } else {
            contr *= contr
            result = contr * contr * Utils.grad(hash, x, y, z)
        }

        return result
    }
}