package muramasa.gtu;

import net.minecraftforge.common.config.Config;

@Config(modid = Ref.MODID)
public class ConfigurationHolder {
	
	//TODO: More description + eventually move reference to here
	
	@Config.Comment("Enable hardcore cables? Default: true")
	//@Config.RequiresWorldRestart
	@Config.RequiresMcRestart
	public static boolean hardcoreCables = true;
	
	@Config.Comment("Show all GT items? Default: true")
	public static boolean showAllItems = true;
	
	@Config.Comment("Enable all of GT's items? Default: true")
	@Config.RequiresMcRestart
	public static boolean enableAllModItem = true;
	
	@Config.Comment("Enable all registrars? Default: true")
	@Config.RequiresMcRestart
	public static boolean enableAllRegistrars = true;
	
	@Config.Comment("Turn on basic machine models? Default: false")
	public static boolean basicMachineModels = false;
	
	@Config.Comment("Allow mixed ores yields 2/3 of pure ore? Default: false")
	@Config.RequiresMcRestart
	public static boolean mixedOreYieldsTwoThirdsOfPureOre = false;
	
	@Config.Comment("Disable old chemical recipes? Default: false")
	@Config.RequiresMcRestart
	public static boolean disableOldChemicalRecipes = false;
	
	@Config.Comment("Enable item replacements? Default: true")
	@Config.RequiresMcRestart
	public static boolean enableItemReplacements = true;
	
	@Config.Comment("Disable vanilla ores from generating? Default: false")
	@Config.RequiresWorldRestart
	public static boolean disableVanillaOreGen = true;
	
	@Config.Comment("Disable vanilla stones from generating? Default: false")
	@Config.RequiresWorldRestart
	public static boolean disableVanillaStoneGen = false;

}
