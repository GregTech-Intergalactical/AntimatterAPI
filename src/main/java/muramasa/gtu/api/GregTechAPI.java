package muramasa.gtu.api;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.ICoverHandler;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.cover.impl.*;
import muramasa.gtu.api.data.Casing;
import muramasa.gtu.api.data.Coil;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.Prefix;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.integration.jei.GregTechJEIPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.Collection;
import java.util.HashMap;

public class GregTechAPI {

    /** Item Registry Section **/
    public static void addItemReplacement(Prefix prefix, Material material, ItemStack stack) {
        prefix.addReplacement(material, stack);
    }

    /** Block Registry Section **/
    public static void addCasing(String name) {
        new Casing(name);
    }

    public static void addCoil(String name, int heatingCapacity) {
        new Coil(name, heatingCapacity);
    }

    /** JEI Registry Section **/
    public static void registerJEICategory(RecipeMap map, GuiData gui) {
        if (Utils.isModLoaded(Ref.MOD_JEI)) {
            GregTechJEIPlugin.registerCategory(map, gui);
        }
    }

    /** Cover Registry Section **/
    private static HashMap<String, Cover> COVER_REGISTRY = new HashMap<>();
    private static HashMap<Item, Cover> CATALYST_TO_COVER = new HashMap<>();

    public static Cover CoverNone = new CoverNone();
    public static Cover CoverPlate = new CoverPlate();
    public static Cover CoverItem = new CoverItem();
    public static Cover CoverFluid = new CoverFluid();
    public static Cover CoverEnergy = new CoverEnergy();
    public static Cover CoverMonitor = new CoverMonitor();

    /**
     * Registers a cover behaviour. This must be done during preInit.
     * @param cover The behaviour instance to be attached.
     */
    public static void registerCover(Cover cover) {
        cover.onRegister();
        COVER_REGISTRY.put(cover.getName(), cover);
    }

    public static Cover getCover(String name) {
        return COVER_REGISTRY.get(name);
    }

    public static void registerCoverCatalyst(ItemStack stack, Cover cover) {
        CATALYST_TO_COVER.put(stack.getItem(), cover);
    }

    public static Cover getCoverFromCatalyst(ItemStack stack) {
        return CATALYST_TO_COVER.get(stack.getItem());
    }

    public static Collection<Cover> getRegisteredCovers() {
        return COVER_REGISTRY.values();
    }

    /** Attempts to place a cover on a tile at a given side **/
    public static boolean placeCover(TileEntity tile, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (stack.isEmpty()) return false;
        ICoverHandler coverHandler = tile.getCapability(GTCapabilities.COVERABLE, side);
        if (coverHandler == null) return false;
        Cover cover = GregTechAPI.getCoverFromCatalyst(stack);
        if (cover == null) return false;
        if (coverHandler.set(Utils.getInteractSide(side, hitX, hitY, hitZ), cover.getNewInstance(Utils.ca(1, stack)))) {
            stack.shrink(1);
            return true;
        }
        return false;
    }

    /** Attempts to remove a cover at a given side **/
    public static boolean removeCover(EntityPlayer player, ICoverHandler coverHandler, EnumFacing side) {
        ItemStack toDrop = coverHandler.get(side).getDroppedStack();
        if (coverHandler.set(side, CoverNone)) {
            player.dropItem(toDrop, false);
            return true;
        }
        return false;
    }
}
