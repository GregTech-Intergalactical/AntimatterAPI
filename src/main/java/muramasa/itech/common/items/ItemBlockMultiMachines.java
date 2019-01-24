package muramasa.itech.common.items;

import net.minecraft.block.Block;

public class ItemBlockMultiMachines extends ItemBlockMachines {

    public ItemBlockMultiMachines(Block block) {
        super(block);
    }

//    public ItemBlockMultiMachines(Block block) {
//        super(block);
//        setHasSubtypes(true);
//    }
//
//    //TODO merge with ItemBlockMachines?
//
//    @Override
//    public String getItemStackDisplayName(ItemStack stack) {
//        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.TAG_MACHINE_STACK_DATA)) {
//            NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
//            Machine machine = MachineList.get(data.getString(Ref.KEY_MACHINE_STACK_TYPE));
//            if (machine != null) {
//                return I18n.format("machine." + machine.getName() + "." + Tier.get(data.getString(Ref.KEY_MACHINE_STACK_TIER)).getName() + ".name");
//            } else {
//                System.out.println("NULL");
//            }
//        }
//        return "MACHINE NAME ERROR";
//    }
//
//    @Override
//    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
//        if (stack.hasTagCompound() && stack.getTagCompound().hasKey(Ref.TAG_MACHINE_STACK_DATA)) {
//            NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
//            tooltip.add("BLOCK MULTI MACHINES");
//            tooltip.add(data.getString(Ref.KEY_MACHINE_STACK_TYPE));
//            tooltip.add(data.getString(Ref.KEY_MACHINE_STACK_TIER));
//        }
//    }
}
