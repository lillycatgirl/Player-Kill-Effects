package com.lillycatt.playerkillfx.client;

import com.lillycatt.playerkillfx.client.rendering.OrbitalRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.util.math.Vec3d;

public class PlayerKillFXClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(OrbitalRenderer::Render);
        OrbitalRenderer.AddEffect(new Vec3d(0,73,-100));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
        });
    }
}
