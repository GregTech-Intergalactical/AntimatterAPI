package muramasa.antimatter.mixin;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.datagen.providers.AntimatterTagProvider;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(TagLoader.class)
public abstract class TagLoaderMixin {
    @Shadow
    @Final
    private String directory;

    @Inject(method = "build(Ljava/util/Map;)Ljava/util/Map;", at = @At("RETURN"))
    private <T> void onCreateLoadResult(Map<ResourceLocation, List<TagLoader.EntryWithSource>> map, CallbackInfoReturnable<Map<ResourceLocation, Collection<T>>> cir) {
        if (directory.equals("tags/items")) {
            try {
                Map<ResourceLocation, Collection<Holder<Item>>> tags = Utils.cast(cir.getReturnValue());
                Map<ResourceLocation, List<Item>> tagMap = Utils.cast(AntimatterTagProvider.TAGS_TO_REMOVE_GLOBAL.get(Registry.ITEM));
                tagMap.forEach((resourceLocation, items) -> {
                    if (tags.containsKey(resourceLocation)){
                        Collection<Holder<Item>> tag = tags.get(resourceLocation);
                        tag = new ArrayList<>(tag.stream().filter(i -> !items.contains(i.value())).toList());
                        tags.put(resourceLocation, tag);
                    }
                });
            } catch (Exception e) {
                Antimatter.LOGGER.error(e.getMessage(), e);
            }
        }
        if (directory.equals("tags/blocks")) {
            try {
                Map<ResourceLocation, Collection<Holder<Block>>> tags = Utils.cast(cir.getReturnValue());
                Map<ResourceLocation, List<Block>> tagMap = Utils.cast(AntimatterTagProvider.TAGS_TO_REMOVE_GLOBAL.get(Registry.BLOCK));
                tagMap.forEach((resourceLocation, items) -> {
                    if (tags.containsKey(resourceLocation)){
                        Collection<Holder<Block>> tag = tags.get(resourceLocation);
                        tag = new ArrayList<>(tag.stream().filter(i -> !items.contains(i.value())).toList());
                        tags.put(resourceLocation, tag);
                    }
                });
            } catch (Exception e) {
                Antimatter.LOGGER.error(e.getMessage(), e);
            }
        }
        if (directory.equals("tags/fluids")) {
            try {
                Map<ResourceLocation, Collection<Holder<Fluid>>> tags = Utils.cast(cir.getReturnValue());
                Map<ResourceLocation, List<Fluid>> tagMap = Utils.cast(AntimatterTagProvider.TAGS_TO_REMOVE_GLOBAL.get(Registry.FLUID));
                tagMap.forEach((resourceLocation, items) -> {
                    if (tags.containsKey(resourceLocation)){
                        Collection<Holder<Fluid>> tag = tags.get(resourceLocation);
                        tag = new ArrayList<>(tag.stream().filter(i -> !items.contains(i.value())).toList());
                        tags.put(resourceLocation, tag);
                    }
                });
            } catch (Exception e) {
                Antimatter.LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
