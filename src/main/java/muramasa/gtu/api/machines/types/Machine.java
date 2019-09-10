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
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.texture.TextureType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.RECIPE;

public class Machine implements IGregTechObject {

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId;
    protected BlockMachine block;
    protected Class<? extends TileEntityMachine> tileClass;
    protected String id;
    protected ArrayList<Tier> tiers = new ArrayList<>();

    /** Recipe Members **/
    protected RecipeMap recipeMap;

    /** GUI Members **/
    protected GuiData guiData;

    /** Texture Members **/
    protected TextureData baseData;
    protected Texture baseTexture;

    /** Multi Members **/
    protected Int2ObjectOpenHashMap<Structure> structures = new Int2ObjectOpenHashMap<>();

    //TODO get valid covers

    public Machine(String id, Object... data) {
        this(id, TileEntityMachine.class, data);
    }

    public Machine(String id, Class<? extends TileEntityMachine> tileClass, Object... data) {
        internalId = lastInternalId++;
        this.id = id;
        this.block = new BlockMachine(this);
        this.tileClass = tileClass;
        addData(data);
        Machines.add(this);
    }

    protected void addData(Object... data) {
        ArrayList<Tier> tiers = new ArrayList<>();
        ArrayList<MachineFlag> flags = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            if (data[i] instanceof RecipeMap) {
                recipeMap = (RecipeMap) data[i];
                flags.add(RECIPE);
            }
            if (data[i] instanceof Tier) tiers.add((Tier) data[i]);
            if (data[i] instanceof MachineFlag) flags.add((MachineFlag) data[i]);
            if (data[i] instanceof Texture) baseTexture = (Texture) data[i];
            //if (data[i] instanceof ITextureHandler) baseData = ((ITextureHandler) data[i]);
        }
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        addFlags(flags.toArray(new MachineFlag[0]));
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
        for (Tier tier : getTiers()) {
            //textures.addAll(Arrays.asList(baseHandler.getBase(this, tier)));
            textures.add(getBaseTexture(tier));
        }
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE)));
        return textures;
    }

    public TextureData getTextureData(Tier tier, MachineState state) {
        //if (baseHandler != null) {
            //return new TextureData().base(baseHandler.getBase(this, tier)).overlay(getOverlayTextures(state));
        //} else {
            return new TextureData().base(getBaseTexture(tier), getBaseTexture(tier), getBaseTexture(tier), getBaseTexture(tier), getBaseTexture(tier), getBaseTexture(tier)).overlay(getOverlayTextures(state));
        //}
    }

    public Texture getBaseTexture(Tier tier) {
        return baseTexture != null ? baseTexture : tier.getBaseTexture();
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
        Arrays.stream(flags).forEach(f -> f.add(this));
    }

    public void setFlags(MachineFlag... flags) {
        Arrays.stream(MachineFlag.VALUES).forEach(f -> f.getTypes().remove(this));
        addFlags(flags);
    }

    public void setBlock(BlockMachine block) {
        this.block = block;
    }

    public void setTileClass(Class<? extends TileEntityMachine> tileClass) {
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
        getTiers().forEach(t -> setStructure(t, structure));
    }

    public void setStructure(Tier tier, Structure structure) {
        structures.put(tier.getInternalId(), structure);
    }

    public boolean hasFlag(MachineFlag flag) {
        return flag.getTypes().contains(this);
    }

    /** Getters **/
    public BlockMachine getBlock() {
        return block;
    }

    public Class<? extends TileEntityMachine> getTileClass() {
        return tileClass;
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
