package muramasa.antimatter.fluid;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid.Flowing;
import net.minecraftforge.fluids.ForgeFlowingFluid.Properties;
import net.minecraftforge.fluids.ForgeFlowingFluid.Source;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * AntimatterFluid is an object that includes all essential information of what a normal fluid would compose of in Minecraft
 * Source + Flowing fluid instances, with a FlowingFluidBlock that handles the in-world block form of them.
 * Item instance is also provided, usually BucketItem or its derivatives.
 * But is generified to an Item instance: {@link net.minecraftforge.fluids.ForgeFlowingFluid#getFilledBucket}
 * <p>
 * TODO: generic getFluidContainer()
 * TODO: Cell Models
 */
public class AntimatterFluid implements ISharedAntimatterObject, IRegistryEntryProvider {

    public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("block/water_overlay");
    public static final ResourceLocation LIQUID_STILL_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/still");
    public static final ResourceLocation LIQUID_FLOW_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/flow");
    public static final ResourceLocation LIQUID_HOT_STILL_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/hot_still");
    public static final ResourceLocation LIQUID_HOT_FLOW_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/hot_flow");
    public static final ResourceLocation GAS_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/gas");
    public static final ResourceLocation GAS_FLOW_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/gas"); // _flow
    public static final ResourceLocation PLASMA_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/plasma");
    public static final ResourceLocation PLASMA_FLOW_TEXTURE = new ResourceLocation(Ref.ID, "block/liquid/plasma"); // _flow

    private final String domain, id;

    protected Properties fluidProperties;
    protected Source source;
    protected Flowing flowing;
    protected Block.Properties blockProperties;
    protected FluidAttributes attributes;
    protected FlowingFluidBlock fluidBlock;
    protected Item containerItem = Items.AIR;

    public AntimatterFluid(String domain, String id, FluidAttributes.Builder builder, Block.Properties blockProperties) {
        this.domain = domain;
        this.id = id;
        this.fluidProperties = new Properties(this::getFluid, this::getFlowingFluid, builder).bucket(this::getContainerItem).block(this::getFluidBlock);
        this.blockProperties = blockProperties;
        this.attributes = builder.translationKey("block." + domain + ".liquid." + id).build(this.source);
    }

    public AntimatterFluid(String domain, String id) {
        this(domain, id, getDefaultAttributesBuilder(), getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, FluidAttributes.Builder builder) {
        this(domain, id, builder, getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, ResourceLocation stillLoc, ResourceLocation flowLoc) {
        this(domain, id, FluidAttributes.builder(stillLoc, flowLoc), getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, Block.Properties properties) {
        this(domain, id, getDefaultAttributesBuilder(), properties);
    }

    @Override
    public void onRegistryBuild(IForgeRegistry<?> registry) {
        if (registry == ForgeRegistries.ITEMS) {
            AntimatterAPI.register(Item.class, getId() + "_bucket", getDomain(), containerItem = new BucketItem(this::getFluid, new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).tab(ItemGroup.TAB_MISC)).setRegistryName(getDomain(), getId() + "_bucket"));
        } else if (registry == ForgeRegistries.BLOCKS) {
            this.fluidBlock = new FlowingFluidBlock(this::getFluid, blockProperties);
            this.fluidBlock.setRegistryName(getDomain(), "block_fluid_".concat(getId()));
            AntimatterAPI.register(Block.class, "block_fluid_".concat(getId()), getDomain(), fluidBlock);
        } else if (registry == ForgeRegistries.FLUIDS) {
            this.source = new Source(this.fluidProperties);
            this.flowing = new Flowing(this.fluidProperties);
            this.source.setRegistryName(getDomain(), getId());
            this.flowing.setRegistryName(getDomain(), "flowing_".concat(getId()));
        }
    }

    public AntimatterFluid source(Source source) {
        this.source = source;
        return this;
    }

    public AntimatterFluid flowing(Flowing flowing) {
        this.flowing = flowing;
        return this;
    }

    public AntimatterFluid flowingBlock(FlowingFluidBlock fluidBlock) {
        this.fluidBlock = fluidBlock;
        return this;
    }

    public AntimatterFluid containerItem(Item item) {
        this.containerItem = item;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    public Properties getFluidProperties() {
        return fluidProperties;
    }

    public Block.Properties getBlockProperties() {
        return blockProperties;
    }

    public FluidAttributes getAttributes() {
        return attributes;
    }

    public Source getFluid() {
        return source;
    }

    public Flowing getFlowingFluid() {
        return flowing;
    }

    public FlowingFluidBlock getFluidBlock() {
        return fluidBlock;
    }

    public Item getContainerItem() {
        return containerItem;
    }

    protected static Block.Properties getDefaultBlockProperties() {
        return Block.Properties.of(Material.WATER).strength(100.0F).noDrops();
    }

    protected static FluidAttributes.Builder getDefaultAttributesBuilder() {
        return getDefaultAttributesBuilder(false);
    }

    protected static FluidAttributes.Builder getDefaultAttributesBuilder(boolean hot) {
        if (hot) {
            return FluidAttributes.builder(LIQUID_HOT_STILL_TEXTURE, LIQUID_HOT_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE);
        }
        return FluidAttributes.builder(LIQUID_STILL_TEXTURE, LIQUID_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE);
    }
}
