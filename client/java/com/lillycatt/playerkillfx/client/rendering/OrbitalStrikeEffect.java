package com.lillycatt.playerkillfx.client.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;

public class OrbitalStrikeEffect {
    public final Vec3d position;
    public final long startTime;

    private final static Vec3i PINK = new Vec3i(209, 98, 164);
    private final static Vec3i DARK_PINK = new Vec3i(181, 86, 144);
    private final static Vec3i WHITE = new Vec3i(255, 255, 255);
    private final static Vec3i ORANGE = new Vec3i(255, 154, 86);
    private final static Vec3i DARK_ORANGE = new Vec3i(239, 118, 39);

    private final static Vec3i[] Colors = new Vec3i[]
            {
                    DARK_PINK, PINK, WHITE, ORANGE, DARK_ORANGE, ORANGE, WHITE, PINK
            };

    public OrbitalStrikeEffect(Vec3d position) {
        this.startTime = System.currentTimeMillis();

        ClientWorld world = MinecraftClient.getInstance().world;

        if (world == null) {
            this.position =  position;
            return;
        }

        BlockPos surface = world.getTopPosition(
                Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                new BlockPos((int) Math.floor(position.x), 0, (int) Math.floor(position.z))
        );
        this.position = surface.toCenterPos();
    }

    public float getAgeSeconds() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public float getWidthFromAge() {
        float ageSeconds = getAgeSeconds();
        float duration = 3f;
        float t = ((4 * ageSeconds / duration) % 4) / 3;
        float d = 2.3f;
        return ((float)Math.min(Math.cbrt(((d * t) - 1.5f) / (d * 4.0f)) * d * t, 0.8d) + 0.38717f) / 1.18717f;
    }

    public Vec3i getColorFromAgeAndSegment(int segment) {
        return Colors[segment % Colors.length];
    }

    public float getAlpha() {
        float ageSeconds = getAgeSeconds();
        float duration = 3f;
        float t = ((4 * ageSeconds / duration) % 4) / 3;
        return Math.min(Math.min(20f * t * Math.abs(t - 0.45f), 1) * t, 1);
    }

    public static final Identifier TEXTURE =
            Identifier.of("playerkillfx", "textures/effect/blank.png");
}
