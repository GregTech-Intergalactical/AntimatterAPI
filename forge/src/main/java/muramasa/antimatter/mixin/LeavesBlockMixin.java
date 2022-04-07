package muramasa.antimatter.mixin;

import muramasa.antimatter.Data;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Collections;
import java.util.List;

@Mixin(LeavesBlock.class)
public abstract class LeavesBlockMixin extends Block {
    public LeavesBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> list = super.getDrops(state, builder);
        ItemStack stack = builder.getOptionalParameter(LootContextParams.TOOL);
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IAntimatterTool) {
            IAntimatterTool tool = (IAntimatterTool) stack.getItem();
            if (tool.getAntimatterToolType() == Data.BRANCH_CUTTER) {
                ResourceLocation resourcelocation = this.getLootTable();
                if (resourcelocation == BuiltInLootTables.EMPTY) {
                    return Collections.emptyList();
                }
                ServerLevel serverworld = builder.getLevel();
                LootTable loottable = serverworld.getServer().getLootTables().get(resourcelocation);
                ItemStack sapling = ItemStack.EMPTY;
                if (ForgeRegistries.BLOCKS.containsKey(new ResourceLocation(this.getRegistryName().toString().replace("leaves", "sapling")))) {
                    sapling = new ItemStack(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.getRegistryName().toString().replace("leaves", "sapling"))));
                }
                /*for (ItemStack stack1 : list){
                    if (stack1.getItem() instanceof BlockItem && ((BlockItem) stack1.getItem()).getBlock() instanceof SaplingBlock){
                        sapling = stack1.copy();
                        break;
                    }
                }*/
                if (!sapling.isEmpty()) {
                    list.clear();
                    list.add(sapling);
                }
            }
        }
        return list;
    }
}
