package muramasa.gtu.integration.fr;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;
import net.minecraft.item.ItemStack;

import static muramasa.gtu.api.data.Materials.*;
import static muramasa.gtu.api.materials.MaterialType.ORE;
import static muramasa.gtu.api.materials.TextureSet.DIAMOND;
import static muramasa.gtu.api.materials.RecipeFlag.*;

public class ForestryRegistrar implements IGregTechRegistrar {

    //TODO
    public static ItemStack FR_WAX = null;

    public static Material Apatite;

    @Override
    public String getId() {
        return Ref.MOD_FR;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case MATERIAL:
                Apatite = new Material("apatite", 0xc8c8ff, DIAMOND).asGemBasic(false, ORE).add(Calcium, 5, Phosphate, 3, Chlorine, 1);
                break;
            case MATERIAL_INIT:
                ELEC.add(Apatite);
                NOSMELT.add(Apatite);
                NOSMASH.add(Apatite);
                CRYSTALLIZE.add(Apatite);
                Apatite.setOreMulti(4).setSmeltingMulti(4).setByProductMulti(2);
                Apatite.addByProduct(Phosphorus);
                Phosphorus.addByProduct(Apatite);
                Chrome.add(ORE);
                Osmium.add(ORE);
                Uranium235.add(ORE);
                Plutonium.add(ORE);
                Naquadria.add(ORE);
                break;
            case RECIPE:
                CombLoader.init();
                break;
        }
    }
}
