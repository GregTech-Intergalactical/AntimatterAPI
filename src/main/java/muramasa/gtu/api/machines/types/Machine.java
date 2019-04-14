package muramasa.gtu.api.machines.types;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.structure.Structure;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.GTLoc;
import muramasa.gtu.common.blocks.BlockMachine;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.RECIPE;

public class Machine implements IStringSerializable {

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId;
    protected BlockMachine block;
    protected Class tileClass;
    protected String name;
    protected ArrayList<Tier> tiers;
    protected int machineMask;

    /** Recipe Members **/
    protected RecipeMap recipeMap;

    /** GUI Members **/
    protected GuiData guiData;

    /** Multi Members **/
    protected Int2ObjectArrayMap<Structure> structures;

    //TODO get valid covers

    public Machine(String name) {
        this(name, new BlockMachine(name), TileEntityMachine.class);
    }

    public Machine(String name, BlockMachine block, Class tileClass) {
        internalId = lastInternalId++;
        this.name = name;
        this.block = block;
        this.tileClass = tileClass;
        setTiers(Tier.LV);
        Machines.add(this);
    }

    public int getInternalId() {
        return internalId;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName(Tier tier) {
        return GTLoc.get("machine." + name + "." + tier.getName() + ".name");
    }

    public RecipeMap getRecipeMap() {
        return recipeMap;
    }

    public List<Texture> getTextures() {
        ArrayList<Texture> textures = new ArrayList<>();
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE)));
        return textures;
    }

    public Texture getBaseTexture(Tier tier) {
        return tier.getBaseTexture();
    }

    public Texture[] getOverlayTextures(MachineState state) {
        String stateDir = state == MachineState.IDLE ? "" : state.getName() + "/";
        return new Texture[] {
            new Texture("blocks/machine/overlay/" + name + "/" + stateDir + TextureType.BOTTOM),
            new Texture("blocks/machine/overlay/" + name + "/" + stateDir + TextureType.TOP),
            new Texture("blocks/machine/overlay/" + name + "/" + stateDir + TextureType.FRONT),
            new Texture("blocks/machine/overlay/" + name + "/" + stateDir + TextureType.BACK),
            new Texture("blocks/machine/overlay/" + name + "/" + stateDir + TextureType.SIDE),
            new Texture("blocks/machine/overlay/" + name + "/" + stateDir + TextureType.SIDE),
        };
    }

    public ModelResourceLocation getOverlayModel(TextureType side) {
        return new ModelResourceLocation(Ref.MODID + ":machine/overlay/" + name + "/" + side.getName());
    }

    public Machine addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            machineMask |= flag.getBit();
            flag.add(this);
        }
        return this;
    }

    public void setFlags(MachineFlag... flags) {
        machineMask = 0;
        addFlags(flags);
    }

    public void setBlock(BlockMachine block) {
        this.block = block;
    }

    public void setTileClass(Class tileClass) {
        this.tileClass = tileClass;
    }

    public Machine setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
        return this;
    }

    public void addGUI(Object instance, int id) {
        guiData = new GuiData(this, instance, id);
        addFlags(MachineFlag.GUI);
    }

    public void addRecipeMap() {
        recipeMap = new RecipeMap(name, 10);
        addFlags(RECIPE);
    }

    public void addStructure(Structure structure) {
        addStructure(getFirstTier(), structure);
    }

    public void addStructure(Tier tier, Structure structure) {
        if (structures == null) structures = new Int2ObjectArrayMap<>();
        structures.put(tier.getInternalId(), structure);
    }

    public boolean hasFlag(MachineFlag flag) {
        return (machineMask & flag.getBit()) != 0;
    }

    /** Getters **/
    public BlockMachine getBlock() {
        return block;
    }

    public Class getTileClass() {
        return tileClass;
    }

    public int getMask() {
        return machineMask;
    }

    public Collection<Tier> getTiers() {
        return tiers;
    }

    public Tier getFirstTier() {
        return tiers.get(0);
    }

    public GuiData getGui() {
        return guiData;
    }

    public Structure getStructure(Tier tier) {
        return structures.get(tier.getInternalId());
    }

    /** Static Methods **/
    public static int getLastInternalId() {
        return lastInternalId;
    }
}
