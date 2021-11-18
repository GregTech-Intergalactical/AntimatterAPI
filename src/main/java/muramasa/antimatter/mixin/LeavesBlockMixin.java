package muramasa.antimatter.mixin;

import muramasa.antimatter.Data;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
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
        ItemStack stack = builder.getOptionalParameter(LootParameters.TOOL);
        if (stack != null && !stack.isEmpty() && stack.getItem() instanceof IAntimatterTool) {
            IAntimatterTool tool = (IAntimatterTool) stack.getItem();
            if (tool.getAntimatterToolType() == Data.BRANCH_CUTTER) {
                ResourceLocation resourcelocation = this.getLootTable();
                if (resourcelocation == LootTables.EMPTY) {
                    return Collections.emptyList();
                }
                ServerWorld serverworld = builder.getLevel();
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
