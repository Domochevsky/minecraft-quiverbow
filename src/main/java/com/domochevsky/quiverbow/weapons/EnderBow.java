package com.domochevsky.quiverbow.weapons;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.ScopedPredictive;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnderBow extends _WeaponBase 	// So archaic... I may have to overhaul this at some point. There's nothing standard about this
{
	public EnderBow() { super(256); }

	private String nameInternal = "Ender Bow";
	
	//public String[] bowPullIconNameArray = new String[] {"pulling_0", "pulling_1", "pulling_2"};
	
	@SideOnly(Side.CLIENT)
    private IIcon pull_0;
	
	@SideOnly(Side.CLIENT)
    private IIcon pull_1;
	
	@SideOnly(Side.CLIENT)
    private IIcon pull_2;
	
	
	//@SideOnly(Side.CLIENT)
	//private IIcon[] iconArray;
	
	private int shotCounter = 0;
	
	private String playerName = "";	// Holds the name of the firing player, so only they can see it firing
	private int Ticks;
	private int ZoomMax;
	
	private int defaultFOV;
	
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
		 this.itemIcon = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderBow_idle");
	        
	        this.pull_0 = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderBow_pulling_0");
	        this.pull_1 = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderBow_pulling_1");
	        this.pull_2 = par1IconRegister.registerIcon("quiverchevsky:weapons/EnderBow_pulling_2");
    }
	
	
	@Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) 
	{
		if(player.getItemInUse() == null) { return this.itemIcon; }
         
         int Pulling = stack.getMaxItemUseDuration() - useRemaining;	// Displaying the bow drawing animation based on the use state
        
         if (Pulling >= 18) { return this.pull_2; }
         else if (Pulling > 13) { return this.pull_1; }
         else if (Pulling > 0) { return this.pull_0; }              
         
         return itemIcon;
    }
	
	
	@SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int state) // Inventory display
	{ 
		if (state == 0) { return this.pull_0; }
		else if (state == 1) { return this.pull_1; }
		else if (state == 2) { return this.pull_2; }
		
		return this.pull_2; // Fallback
	}	
	
	
	@Override									
	public IIcon getIconFromDamage(int meta)	// This is for inventory display. Comes in with metadata
    {		
		return this.itemIcon;
    }
	
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
	    super.addInformation(stack, player, list, par4);
	    
	    int ammo = this.getMaxDamage() - this.getDamage(stack);
	    
	    list.add(EnumChatFormatting.BLUE + "Durability: " + ammo + " / " + this.getMaxDamage());
	    
	    list.add(EnumChatFormatting.GREEN + "Zoom on use.");
	    list.add(EnumChatFormatting.GREEN + "Predictive arrow on use.");
	    
	    list.add("An ender-eye scope is attached.");
	    list.add("It's staring at you.");
    }
	
	
	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config) 
	{ 
		this.Enabled = config.get(this.nameInternal, "Am I enabled? (default true)", true).getBoolean(true);
		this.namePublic = config.get(this.nameInternal, "What's my name?", this.nameInternal).getString();
    	this.Ticks = config.get(this.nameInternal, "How often should I display the predictive projectile? (default every 5 ticks. That's 4 per second.)", 5).getInt();
    	this.ZoomMax = config.get(this.nameInternal, "How far can I zoom in? (default 30. Lower equals more zoom.)", 30).getInt();
    	
    	this.isMobUsable = config.get(this.nameInternal, "Can I be used by QuiverMobs? (default false. They don't know how to pull a string anymore.)", false).getBoolean();
	}
    
	
	@Override
    public void addRecipes() 
	{ 
		if (Enabled)
        {
            // One ender bow, all ready
            GameRegistry.addRecipe(new ItemStack(this), "zxy", "xay", "zxy",
            		'x', Items.stick, 
            		'y', Items.string, 
            		'z', Items.ender_eye, 
            		'a', Items.iron_ingot 
            );
        }
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu
	}
	
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean par5) 
	{		
		if (stack.stackTagCompound == null) 
		{
			stack.stackTagCompound = new NBTTagCompound();
			
			stack.stackTagCompound.setBoolean("isZoomed", false);
			stack.stackTagCompound.setInteger("defaultFOV", 0);	// FOV is now using full numbers. We're recording the current default FOV here
		}

		// let's check zoom here
		if (entity instanceof EntityPlayer) 	// Step 1, is this a player?
        {
			EntityPlayer entityplayer = (EntityPlayer)entity;

			if (entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem() == stack)	// Step 2, are they holding the bow?
			{
				if (entityplayer.isUsingItem()) // step 3, are they using the bow?
				{
					this.setCurrentZoom(stack, true); // We need to zoom in!
					
					if (world.isRemote)
					{ 
						//System.out.println("[ENDER BOW] Current FOV: " + Minecraft.getMinecraft().gameSettings.fovSetting);
				    	
				    	// We're gonna zoom in each tick. Is it bigger than the max zoom? Then keep zooming.
				    	if (Minecraft.getMinecraft().gameSettings.fovSetting > ZoomMax) 
				    	{
				    		// FOV is now using full numbers. 70 is 70, 120 is 120. Nice of them.
				    		Minecraft.getMinecraft().gameSettings.fovSetting -= 2;	// 2 less, until we've reached zoomMax
				    	}
					}	
					// else, server side. Has no deal with game settings
					
				}
				else // Not using this item currently
				{
					if (this.isCurrentlyZoomed(stack)) 			// Are we currently zoomed in?
					{
						this.setCurrentZoom(stack, false);
						if (world.isRemote) { this.restoreFOV(stack); }	// Begone with that then
					}
					// else, not zoomed in currently
				}
			}
			else // Not holding the bow
			{
				if (this.isCurrentlyZoomed(stack)) 				// Are we currently zoomed in?
				{
					this.setCurrentZoom(stack, false);
					if (world.isRemote) { this.restoreFOV(stack); } 	// Begone with that then
						
				}
			}
			
			// step 0, recording the current zoom level, if we're not zoomed in. Need to do this AFTER we reset
			if (!this.isCurrentlyZoomed(stack))	// Not zoomed in currently
			{				
				if (world.isRemote) { this.recordCurrentFOV(stack); } // Client side only
			}
		}
		// else, not a player holding this thing
	}
	
	
	// Records the current FOV setting
	void recordCurrentFOV(ItemStack stack) { this.defaultFOV = (int) Minecraft.getMinecraft().gameSettings.fovSetting; } // Client-side only
	
	// Sets the FOV setting back to the recorded default
	void restoreFOV(ItemStack stack) { Minecraft.getMinecraft().gameSettings.fovSetting = this.defaultFOV; }	// Client-side only
	
	
	void setCurrentZoom(ItemStack stack, boolean zoom)
	{
		if (stack == null) { return; }	// Not a  valid item
		if (!stack.hasTagCompound()) { return; } // No tag
		
		stack.stackTagCompound.setBoolean("isZoomed", zoom);
	}
	
	boolean isCurrentlyZoomed(ItemStack stack)
	{
		if (stack == null) { return false; }	// Not a  valid item
		if (!stack.hasTagCompound()) { return false; } // No tag
		
		return stack.stackTagCompound.getBoolean("isZoomed");
	}
    
    
    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int par4) 
    {
        int chargeTime = this.getMaxItemUseDuration(stack) - par4;

        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, chargeTime);
        MinecraftForge.EVENT_BUS.post(event);
        
        if (event.isCanceled()) { return; }	// Not having it
        
        chargeTime = event.charge;

        // Either creative mode or infinity enchantment is higher than 0. Not using arrows
        boolean freeShot = player.capabilities.isCreativeMode;

        if (freeShot || player.inventory.hasItemStack(new ItemStack(Items.arrow)) ) 
        {
            float f = (float) chargeTime / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D) { return; }
            if (f > 1.0F) { f = 1.0F; }

            EntityArrow entityarrow = new EntityArrow(world, player, f * 2.0F);

            if (f == 1.0F) { entityarrow.setIsCritical(true); }

            stack.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            if (freeShot) { entityarrow.canBePickedUp = 2; }
            else { player.inventory.consumeInventoryItem(Items.arrow); }

            if (!world.isRemote) { world.spawnEntityInWorld(entityarrow); }	// pew.
        }
    }

   
    @Override
    public int getMaxItemUseDuration(ItemStack par1ItemStack) { return 72000; }

   
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) { return EnumAction.bow; }
    
    
    @Override
    public void onUsingTick(ItemStack stack, EntityPlayer player, int count)
    {
    	float yaw = player.rotationYaw;
    	float pitch = player.rotationPitch;
    	if (player.worldObj.isRemote) 
    	{
    		// The projectile is only allowed to shoot every X ticks, so we're gonna make this happen here
    		this.shotCounter += 1;
    		if (this.shotCounter >= Ticks) 
    		{
    			// Only allowing this to happen when the right player uses this
    			if (player.getDisplayName() == this.playerName)
    			{
	    			ScopedPredictive entityarrow = new ScopedPredictive(player.worldObj, player, 2.0F * 1.5F);
	    			player.worldObj.spawnEntityInWorld(entityarrow);
    			}
    			this.shotCounter = 0;
    		}
        }
    }
    
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        ArrowNockEvent event = new ArrowNockEvent(player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) { return event.result; }

        if (player.capabilities.isCreativeMode || player.inventory.hasItemStack( new ItemStack(Items.arrow) ) )
        {
        	player.setItemInUse(stack, this.getMaxItemUseDuration(stack)); 
        	this.playerName = player.getDisplayName();	// Recording the player name here, so only they can see the projectile
        }
        
        return stack;
    }
    
    
    @Override
    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player)
    {
    	stack.stackTagCompound.setBoolean("zoom", false);	// Dropped the bow
    	stack.stackTagCompound.setFloat("currentzoom", 0);
    	
    	if (player.worldObj.isRemote) 
    	{											
    		Minecraft.getMinecraft().gameSettings.fovSetting = stack.stackTagCompound.getFloat("zoomlevel");	// Begone with the zoom
        }
    	
        return true;
    }
    
    
    @Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list) 	// getSubItems
	{
		list.add(new ItemStack(item, 1, 0));
	}
}
