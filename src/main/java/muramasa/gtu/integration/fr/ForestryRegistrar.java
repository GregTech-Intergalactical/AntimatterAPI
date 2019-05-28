package muramasa.gtu.integration.fr;

import muramasa.gtu.Ref;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;
import net.minecraft.item.ItemStack;

import static muramasa.gtu.api.data.Materials.*;
import static muramasa.gtu.api.materials.ItemFlag.GENERATE_ORE;
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
                Apatite = new Material("apatite", 0xc8c8ff, DIAMOND).asGemBasic(false, GENERATE_ORE).add(Calcium, 5, Phosphate, 3, Chlorine, 1);
                break;
            case MATERIAL_INIT:
                ELEC.add(Apatite);
                NOSMELT.add(Apatite);
                NOSMASH.add(Apatite);
                CRYSTALLIZE.add(Apatite);
                Apatite.setOreMulti(4).setSmeltingMulti(4).setByProductMulti(2);
                Apatite.addByProduct(Phosphorus);
                Phosphorus.addByProduct(Apatite);
                Chrome.add(GENERATE_ORE);
                Osmium.add(GENERATE_ORE);
                Uranium235.add(GENERATE_ORE);
                Plutonium.add(GENERATE_ORE);
                Naquadria.add(GENERATE_ORE);
                break;
            case MACHINE_RECIPE:
                CombLoader.init();
                break;
        }
    }
}
