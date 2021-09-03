package muramasa.antimatter.item;

import muramasa.antimatter.Ref;
import muramasa.antimatter.client.RenderHelper;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.texture.Texture;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class ItemMultiTextureBattery extends ItemBattery{
    public ItemMultiTextureBattery(String domain, String id, Tier tier, long cap, boolean reusable) {
        super(domain, id, tier, cap, reusable);
        if (FMLEnvironment.dist.isClient()){
            RenderHelper.registerBatteryPropertyOverrides(this);
        }
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        String id = this.getId();
        ItemModelBuilder builders[] = new ItemModelBuilder[8];
        for (int i = 0; i < 8; i++){
            ItemModelBuilder builder = prov.getBuilder(id + i);
            builder.parent(new ModelFile.UncheckedModelFile(new ResourceLocation("minecraft","item/handheld")));
            builder.texture("layer0", new Texture(getDomain(), "item/basic/" + getId() + "/" + i));
            builders[i] = builder;
        }

        prov.tex(item, "minecraft:item/handheld", new Texture(getDomain(), "item/basic/" + getId() + "/1")).override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.0F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"0"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.01F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"1"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.173F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"2"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.336F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"3"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.499F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"4"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.662F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"5"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.825F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"6"))).end().override().predicate(new ResourceLocation(Ref.ID, "battery"), 0.99F).model(new ModelFile.UncheckedModelFile(new ResourceLocation(Ref.ID, "item/" + id +"7")));
    }
}
