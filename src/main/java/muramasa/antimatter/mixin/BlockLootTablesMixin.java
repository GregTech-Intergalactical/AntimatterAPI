package muramasa.antimatter.mixin;

import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import net.minecraft.block.Block;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLootTables.class)
public abstract class BlockLootTablesMixin {

    @Inject(method = "droppingWithChancesAndSticks", at = @At("RETURN"), cancellable = true)
    private static void droppingWithBranchCutters(Block block, Block sapling, float[] chances, CallbackInfoReturnable<LootTable.Builder> ci){
        ci.setReturnValue(ci.getReturnValue().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).acceptCondition(AntimatterBlockLootProvider.BRANCH_CUTTER).addEntry(ItemLootEntry.builder(sapling))));
    }
}
