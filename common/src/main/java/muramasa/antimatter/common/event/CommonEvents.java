package muramasa.antimatter.common.event;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.AntimatterDynamics;
import muramasa.antimatter.datagen.providers.AntimatterBlockLootProvider;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.proxy.ClientHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class CommonEvents {
    public static void lootTableLoad(LootTable table, ResourceLocation name){
        if (table.getLootTableId().getPath().startsWith("blocks/")) {
            ResourceLocation blockId = new ResourceLocation(table.getLootTableId().getNamespace(), name.getPath().replace("blocks/", ""));
            if (AntimatterPlatformUtils.blockExists(blockId)) {
                Block block = AntimatterPlatformUtils.getBlockFromId(blockId);
                //Antimatter.LOGGER.info(blockId.toString());
                if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE) {
                    table.addPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(AntimatterBlockLootProvider.SAW).add(LootItem.lootTableItem(block)).build());
                }
            }
        }
    }

    public static boolean anvilUpdate(ItemStack left, ItemStack right){
        if (left.getItem() == right.getItem()) {
            if (left.getItem() instanceof IAntimatterTool leftTool && right.getItem() instanceof IAntimatterTool rightTool) {
                if (leftTool.getPrimaryMaterial(left) != rightTool.getPrimaryMaterial(right) || leftTool.getSecondaryMaterial(left) != rightTool.getSecondaryMaterial(right)) {
                    return true;
                }
            } else if (left.getItem() instanceof IAntimatterArmor && right.getItem() instanceof IAntimatterArmor) {
                IAntimatterArmor leftTool = (IAntimatterArmor) left.getItem();
                IAntimatterArmor rightTool = (IAntimatterArmor) right.getItem();
                if (leftTool.getMaterial(left) != rightTool.getMaterial(right)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void placeBlock(BlockState placedOff, Entity entity, LevelAccessor world, BlockPos pos, BlockState placedBlock){
        if (placedOff.getBlock() instanceof BlockPipe && !(placedBlock.getBlock() instanceof BlockPipe)){
            if (entity instanceof Player && !entity.isCrouching()){
                BlockEntity pipe = world.getBlockEntity(pos.relative(entity.getDirection()));
                if (pipe instanceof TileEntityPipe && placedBlock.getBlock() instanceof EntityBlock){
                    ((TileEntityPipe<?>)pipe).setConnection(entity.getDirection().getOpposite());
                }
            }
        }
    }

    public static void onItemCrafted(Container inv, Player player){
        if (!AntimatterConfig.GAMEPLAY.PLAY_CRAFTING_SOUNDS) return;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (inv.getItem(i).getItem() instanceof IAntimatterTool tool) {
                SoundEvent type = tool.getAntimatterToolType().getUseSound();
                if (type != null) {
                    player.playSound(type, 0.75F, 0.75F);
                }
            }
        }
    }

    public static void onContainerOpen(Player player, AbstractContainerMenu container){
        if (player instanceof ServerPlayer serverPlayer) {
            if (container instanceof IAntimatterContainer antimatterContainer) {
                antimatterContainer.listeners().add(serverPlayer);
            }
        }
    }


    /**
     * Recipe event for local servers, builds recipes.
     * @param manager recipe manager.
     */
    public static void recipeEvent(RecipeManager manager) {
        if (ClientHandler.isLocal()) {
            //AntimatterDynamics.onResourceReload(false);
            AntimatterDynamics.onRecipeCompile(false, manager);
        }
    }

    /**
     * Recipe event for online server, builds recipes.
     */
    public static void tagsEvent() {
        if (!ClientHandler.isLocal()) {
            AntimatterDynamics.onResourceReload(false, false);
            AntimatterDynamics.onRecipeCompile(true, Minecraft.getInstance().getConnection().getRecipeManager());
        }
    }
}
