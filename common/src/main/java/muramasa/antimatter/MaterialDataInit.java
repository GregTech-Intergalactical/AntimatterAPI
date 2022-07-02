package muramasa.antimatter;

import muramasa.antimatter.event.MaterialEvent;

import static muramasa.antimatter.Data.*;

public class MaterialDataInit {
    public static void onMaterialEvent(MaterialEvent event){
        event.setMaterial(Data.NULL).addTools(5.0F, 5, Integer.MAX_VALUE, 3/*, ImmutableMap.of(Enchantments.BLOCK_FORTUNE, 3)*/).addHandleStat(0, 0.0F);
        event.setMaterial(Data.Stone).asDust(DUST_IMPURE, GEAR).addHandleStat(-10, -0.5F);
        event.setMaterial(Granite).asDust(ROCK);
        event.setMaterial(Diorite).asDust(ROCK);
        event.setMaterial(Andesite).asDust(ROCK);
        event.setMaterial(Deepslate).asDust(ROCK);
        event.setMaterial(Tuff).asDust(ROCK);

        event.setMaterial(Gravel).asDust(ROCK);
        event.setMaterial(Dirt).asDust(ROCK);
        event.setMaterial(Sand).asDust(ROCK);
        event.setMaterial(RedSand).asDust(ROCK);
        event.setMaterial(Sandstone).asDust(ROCK);
        event.setMaterial(Blackstone).asDust();

        event.setMaterial(Basalt).asDust(ROCK);
        event.setMaterial(Endstone).asDust();
        event.setMaterial(Netherrack).asDust();
        event.setMaterial(Prismarine).asDust();
        event.setMaterial(DarkPrismarine).asDust();
    }
}
