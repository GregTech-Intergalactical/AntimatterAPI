package muramasa.antimatter.machine.types;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.RecipeBuilder;
import muramasa.antimatter.recipe.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.structure.Structure;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.texture.TextureData;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

public class Machine<T extends Machine<T>> implements IAntimatterObject, IRegistryEntryProvider {

    /** Basic Members **/
    protected TileEntityType<?> tileType;
    protected Function<Machine<?>, Supplier<? extends TileEntityMachine>> tileFunc = m -> () -> new TileEntityMachine(this);
    protected String domain, id;
    protected Set<Tier> tiers = ImmutableSet.of();

    /** Recipe Members **/
    protected RecipeMap<?> recipeMap;

    /** GUI Members **/
    protected GuiData guiData;
    protected ItemGroup group = Ref.TAB_MACHINES;

    /** Texture Members **/
    protected TextureData baseData;
    protected Texture baseTexture;

    /** Multi Members **/
    protected Object2ObjectMap<Tier, Structure> structures = new Object2ObjectOpenHashMap<>();

    //TODO get valid covers

    public Machine(String domain, String id, Object... data) {
        addData(data);
        this.domain = domain;
        this.id = id;
        AntimatterAPI.register(Machine.class, id, this);
    }

    @Override
    public void onRegistryBuild(String domain, IForgeRegistry<?> registry) {
        if (!this.domain.equals(domain) || registry != ForgeRegistries.BLOCKS) return;
        tileType = new TileEntityType<>(tileFunc.apply(this), tiers.stream().map(t -> new BlockMachine(this, t)).collect(Collectors.toSet()), null).setRegistryName(domain, id);
        AntimatterAPI.register(TileEntityType.class, getId(), getTileType());
    }

    protected void addData(Object... data) {
        List<Tier> tiers = new ObjectArrayList<>();
        List<MachineFlag> flags = new ObjectArrayList<>();
        for (Object o : data) {
            if (o instanceof RecipeMap) {
                recipeMap = (RecipeMap<?>) o;
                flags.add(RECIPE);
            }
            if (o instanceof Tier) tiers.add((Tier) o);
            if (o instanceof MachineFlag) flags.add((MachineFlag) o);
            if (o instanceof Texture) baseTexture = (Texture) o;
            if (o instanceof ItemGroup) group = (ItemGroup) o;
            //if (data[i] instanceof ITextureHandler) baseData = ((ITextureHandler) data[i]);
        }
        setTiers(tiers.size() > 0 ? tiers.toArray(new Tier[0]) : Tier.getStandard());
        addFlags(flags.toArray(new MachineFlag[0]));
    }

    public T setTile(Function<Machine<?>, Supplier<? extends TileEntityMachine>> func) {
        this.tileFunc = func;
        return (T) this;
    }

    public T setTile(Supplier<? extends TileEntityMachine> supplier) {
        setTile(m -> supplier);
        return (T) this;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public ITextComponent getDisplayName(Tier tier) {
        return new TranslationTextComponent("machine." + id + '.' + tier.getId());
    }

    public List<Texture> getTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        for (Tier tier : getTiers()) {
            //textures.addAll(Arrays.asList(baseHandler.getBase(this, tier)));
            textures.add(getBaseTexture(tier));
        }
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.IDLE)));
        textures.addAll(Arrays.asList(getOverlayTextures(MachineState.ACTIVE)));
        return textures;
    }

    public Texture getBaseTexture(Tier tier) {
        return baseTexture != null ? baseTexture : tier.getBaseTexture();
    }

    public Texture[] getOverlayTextures(MachineState state) {
        String stateDir = state == MachineState.IDLE ? "" : state.getId() + '/';
        return new Texture[] {
            new Texture(getDomain(), "block/machine/overlay/" + id + '/' + stateDir + "bottom"),
            new Texture(getDomain(), "block/machine/overlay/" + id + '/' + stateDir + "top"),
            new Texture(getDomain(), "block/machine/overlay/" + id + '/' + stateDir + "front"),
            new Texture(getDomain(), "block/machine/overlay/" + id + '/' + stateDir + "back"),
            new Texture(getDomain(), "block/machine/overlay/" + id + '/' + stateDir + "side"),
            new Texture(getDomain(), "block/machine/overlay/" + id + '/' + stateDir + "side"),
        };
    }

    public ResourceLocation getOverlayModel(Direction side) {
        return new ResourceLocation(getDomain(), "block/machine/overlay/" + id + '/' + side.getName());
    }

    public RecipeMap<?> getRecipeMap() {
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
        this.tiers = ImmutableSet.copyOf(tiers);
    }

    public void setGUI(MenuHandlerMachine<?> menuHandler) {
        guiData = new GuiData<>(this, menuHandler);
        addFlags(MachineFlag.GUI);
    }

    public void setStructure(Structure structure) {
        getTiers().forEach(t -> setStructure(t, structure));
    }

    public void setStructure(Tier tier, Structure structure) {
        structures.put(tier, structure);
    }

    public boolean has(MachineFlag flag) {
        return flag.getTypes().contains(this);
    }

    /** Getters **/
    public TileEntityType<?> getTileType() {
        return tileType;
    }

    public Collection<Tier> getTiers() {
        return tiers;
    }

    public GuiData<?> getGui() {
        return guiData;
    }

    public ItemGroup getGroup() {
        return group;
    }

    public Structure getStructure(Tier tier) {
        return structures.get(tier);
    }

    /** Static Methods **/
    public static Machine<?> get(String name) {
        Machine<?> machine = AntimatterAPI.get(Machine.class, name);
        return machine != null ? machine : Data.MACHINE_INVALID;
    }

    public static Collection<Machine<?>> getTypes(MachineFlag... flags) {
        List<Machine<?>> types = new ObjectArrayList<>();
        for (MachineFlag flag : flags) {
            types.addAll(flag.getTypes());
        }
        return types;
    }
}
