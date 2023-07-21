package muramasa.antimatter.fluid;

import earth.terrarium.botarium.common.registry.fluid.*;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.registration.RegistryType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;

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

    public static final FluidRegistry FLUID_TYPES = new FluidRegistry(Ref.SHARED_ID);

    private final String domain, id;
    protected FlowingFluid source;
    protected FlowingFluid flowing;
    protected Block.Properties blockProperties;
    protected FluidProperties attributes;
    protected FluidData fluidData;
    protected LiquidBlock fluidBlock;
    protected Item containerItem = Items.AIR;

    public AntimatterFluid(String domain, String id, FluidProperties.Builder builder, Block.Properties blockProperties) {
        this.domain = domain;
        this.id = id;
        this.blockProperties = blockProperties;
        this.attributes = builder.build(new ResourceLocation(Ref.SHARED_ID, id));
    }

    public AntimatterFluid(String domain, String id) {
        this(domain, id, getDefaultAttributesBuilder(), getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, FluidProperties.Builder builder) {
        this(domain, id, builder, getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, ResourceLocation stillLoc, ResourceLocation flowLoc) {
        this(domain, id, FluidProperties.create().still(stillLoc).flowing(flowLoc), getDefaultBlockProperties());
    }

    public AntimatterFluid(String domain, String id, Block.Properties properties) {
        this(domain, id, getDefaultAttributesBuilder(), properties);
    }

    @Override
    public void onRegistryBuild(RegistryType registry) {
        if (registry == RegistryType.ITEMS) {
            AntimatterAPI.register(Item.class, getId() + "_bucket", getDomain(), containerItem = new BucketItem(this.getFluid(), new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET).tab(CreativeModeTab.TAB_MISC)));
        } else if (registry == RegistryType.BLOCKS) {
            FluidData data = FLUID_TYPES.register(attributes);
            this.source = FluidUtils.createSourceFluid(data);
            this.flowing = FluidUtils.createFlowingFluid(data);
            data.setBucket(() -> this.containerItem);
            this.fluidBlock = new BotariumLiquidBlock(data, blockProperties);
            AntimatterAPI.register(Block.class, "block_fluid_".concat(getId()), getDomain(), fluidBlock);
        } else if (registry == RegistryType.FLUIDS) {
            AntimatterAPI.register(Fluid.class, getId(), getDomain(), source);
            AntimatterAPI.register(FlowingFluid.class, "flowing_".concat(getId()), getDomain(), flowing);
        }
    }

    public AntimatterFluid source(BotariumSourceFluid source) {
        this.source = source;
        return this;
    }

    public AntimatterFluid flowing(BotariumFlowingFluid flowing) {
        this.flowing = flowing;
        return this;
    }

    public AntimatterFluid flowingBlock(LiquidBlock fluidBlock) {
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

    public Block.Properties getBlockProperties() {
        return blockProperties;
    }

    public FluidProperties getAttributes() {
        return attributes;
    }

    public FlowingFluid getFluid() {
        return source;
    }

    public FlowingFluid getFlowingFluid() {
        return flowing;
    }

    public LiquidBlock getFluidBlock() {
        return fluidBlock;
    }

    public Item getContainerItem() {
        return containerItem;
    }

    protected static Block.Properties getDefaultBlockProperties() {
        return Block.Properties.of(Material.WATER).strength(100.0F).noDrops();
    }

    protected static FluidProperties.Builder getDefaultAttributesBuilder() {
        return getDefaultAttributesBuilder(false);
    }

    protected static FluidProperties.Builder getDefaultAttributesBuilder(boolean hot) {
        if (hot) {
            return FluidProperties.create().still(LIQUID_HOT_STILL_TEXTURE).flowing(LIQUID_HOT_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).sounds("bucket_fill", SoundEvents.BUCKET_FILL_LAVA).sounds("bucket_empty", SoundEvents.BUCKET_EMPTY_LAVA);
        }
        return FluidProperties.create().still(LIQUID_STILL_TEXTURE).flowing(LIQUID_FLOW_TEXTURE).overlay(OVERLAY_TEXTURE).sounds("bucket_fill", SoundEvents.BUCKET_FILL).sounds("bucket_empty", SoundEvents.BUCKET_EMPTY);
    }
}
