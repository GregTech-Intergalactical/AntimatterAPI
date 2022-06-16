package net.minecraftforge.mixin.client;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;

@Debug(export = true)
@Mixin(ModelBlockRenderer.class)
public class MixinModelBlockRenderer {
}
