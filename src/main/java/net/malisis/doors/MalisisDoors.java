package net.malisis.doors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;

import net.malisis.doors.bigdoors.Door3x3;
import net.malisis.doors.block.BlockMixer;
import net.malisis.doors.block.CustomDoor;
import net.malisis.doors.block.Door;
import net.malisis.doors.block.DoorFactory;
import net.malisis.doors.block.FenceGate;
import net.malisis.doors.block.MixedBlock;
import net.malisis.doors.block.PlayerSensor;
import net.malisis.doors.block.Swapper;
import net.malisis.doors.block.TrapDoor;
import net.malisis.doors.block.VanishingBlock;
import net.malisis.doors.client.gui.MalisisGui;
import net.malisis.doors.inventory.MalisisTab;
import net.malisis.doors.item.CustomDoorItem;
import net.malisis.doors.item.DoorItem;
import net.malisis.doors.item.VanishingCopierItem;
import net.malisis.doors.network.MalisisNetwork;
import net.malisis.doors.registry.AutoLoad;
import net.malisis.doors.registry.Registries;
import net.malisis.doors.renderer.font.MalisisFont;
import net.malisis.doors.util.Utils;
import net.malisis.doors.util.modmessage.ModMessageManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(
modid = MalisisDoors.modid,
name = MalisisDoors.modname,
version = MalisisDoors.version,
acceptedMinecraftVersions = "[1.12, 1.13)")
public class MalisisDoors implements IMalisisMod {
	public static final String modid = "malisisdoors";
	public static final String modname = "Malisis' Doors";
	public static final String version = "${version}";
	
	public static MalisisDoors instance;
	public static MalisisNetwork network;
	
	public static MalisisTab tab = new MalisisTab(MalisisDoors.modid, () -> Items.jailDoorItem);

	public static MalisisFont digitalFont;
	
	/** AsmDataTable */
	public static ASMDataTable asmDataTable;
	
	/** List of {@link IMalisisMod} registered. */
	private HashMap<String, IMalisisMod> registeredMods = new HashMap<>();
	
	
	/** Logger for the mod. */
	public static Logger log = LogManager.getLogger(modid);
	
	/** Whether the mod is currently running in obfuscated environment or not. */
	public static boolean isObfEnv = false;
	
	public MalisisDoors() {
		instance = this;
		network = new MalisisNetwork(this);
		isObfEnv = !(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
		registerMod(this);
	}
	
	@Override
	public String getModId() {
		return modid;
	}
	
	@Override
	public String getName() {
		return modname;
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	
	/**
	 * Registers a {@link IMalisisMod} mod.
	 *
	 * @param mod the mod to register
	 */
	public static void registerMod(IMalisisMod mod) {
		instance.registeredMods.put(mod.getModId(), mod);
	}
	
	/**
	 * Gets the a registered {@link IMalisisMod} by his id.
	 *
	 * @param id the id of the mod
	 * @return the mod registered, null if no mod with the specified id is found
	 */
	public static IMalisisMod getMod(String id) {
		return instance.registeredMods.get(id);
	}
	
	/**
	 * Gets a list of registered {@link IMalisisMod} ids.
	 *
	 * @return set of ids.
	 */
	public static Set<String> listModId() {
		return instance.registeredMods.keySet();
	}
	
	/**
	 * Automatically loads (and eventually instantiate) classes with the {@link AutoLoad} annotation.
	 *
	 * @param asmDataTable the asm data table
	 */
	private void autoLoadClasses() {
		Set<ASMData> classes = ImmutableSortedSet.copyOf(	Ordering.natural().onResultOf(ASMData::getClassName),
				asmDataTable.getAll(AutoLoad.class.getName()));
		
		Set<ASMData> clientClasses = asmDataTable.getAll(SideOnly.class.getName());
		
		for (ASMData data : classes) {
			try {
				//don't load client classes on server.
				if (!isClient() && isClientClass(data.getClassName(), clientClasses))
					continue;
				
				Class<?> clazz = Class.forName(data.getClassName());
				AutoLoad anno = clazz.getAnnotation(AutoLoad.class);
				if (anno.value())
					clazz.newInstance();
			}
			catch (Exception e) {
				MalisisDoors.log.error("Could not autoload {}.", data.getClassName(), e);
			}
		}
	}
	
	
	/**
	 * Checks if is the specified class has a @SideOnly(Side.CLIENT) annotation.
	 *
	 * @param className the class name
	 * @param clientClasses the client classes
	 * @return true, if is client class
	 */
	private boolean isClientClass(String className, Set<ASMData> clientClasses) {
		for (ASMData data : clientClasses) {
			if (data.getClassName().equals(className) && data.getObjectName().equals(data.getClassName())
					&& ((ModAnnotation.EnumHolder) data.getAnnotationInfo().get("value")).getValue().equals("CLIENT"))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks the mod is loading on a physical client..
	 *
	 * @return true, if is client
	 */
	public static boolean isClient() {
		return FMLCommonHandler.instance().getSide().isClient();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		asmDataTable = event.getAsmData();
		autoLoadClasses();
		
		//register this to the EVENT_BUS for onGuiClose()
		MinecraftForge.EVENT_BUS.register(this);
		
		Registries.processFMLStateEvent(event);
		
		ModMessageManager.register(this, DoorDescriptor.class);
		
		Registers.init();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		if (isClient()) {
			ClientCommandHandler.instance.registerCommand(new MalisisCommand());
		}
		
		Registries.processFMLStateEvent(event);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Registries.processFMLStateEvent(event);
		
		if (MalisisDoors.isClient()) {
			
			ResourceLocation rl = new ResourceLocation(MalisisDoors.modid + ":fonts/digital-7 (mono).ttf");
			MalisisDoors.digitalFont = new MalisisFont(rl);
		}
	}
	
	@EventHandler
	public void postInit(FMLLoadCompleteEvent event) {
		Registries.processFMLStateEvent(event);
	}
	
	
	/**
	 * Gui close event.<br>
	 * Used to cancel the closing of the {@link MalisisGui} when opened from command line.
	 *
	 * @param event the event
	 */
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onGuiClose(GuiOpenEvent event) {
		if (!MalisisGui.cancelClose || event.getGui() != null)
			return;
		
		MalisisGui.cancelClose = false;
		event.setCanceled(true);
	}
	
	
	public static class Blocks {
		
		//MalisisDoors doors
		public static Door woodSlidingDoor;
		public static Door ironSlidingDoor;
		public static Door jailDoor;
		public static Door laboratoryDoor;
		public static Door factoryDoor;
		public static Door shojiDoor;
		public static Door curtains;
		
		//MalisisDoors trapdoors
		public static TrapDoor trapdoorAcacia;
		public static TrapDoor trapdoorBirch;
		public static TrapDoor trapdoorDarkOak;
		public static TrapDoor trapdoorJungle;
		public static TrapDoor trapdoorSpruce;
		
		//Special doors
		public static CustomDoor customDoor;
		
		//3x3 doors
		public static Door3x3 bigDoorOak;
		public static Door3x3 bigDoorBirch;
		public static Door3x3 bigDoorSpruce;
		public static Door3x3 bigDoorJungle;
		public static Door3x3 bigDoorAcacia;
		public static Door3x3 bigDoorDarkOak;
		public static Door3x3 bigDoorIron;
		public static Door3x3 bigDoorRusty;
		
		public static TrapDoor slidingTrapDoor;
		public static FenceGate camoFenceGate;
		
		//MalisisDoors blocks
		public static DoorFactory doorFactory;
		
		public static BlockMixer blockMixer;
		public static MixedBlock mixedBlock;
		public static VanishingBlock vanishingBlock;
		public static PlayerSensor playerSensor;
		public static Swapper swapper;
	}
	
	public static class Items {
		
		//MalisisDoors door items
		public static DoorItem woodSlidingDoorItem;
		public static DoorItem ironSlidingDoorItem;
		public static DoorItem jailDoorItem;
		public static DoorItem laboratoryDoorItem;
		public static DoorItem factoryDoorItem;
		public static DoorItem shojiDoorItem;
		public static DoorItem curtainsItem;
		
		//MalisisDoors trapdoors items
		/*public static Item trapdoorAcaciaItem;
		public static Item trapdoorBirchItem;
		public static Item trapdoorDarkOakItem;
		public static Item trapdoorJungleItem;
		public static Item trapdoorSpruceItem;*/
		public static Item slidingTrapDoorItem;
		
		//Special door items
		public static CustomDoorItem customDoorItem;
		
		public static VanishingCopierItem vanishingCopierItem;
	}
	

	public static class Sounds
	{
		public static SoundEvent portal;
	}
	
	/**
	 * Displays a text in the chat.
	 *
	 * @param text the text
	 */
	public static void message(Object text) {
		message(text, (Object) null);
	}
	
	/**
	 * Displays a text in the chat.<br>
	 * Client side calls will display italic and grey text.<br>
	 * Server side calls will display white text. The text will be sent to all clients connected.
	 *
	 * @param text the text
	 * @param data the data
	 */
	public static void message(Object text, Object... data) {
		String txt = text != null ? text.toString() : "null";
		if (text instanceof Object[])
			txt = Arrays.deepToString((Object[]) text);
		
		//on server simply log messages
		if (!isClient()) {
			log.info(String.format(txt, data));
			return;
		}
		
		TextComponentString msg = new TextComponentString(I18n.format(txt, data));
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();
			
			if (server != null) {
				server.getPlayerList().sendMessage(msg);
			}
		}
		else {
			EntityPlayer player = Utils.getClientPlayer();
			if (player == null)
				return;
			Style cs = new Style();
			cs.setItalic(true);
			cs.setColor(TextFormatting.GRAY);
			msg.setStyle(cs);
			
			player.sendMessage(msg);
		}
	}
	
}
