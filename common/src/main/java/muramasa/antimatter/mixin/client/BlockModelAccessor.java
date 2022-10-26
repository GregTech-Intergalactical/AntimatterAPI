package muramasa.antimatter.mixin.client;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(BlockModel.class)
public interface BlockModelAccessor {
    @Accessor
    Map<String, Either<Material, String>> getTextureMap();

    @Invoker("bakeFace")
    static BakedQuad invokeBakeFace(BlockElement part, BlockElementFace partFace, TextureAtlasSprite sprite, Direction direction, ModelState transform, ResourceLocation location){
        throw new AssertionError();
    }
}
