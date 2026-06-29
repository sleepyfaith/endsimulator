package lgbt.faith.biome

import lgbt.faith.noise.SimplexNoise
import lgbt.faith.rand.Rand
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class BiomeSource(val worldSeed: Long) {
    private val simplex: SimplexNoise

    init {
        val rand = Rand().apply { setSeed(worldSeed) }
        rand.advance(17292)
        simplex = SimplexNoise(rand)
    }

    fun sampleSimplex(x: Int, z: Int): Boolean {
        return simplex.sample(x, z) < (-0.9f).toDouble()
    }

    // height
    fun clamp(v: Float): Float {
        if (v < -100.0f) return -100.0f
        return v.coerceAtMost(80.0f)
    }

    fun getHeight(x: Int, z: Int): Float {
        val scaledX = x / 2
        val scaledZ = z / 2
        val oddX = x % 2
        val oddZ = z % 2

        var height: Float = 100.0f - sqrt(((x * x + z * z).toFloat()).toDouble()).toFloat() * 8.0f

        height = clamp(height)

        for (rx in -12..12) {
            for (rz in -12..12) {
                val shiftedX = (scaledX + rx)
                val shiftedZ = (scaledZ + rz)

                if (shiftedX * shiftedX + shiftedZ * shiftedZ > 4096
                    && sampleSimplex(shiftedX, shiftedZ)) {

                    val elevation = (abs(shiftedX.toFloat()) * 3439.0f + abs(shiftedZ.toFloat()) * 147.0f) % 13.0f + 9.0f
                    val smoothX = (oddX - rx * 2).toFloat()
                    val smoothZ = (oddZ - rz * 2).toFloat()
                    val noise = clamp(100.0f - sqrt(smoothX * smoothX + smoothZ * smoothZ) * elevation)
                    height = max(height, noise)
                }

            }
        }
        return height
    }

    // biome
    fun sampleBiome(_x: Int, _z: Int): String {
        val x = _x shr 2
        val z = _z shr 2

        if (x.toLong() * x.toLong() + z.toLong() * z.toLong() <= 4096) {
            return "the_end"
        }

        val height = getHeight(x * 2 + 1, z * 2 + 1)

        if (height > 40.0f) {
            return "end_highlands"
        } else if (height >= 0.0f) {
            return "end_midlands"
        } else if (height >= -20.0f) {
            return "end_barrens"
        }

        return "small_end_islands"
    }
}