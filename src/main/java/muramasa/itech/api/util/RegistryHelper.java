package muramasa.itech.api.util;

import muramasa.itech.ITech;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RegistryHelper {

    public static void registerBlock(RegistryEvent.Register<Block> event, Block block) {
        event.getRegistry().register(block);
    }

    public static void registerBlock(RegistryEvent.Register<Block> event, Block block, Class tileClass, String tileName) {
        registerBlock(event, block);
        GameRegistry.registerTileEntity(tileClass, new ResourceLocation(ITech.MODID, tileName));
    }

    public static void registerItem(Item item) {

    }

    public static void registerItem(RegistryEvent.Register<Item> event, ItemBlock itemBlock) {

    }

    public static void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(ITech.MODID + ":" + id, "inventory"));
    }

    public static void RegisterItemModel(Item item, int meta, String model) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(model, "inventory"));
    }

    public static void RegisterItemModelVariant(Item item, int meta, String model, String variant) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(model, variant));
    }
}
