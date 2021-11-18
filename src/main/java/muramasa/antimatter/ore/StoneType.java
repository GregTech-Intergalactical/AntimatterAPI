package muramasa.antimatter.ore;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class StoneType implements ISharedAntimatterObject, IRegistryEntryProvider {

    private final String domain, id;
    //private int harvestLevel;
    private boolean gravity, requiresTool;
    public boolean generateBlock = false;
    private final Material material;
    private final Texture texture;
    private final SoundType soundType;
    private BlockState state;
    private Supplier<BlockState> stateSupplier;
    private int harvestLevel, fallingDustColor;
    private float hardness, resistence;
    private ToolType toolType;
    private net.minecraft.block.material.Material blockMaterial;

    public StoneType(String domain, String id, Material material, Texture texture, SoundType soundType, boolean generateBlock) {
        this.domain = domain;
        this.id = id;
        this.material = material;
        this.texture = texture;
        this.soundType = soundType;
        this.generateBlock = generateBlock;
        this.gravity = false;
        this.harvestLevel = 0;
        this.requiresTool = true;
        this.hardness = 1.5F;
        this.resistence = 6.0F;
        this.toolType = ToolType.PICKAXE;
        this.fallingDustColor = -16777216;
        this.blockMaterial = net.minecraft.block.material.Material.STONE;
    }

    public StoneType setHarvestLevel(int harvestLevel) {
        this.harvestLevel = harvestLevel;
        return this;
    }

    public StoneType setHardnessAndResistance(float hardnessAndResistence) {
        this.hardness = hardnessAndResistence;
        this.resistence = hardnessAndResistence;
        return this;
    }

    public StoneType setHardnessAndResistance(float hardness, float resistence) {
        this.hardness = hardness;
        this.resistence = resistence;
        return this;
    }

    public StoneType setGravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public StoneType setRequiresTool(boolean requiresTool) {
        this.requiresTool = requiresTool;
        return this;
    }

    public StoneType setFallingDustColor(int fallingDustColor) {
        this.fallingDustColor = fallingDustColor;
        return this;
    }

    public StoneType setToolType(ToolType toolType) {
        this.toolType = toolType;
        return this;
    }

    public StoneType setToolType(String toolType) {
        this.toolType = ToolType.get(toolType);
        return this;
    }

    public StoneType setBlockMaterial(net.minecraft.block.material.Material material) {
        this.blockMaterial = material;
        return this;
    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (generateBlock && registry == ForgeRegistries.BLOCKS) setState(new BlockStone(this));
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public String getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public BlockState getState() {
        return state;
    }

    public Texture getTexture() {
        return texture;
    }

    public SoundType getSoundType() {
        return soundType;
    }

    public ToolType getToolType() {
        return toolType;
    }

    public int getFallingDustColor() {
        return fallingDustColor;
    }

    public net.minecraft.block.material.Material getBlockMaterial() {
        return blockMaterial;
    }

    public boolean doesGenerateBlock() {
        return generateBlock;
    }

    public boolean doesRequireTool() {
        return requiresTool;
    }

    public StoneType setState(Block block) {
        this.state = block.defaultBlockState();
        return this;
    }

    public StoneType setState(BlockState blockState) {
        this.state = blockState;
        return this;
    }

    public StoneType setStateSupplier(Supplier<BlockState> blockState) {
        this.stateSupplier = blockState;
        return this;
    }

    public int getHarvestLevel() {
        return harvestLevel;
    }

    public float getHardness() {
        return hardness;
    }

    public float getResistence() {
        return resistence;
    }

    public boolean getGravity() {
        return gravity;
    }

    public static Collection<Texture> getAllTextures() {
        List<Texture> textures = new ObjectArrayList<>();
        for (StoneType type : getAllGeneratingBlock()) {
            textures.add(type.getTexture());
        }
        return textures;
    }

    //TODO collection
    public static StoneType[] getAllGeneratingBlock() {
        return AntimatterAPI.all(StoneType.class).stream().filter(s -> s.generateBlock).toArray(StoneType[]::new);
    }

    public void initSuppliedState() {
        if (state == null && stateSupplier != null) {
            state = stateSupplier.get();
        }
    }

    public static StoneType get(String id) {
        return AntimatterAPI.get(StoneType.class, id);
    }
}
