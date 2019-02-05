package muramasa.gregtech.common.utils;

import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.items.MetaTool;
import muramasa.gregtech.api.util.ToolHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandTool extends CommandBase {

    @Override
    public String getName() {
        return "tool";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "tool <matID1> <matID2>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (sender instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) sender;
            ItemStack stack = player.getHeldItemMainhand();
            if (stack.getItem() instanceof MetaTool) {
                try {
                    int value1 = Integer.parseInt(args[0]);
                    int value2 = Integer.parseInt(args[1]);
                    ToolHelper.validateTag(stack);
//                    ToolStats.setDurability(stack, value);
                    ToolHelper.setEnergy(stack, value1);
                    ToolHelper.setMaxEnergy(stack, value2);
                } catch (NumberFormatException e) {
                    if (args.length == 2) {
                        ToolHelper.validateTag(stack);
                        ToolHelper.setup(stack, Materials.get(args[0]), Materials.get(args[1]));
                    }
                }
            }
        }
    }
}
