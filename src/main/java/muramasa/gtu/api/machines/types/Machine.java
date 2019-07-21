package muramasa.gtu.api.machines.types;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockMachine;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.GuiData;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.structure.Structure;
import muramasa.gtu.api.texture.Texture;
import muramasa.gtu.api.texture.TextureType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Machine implements IGregTechObject {

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId;
    protected BlockMachine block;
    protected Class tileClass;
    protected String id;
    protected ArrayList<Tier> tiers;
    protected int machineMask;

    /** Recipe Members **/
    protected RecipeMap recipeMap;

    /** GUI Members **/
    protected GuiData guiData;

    /** Multi Members **/
    protected Int2ObjectOpenHashMap<Structure> structures;

    //TODO get valid covers

    public Machine(String id) {
        this(id, TileEntityMachine.class);
    }

    public Machine(String id, Class tileClass) {
        internalId = lastInternalId++;
        this.id = id;
        this.block = new BlockMachine(this);
        this.tileClass = tileClass;
        setTiers(Tier.LV);
        Machines.add(this);
    }

    public int getInternalId() {
        return internalId;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getDisplayName(Tier tier) {
        return Utils.trans("machine." + id + "." + tier.getId() + ".name");
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
        String stateDir = state == MachineState.IDLE ? "" : state.getId() + "/";
        return new Texture[] {
            new Texture("blocks/machine/overlay/" + id + "/" + stateDir + TextureType.BOTTOM),
            new Texture("blocks/machine/overlay/" + id + "/" + stateDir + TextureType.TOP),
            new Texture("blocks/machine/overlay/" + id + "/" + stateDir + TextureType.FRONT),
            new Texture("blocks/machine/overlay/" + id + "/" + stateDir + TextureType.BACK),
            new Texture("blocks/machine/overlay/" + id + "/" + stateDir + TextureType.SIDE),
            new Texture("blocks/machine/overlay/" + id + "/" + stateDir + TextureType.SIDE),
        };
    }

    public ModelResourceLocation getOverlayModel(TextureType side) {
        return new ModelResourceLocation(Ref.MODID + ":machine/overlay/" + id + "/" + side.getId());
    }

    public RecipeMap getRecipeMap() {
        return recipeMap;
    }

    public RecipeBuilder getRecipeBuilder() {
        return recipeMap.RB();
    }

    public void addFlags(MachineFlag... flags) {
        for (MachineFlag flag : flags) {
            machineMask |= flag.getBit();
            flag.add(this);
        }
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

    public void setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
    }

    public void setGUI(Object instance, int id) {
        guiData = new GuiData(this, instance, id);
        addFlags(MachineFlag.GUI);
    }

    public void setStructure(Structure structure) {
        setStructure(getFirstTier(), structure);
    }

    public void setStructure(Tier tier, Structure structure) {
        if (structures == null) structures = new Int2ObjectOpenHashMap<>();
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
