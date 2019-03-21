package muramasa.gregtech.proxy;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.tools.MaterialTool;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class ServerProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        //NOOP
    }

    @Override
    public void init(FMLInitializationEvent e) {
        //NOOP
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        //NOOP
    }

    @Override
    public void serverStarting(FMLServerStartingEvent e) {
        e.registerServerCommand(new CommandBase() {
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
                    if (stack.getItem() instanceof MaterialTool) {
//                        try {
//                            MaterialTool tool = (MaterialTool) stack.getItem();
//                            int value1 = Integer.parseInt(args[0]);
//                            int value2 = Integer.parseInt(args[1]);
//                            tool.validateTag(stack);
////                    ToolStats.setDurability(stack, value);
//                            tool.setEnergy(stack, value1);
//                            tool.setMaxEnergy(stack, value2);
//                        } catch (NumberFormatException e) {
                            MaterialTool tool = (MaterialTool) stack.getItem();
                            if (args.length == 2) {
                                tool.validateTag(stack);
                                tool.getTag(stack).setString(Ref.KEY_TOOL_DATA_PRIMARY_MAT, args[0]);
                                tool.getTag(stack).setString(Ref.KEY_TOOL_DATA_SECONDARY_MAT, args[1]);
//                                tool.setup(stack, Materials.get(args[0]), Materials.get(args[1]));
                            }
//                        }
                    }
                }
            }
        });
    }
}
