package net.minecraftforge.mixin.client;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockElement.class)
public interface BlockElementAccessor {
    @Invoker("uvsByFace")
    float[] uvsByFace(Direction face);
}
