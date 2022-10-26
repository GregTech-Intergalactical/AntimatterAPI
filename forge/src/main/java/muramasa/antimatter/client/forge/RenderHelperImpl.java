package muramasa.antimatter.client.forge;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class RenderHelperImpl {
    public static void registerProperty(Item item, ResourceLocation location, ClampedItemPropertyFunction function){
        ItemProperties.register(item, location, function);
    }
}
