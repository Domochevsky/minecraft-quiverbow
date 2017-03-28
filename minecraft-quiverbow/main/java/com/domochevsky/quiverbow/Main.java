package com.domochevsky.quiverbow;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.RecipeSorter;

import com.domochevsky.quiverbow.ArmsAssistant.Entity_AA;
import com.domochevsky.quiverbow.ammo.*;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.models.*;
import com.domochevsky.quiverbow.net.PacketHandler;
import com.domochevsky.quiverbow.projectiles.*;
import com.domochevsky.quiverbow.recipes.Recipe_Ammo;
import com.domochevsky.quiverbow.recipes.Recipe_ERA;
import com.domochevsky.quiverbow.recipes.Recipe_Weapon;
import com.domochevsky.quiverbow.weapons.*;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid="quiverchevsky", name="QuiverBow", version="b102")
public class Main
{
	@Instance("quiverchevsky")
	public static Main instance;
	
	@SidedProxy(clientSide="com.domochevsky.quiverbow.ClientProxy", serverSide="com.domochevsky.quiverbow.CommonProxy")
	public static CommonProxy proxy;
	
	protected Configuration config;										// Accessible from other files this way
	
	// TODO: Overhaul all of this to use arraylist.
	private static ArrayList<_WeaponBase> weapons = new ArrayList<_WeaponBase>();	// Holder array for all (fully set up) possible weapons
	private static ArrayList<_AmmoBase> ammo = new ArrayList<_AmmoBase>();			// Same with ammo, since they got recipes as well
	//private static String[] weaponType = new String[60];		// For Battle Gear 2
	
	@SideOnly(Side.CLIENT)
	private static ArrayList<ModelBase> models;	// Client side only
	
	public static Block fenLight = null;
	
	private static int projectileCount = 1;	// A running number, to register projectiles by
	
	// Config
	public static boolean breakGlass;				// If this is false then we're not allowed to break blocks with projectiles (Don't care about TNT)
	public static boolean useModels;				// If this is false then we're reverting back to held icons
	public static boolean noCreative;				// If this is true then disabled weapons won't show up in the creative menu either
	public static boolean allowTurret;				// If this is false then the Arms Assistant will not be available
	public static boolean allowTurretPlayerAttacks;	// If this is false then the AA is not allowed to attack players (ignores them)
	public static boolean restrictTurretRange;		// If this is false then we're not capping the targeting range at 32 blocks
	public static boolean sendBlockBreak;			// If this is false then Helper.tryBlockBreak() won't send a BlockBreak event. Used by protection plugins.
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		this.config = new Configuration(event.getSuggestedConfigurationFile());	// Starting config
		
		this.config.load();	// And loading it up
		
		breakGlass = this.config.get("generic", "Can we break glass and other fragile things with our projectiles? (default true)", true).getBoolean();
		sendBlockBreak = this.config.get("generic", "Do we send a BlockBreak event when breaking things with our projectiles? (default true)", true).getBoolean();
		useModels = this.config.get("generic", "Are we using models or icons for held weapons? (default true for models. False for icons)", true).getBoolean();
		noCreative = this.config.get("generic", "Are we removing disabled weapons from the creative menu too? (default false. On there, but uncraftable)", false).getBoolean();
		
		allowTurret = this.config.get("Arms Assistant", "Am I enabled? (default true)", true).getBoolean();
		restrictTurretRange = this.config.get("Arms Assistant", "Is my firing range limited to a maximum of 32 blocks? (default true. Set false for 'Shoot as far as your weapon can handle'.)", true).getBoolean();
		
		// Item registry here
		this.registerAmmo();
		this.registerWeapons(event.getSide().isClient());
		this.registerProjectiles();
		this.registerBlocks();
		
		addAllProps(event, this.config);	// All items are registered now. Making recipes and recording props
		
		this.config.save();				// Done with config, saving it
		
		PacketHandler.initPackets();	// Used for sending particle packets, so I can do my thing purely on the server side
		
		// Registering the Arms Assistant
		EntityRegistry.registerModEntity(Entity_AA.class, "quiverchevsky_turret", 0, this, 80, 1, true);
		//EntityRegistry.registerModEntity(Entity_BB.class, "quiverchevsky_flyingBB", 1, this, 80, 1, true);
		
		proxy.registerTurretRenderer();
		
		// Do I have to register a crafting listener of sorts? To what end?
		RecipeSorter.register("quiverchevsky:recipehandler", Recipe_ERA.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_2", Recipe_Weapon.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
		RecipeSorter.register("quiverchevsky:recipehandler_3", Recipe_Ammo.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		
		Listener listener = new Listener();
		
		FMLCommonHandler.instance().bus().register(listener);
		MinecraftForge.EVENT_BUS.register(listener);
		
		if (event.getSide().isServer()) { return; }	// Client-only from this point.
		
		ListenerClient listenerClient = new ListenerClient();
		
		FMLCommonHandler.instance().bus().register(listenerClient);
		MinecraftForge.EVENT_BUS.register(listenerClient);
	}
	
	
	/*@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Everything should be registered by this point. Letting ForgeUpdater know what my version is (if it's installed)
		//FMLInterModComms.sendMessage("forgeupdater", "updaterInfo", "{id='quiverbow', minType='2', formats=[QuiverBow_$mc_$v.zip, QuiverBow_1.7.10_$v.zip]}");
		// id, as it shows up on curse.com, in the URL
		// minType: 0 = alpha, 1 = beta, 2 = release
		// Format is for the file and how it's aranged, eg QuiverBow_1.7.10_b100.zip, $mc and $v are wildcards

		if (Loader.isModLoaded("battlegear2"))
		{
			System.out.println("[QUIVERBOW] Making myself known to Battle Gear 2.");
			int counter = 0;

			while (counter < weapons.length && weapons[counter] != null)
			{
				registerWithBattleGear2(weapons[counter], weaponType[counter]);	// Mod intercompatibility

				counter += 1;
			}
		}
	}*/
	
	
	void registerAmmo()		// Items with which weapons can be reloaded
	{
		this.addAmmo(new ArrowBundle(), "ArrowBundle");
		this.addAmmo(new RocketBundle(), "RocketBundle");
		
		this.addAmmo(new GatlingAmmo(), "SugarMagazine");
		this.addAmmo(new Part_GatlingBody(), "Part_SEBody");
		this.addAmmo(new Part_GatlingBarrel(), "Part_SEBarrel");
		
		this.addAmmo(new LargeRocket(), "LargeRocket");
		this.addAmmo(new ColdIronClip(), "ColdIronClip");
		this.addAmmo(new BoxOfFlintDust(),"BoxOfFlintDust");
		this.addAmmo(new SeedJar(), "SeedJar");
		
		this.addAmmo(new ObsidianMagazine(), "ObsidianMagazine");
		this.addAmmo(new GoldMagazine(), "GoldMagazine");
		this.addAmmo(new NeedleMagazine(), "ThornMagazine");
		this.addAmmo(new LapisMagazine(), "LapisMagazine");
		this.addAmmo(new RedstoneMagazine(), "RedstoneMagazine");
		
		this.addAmmo(new LargeNetherrackMagazine(), "LargeNetherrackMagazine");
		this.addAmmo(new LargeRedstoneMagazine(), "LargeRedstoneMagazine");
		
		this.addAmmo(new PackedUpAA(), "TurretSpawner");
		//this.addAmmo(new PackedUpBB(), "FlyingAASpawner");
		
		this.addAmmo(new EnderQuartzClip(), "EnderQuartzMagazine");
	}
	
	
	void registerWeapons(boolean isClient)	// The weapons themselves
	{
		this.addWeapon(new Crossbow_Compact(), new Crossbow_Model(), "Crossbow", isClient, "dual");
		this.addWeapon(new Crossbow_Double(), new CrossbowDouble_Model(), "CrossbowDouble", isClient, "mainhand");
		this.addWeapon(new Crossbow_Blaze(), new Crossbow_Model(), "CrossbowBlaze", isClient, "mainhand");
		this.addWeapon(new Crossbow_Auto(), new CrossbowAuto_Model(), "CrossbowAuto", isClient, "mainhand");
		this.addWeapon(new Crossbow_AutoImp(), new CrossbowAutoImp_Model(), "CrossbowAutoImp", isClient, "mainhand");
		
		this.addWeapon(new CoinTosser(), new CoinTosser_Model(), "CoinTosser", isClient, "mainhand");
		this.addWeapon(new CoinTosser_Mod(), new CoinTosser_Mod_Model(), "CoinTosser_Mod", isClient, "mainhand");
		
		this.addWeapon(new DragonBox(), new DragonBox_Model(), "DragonBox", isClient, "mainhand");
		this.addWeapon(new DragonBox_Quad(), new QuadBox_Model(), "DragonBox_Quad", isClient, "mainhand");
		
		this.addWeapon(new LapisCoil(), new LapisCoil_Model(), "LapisCoil", isClient, "mainhand");
		this.addWeapon(new ThornSpitter(), new ThornSpitter_Model(), "ThornSpitter", isClient, "dual");
		this.addWeapon(new ProximityNeedler(), new PTT_Model(), "ProxyNeedler", isClient, "mainhand");
		this.addWeapon(new SugarEngine(), new SugarEngine_Model(), "SugarEngine", isClient, "mainhand");
		
		this.addWeapon(new RPG(), new RPG_Model(), "RPG", isClient, "mainhand");
		this.addWeapon(new RPG_Imp(), new RPG_Model(), "RPG_Imp", isClient, "mainhand");
		
		this.addWeapon(new Mortar_Arrow(), new Mortar_Model(), "MortarArrow", isClient, "mainhand");
		this.addWeapon(new Mortar_Dragon(), new Mortar_Model(), "MortarRocket", isClient, "mainhand");
		
		this.addWeapon(new Seedling(), new Seedling_Model(), "Seedling", isClient, "dual");
		this.addWeapon(new Potatosser(), new Potatosser_Model(), "Potatosser", isClient, "mainhand");
		this.addWeapon(new SnowCannon(), new SnowCannon_Model(), "SnowCannon", isClient, "dual");
		
		this.addWeapon(new QuiverBow(), null, "QuiverBow", isClient, "mainhand");
		
		this.addWeapon(new EnderBow(), null, "EnderBow", isClient, "mainhand");
		this.addWeapon(new EnderRifle(), new EnderRifle_Model(), "EnderRifle", isClient, "mainhand");
		this.addWeapon(new FrostLancer(), new FrostLancer_Model(), "FrostLancer", isClient, "mainhand");
		
		this.addWeapon(new OSP(), new OSP_Model(), "OSP", isClient, "dual");
		this.addWeapon(new OSR(), new OSR_Model(), "OSR", isClient, "mainhand");
		this.addWeapon(new OWR(), new OWR_Model(), "OWR", isClient, "mainhand");
		
		this.addWeapon(new FenFire(), new FenFire_Model(), "FenFire", isClient, "dual");
		this.addWeapon(new FlintDuster(), new FlintDuster_Model(), "FlintDuster", isClient, "mainhand");
		
		this.addWeapon(new LightningRed(), new LightningRed_Model(), "LightningRed", isClient, "mainhand");
		this.addWeapon(new Sunray(), new Sunray_Model(), "Sunray", isClient, "mainhand");
		
		this.addWeapon(new PowderKnuckle(), null, "PowderKnuckle", isClient, "dual");
		this.addWeapon(new PowderKnuckle_Mod(), null, "PowderKnuckle_Mod", isClient, "dual");
		
		this.addWeapon(new NetherBellows(), new NetherBellows_Model(), "NetherBellows", isClient, "mainhand");
		this.addWeapon(new RedSprayer(), new RedSprayer_Model(), "Redsprayer", isClient, "mainhand");
		
		this.addWeapon(new SoulCairn(), new SoulCairn_Model(), "SoulCairn", isClient, "dual");
		this.addWeapon(new AquaAccelerator(), new AquaAcc_Model(), "AquaAccelerator", isClient, "dual");
		this.addWeapon(new SilkenSpinner(), new AquaAcc_Model(), "SilkenSpinner", isClient, "dual");
		
		this.addWeapon(new SeedSweeper(), new SeedSweeper_Model(), "SeedSweeper", isClient, "mainhand");
		this.addWeapon(new MediGun(), new MediGun_Model(), "RayOfHope", isClient, "mainhand");
		
		this.addWeapon(new ERA(), new ERA_Model(), "ERA", isClient, "mainhand");
		
		this.addWeapon(new AA_Targeter(), new AATH_Model(), "AATargeter", isClient, "dual");
		
		this.addWeapon(new Endernymous(), new EnderNymous_Model(), "EnderNymous", isClient, "dual");
	}
	
	
	void registerProjectiles()	// Entities that get shot out of weapons as projectiles
	{
		this.addProjectile(RegularArrow.class, true, "Arrow");
		this.addProjectile(BlazeShot.class, true, "Blaze");
		this.addProjectile(CoinShot.class, true, "Coin");
		this.addProjectile(SmallRocket.class, true, "RocketSmall");
		
		this.addProjectile(LapisShot.class, true, "Lapis");
		this.addProjectile(Thorn.class, true, "Thorn");
		this.addProjectile(ProxyThorn.class, true, "ProxyThorn");
		this.addProjectile(SugarRod.class, true, "Sugar");
		
		this.addProjectile(BigRocket.class, true, "RocketBig");
		
		this.addProjectile(Sabot_Arrow.class, true, "SabotArrow");
		this.addProjectile(Sabot_Rocket.class, true, "SabotRocket");
		
		this.addProjectile(Seed.class, true, "Seed");
		this.addProjectile(PotatoShot.class, true, "Potato");
		this.addProjectile(SnowShot.class, true, "Snow");
		
		this.addProjectile(ScopedPredictive.class, true, "Predictive");
		this.addProjectile(EnderShot.class, true, "Ender");
		this.addProjectile(ColdIron.class, true, "ColdIron");
		
		this.addProjectile(OSP_Shot.class, true, "OSP");
		this.addProjectile(OSR_Shot.class, true, "OSR");
		this.addProjectile(OWR_Shot.class, true, "OWR");
		
		this.addProjectile(FenGoop.class, true, "FenLight");
		this.addProjectile(FlintDust.class, true, "FlintDust");
		
		this.addProjectile(RedLight.class, true, "RedLight");
		this.addProjectile(SunLight.class, true, "SunLight");
		
		this.addProjectile(NetherFire.class, true, "NetherFire");
		this.addProjectile(RedSpray.class, true, "RedSpray");
		
		this.addProjectile(SoulShot.class, true, "Soul");
		
		this.addProjectile(WaterShot.class, true, "Water");
		this.addProjectile(WebShot.class, true, "Web");
		
		this.addProjectile(HealthBeam.class, true, "Health");
		
		this.addProjectile(EnderAccelerator.class, true, "ERA");
		this.addProjectile(EnderAno.class, true, "Ano");
	}
	
	
	private void registerBlocks()		// Blocks we can place
	{
		fenLight = new FenLight(Material.glass);
		GameRegistry.registerBlock(fenLight, "quiverchevsky_FenLight");
	}
	
	
	private void addAmmo(_AmmoBase ammoBase, String name)
	{
		this.ammo.add(ammoBase);
		
		GameRegistry.registerItem(ammoBase, "ammochevsky_" + name); // And register it
	}
	
	
	// Helper function for taking care of weapon registration
	private void addWeapon(_WeaponBase weapon, ModelBase model, String weaponName, boolean isClient)
	{
		if (this.weapons == null) { this.weapons = new ArrayList<_WeaponBase>(); }
		
		this.weapons.add(weapon);
		
		GameRegistry.registerItem(weapon, "weaponchevsky_" + weaponName);	// And register it
		
		weapon.setUniqueName(weaponName);
		
		if (isClient && useModels && model != null)	// Do we care about models? And if we do, do we got a custom weapon model? :O
		{
			if (this.models == null) { this.models = new ArrayList<ModelBase>(); }	// Init
			
			this.models.add(model);
			models[counter] = model;								// Keeping track of it
			proxy.registerWeaponRenderer(weapon, (byte) counter);	// And registering its renderer
		}
	}
	
	
	private void addProjectile(Class<? extends Entity> entityClass, boolean hasRenderer, String name)
	{
		EntityRegistry.registerModEntity(entityClass, "projectilechevsky_" + name, projectileCount, this, 80, 1, true);
		
		if (hasRenderer) { proxy.registerProjectileRenderer(entityClass); } // Entity-specific renderer
		
		projectileCount += 1;
	}
	
	
	// Adding props and recipes for all registered weapons now
	private static void addAllProps(FMLPreInitializationEvent event, Configuration config)
	{
		int counter = 0;
		
		while (counter < ammo.length && ammo[counter] != null)	// Ammo first
		{
			ammo[counter].addRecipes();
			counter += 1;
		}
		
		// Then parts
		
		counter = 0;
		
		while (counter < weapons.length && weapons[counter] != null)	// Weapons last
		{
			weapons[counter].addProps(event, config);
			weapons[counter].addRecipes();
			
			counter += 1;
		}
	}
	
	
	private static void registerWithBattleGear2(Item item, String wield)
	{
		//Where hand is a case-insensitive String ("both" -or- "dual" for one-handed items,  "right" -or- "mainhand" -or- "left" -or- "offhand" for two-handed on designated side)
		//Where itemStack is an ItemStack instance specific enough of the item
		FMLInterModComms.sendMessage("battlegear2", wield, new ItemStack(item));
	}


	public static _WeaponBase getRandomWeaponForQuiverMob()
	{
		// TODO
	}
	
	
	public static _WeaponBase getWeaponByUniqueName(String uniqueName)
	{
		// TODO
	}
}