package net.minecraftforge.mixin.client;

import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockModel.GuiLight.class)
public interface GuiLightAccessor {
    @Accessor
    String getName();
}
