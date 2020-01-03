package muramasa.antimatter.machines.types;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.gtu.Ref;
import muramasa.antimatter.blocks.BlockMachine;
import muramasa.antimatter.gui.MenuHandler;
import muramasa.gtu.data.Machines;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.machines.MachineFlag;
import muramasa.antimatter.machines.MachineState;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.recipe.RecipeBuilder;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.texture.TextureData;
import muramasa.antimatter.texture.TextureType;
import muramasa.antimatter.tileentities.TileEntityMachine;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static muramasa.antimatter.machines.MachineFlag.RECIPE;

public class Machine implements IAntimatterObject {

    /** Global Members **/
    private static int lastInternalId;

    /** Basic Members **/
    protected int internalId; //TODO needed?
    protected Object2ObjectOpenHashMap<Tier, BlockMachine> blocks = new Object2ObjectOpenHashMap<>();
    protected TileEntityType tileType;
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
        this(id, TileEntityMachine::new, data);
    }

    public Machine(String id, Supplier<? extends TileEntityMachine> tile, Object... data) {
        internalId = lastInternalId++;
        addData(data);
        this.id = id;
        tiers.forEach(t -> blocks.put(t, new BlockMachine(this, t)));
        this.tileType = TileEntityType.Builder.create(tile, blocks.values().toArray(new BlockMachine[0])).build(null).setRegistryName(Ref.MODID, id);
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

    public ITextComponent getDisplayName(Tier tier) {
        return new TranslationTextComponent("machine." + id + "." + tier.getId());
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
            new Texture("block/machine/overlay/" + id + "/" + stateDir + TextureType.BOTTOM),
            new Texture("block/machine/overlay/" + id + "/" + stateDir + TextureType.TOP),
            new Texture("block/machine/overlay/" + id + "/" + stateDir + TextureType.FRONT),
            new Texture("block/machine/overlay/" + id + "/" + stateDir + TextureType.BACK),
            new Texture("block/machine/overlay/" + id + "/" + stateDir + TextureType.SIDE),
            new Texture("block/machine/overlay/" + id + "/" + stateDir + TextureType.SIDE),
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

    public void setTiers(Tier... tiers) {
        this.tiers = new ArrayList<>(Arrays.asList(tiers));
    }

    public void setGUI(MenuHandler menuHandler) {
        guiData = new GuiData(this, menuHandler);
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
    public Collection<BlockMachine> getBlocks() {
        return blocks.values();
    }

    @Nullable
    public BlockMachine getBlock(Tier tier) {
        return blocks.get(tier);
    }

    public TileEntityType getTileType() {
        return tileType;
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
