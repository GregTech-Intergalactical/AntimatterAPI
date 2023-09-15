package muramasa.antimatter.mixin.fabric;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import muramasa.antimatter.common.event.CommonEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Debug(export = true)
@Mixin(LootTables.class)
public class LootTablesMixin {
    @Inject(method = "method_20711", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void injectSetId(ImmutableMap.Builder builder, ResourceLocation resourceLocation, JsonElement jsonElement, CallbackInfo ci, LootTable lootTable){
        CommonEvents.lootTableLoad(lootTable, resourceLocation);
    }
}
