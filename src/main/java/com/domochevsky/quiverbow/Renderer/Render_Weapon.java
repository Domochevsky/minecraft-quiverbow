package com.domochevsky.quiverbow.Renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.models.EnderNymous_Model;

public class Render_Weapon implements IItemRenderer
{	
	byte weaponID;
	
	//ModelBase model;
	ResourceLocation tex;
	
	boolean dontRender;

    public Render_Weapon(byte weapon) 
    {
    	this.weaponID = weapon;	// the weapon ID
    	
    	if (Main.models.get(weapon) == null) { this.dontRender = true; }	// Nothing to render
    }
    
    
    void setTexture(ItemStack stack)
    {
    	tex = new ResourceLocation("quiverchevsky:textures/items/models/" + Main.weapons.get(weaponID).getModelTexPath(stack) + ".png");
    }
    
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) 
	{
		if (type == ItemRenderType.INVENTORY) { return false; }	// There is no renderer for 3D inventory items, only for blocks
		
		return true;	// Changed from default false
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) { return false; }
	
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) 
	{
		if (this.dontRender) { return; }	// Not rendering, likely because we don't have a model ready
		
		float scale = 0.75F;
		
		// The actual renderer
		switch (type) 
		{
	        case EQUIPPED: 				// render in third person
	            GL11.glPushMatrix();
		            GL11.glScalef(scale, scale, scale);
		            
	            	this.setTexture(stack); // Grabbing the right texture for this frame first
	            	Minecraft.getMinecraft().renderEngine.bindTexture(tex);	// And binding it
		            
		            GL11.glRotatef(-180F, 1.0f, 0.0f, 0.0f);	// X axis
		            //GL11.glRotatef(0F, 0.0f, 1.0f, 0.0f);		// Y axis, rotates sideways
		            GL11.glRotatef(-40F, 0.0f, 0.0f, 1.0f);  	// Z axis, -40F default straight holding (-80 adjusted, so it doesn't clip through the floor)
		            
		            //GL11.glTranslatef(0.65F, 0.55F, 0.0F);   	    // translate model to fit in the hand of the player (angled)
		            GL11.glTranslatef(1.00F, -0.15F, 0.0F);   	// translate model to fit in the hand of the player, backup (straight holding)
		            
		            if (stack.getItemDamage() >= stack.getMaxDamage() && Main.models.get(weaponID) instanceof EnderNymous_Model)
		            {
		            	EnderNymous_Model nym = (EnderNymous_Model) Main.models.get(weaponID);
		            	nym.renderEmpty((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);	// Trying something out here
		            }
		            else
		            {
		            	Main.models.get(weaponID).render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		            }
		            
	            GL11.glPopMatrix();
	        break;
	
	        case EQUIPPED_FIRST_PERSON:	// Render first person holding
	            GL11.glPushMatrix();
	            	this.setTexture(stack); // Grabbing the right texture for this frame first
	            	Minecraft.getMinecraft().renderEngine.bindTexture(tex);	// And binding it
	
		            //GL11.glRotatef(0.0F + tempIncrement, 1.0f, 0.0f, 0.0f);	// X axis, rotates in circle from left to right
		            GL11.glRotatef(-175F, 0.0f, 1.0f, 0.0f);		// Y axis, rotates in circle from right to left, 160F default
		            GL11.glRotatef(133F, 0.0f, 0.0f, 1.0f);		// Z axis, rotates item up/down

		            GL11.glTranslatef(1.0F, 0.4F, -0.74F);	
		            // x -0.8 default (shifts item from back to front)
		            // y 0.9 default (shifts item from up to down)
		            // z -0.1 default (shifts the item from right to left)
		            
		            //tempIncrement += 0.02F;	// I need to know what axis these are, so let's rotate it
		            //if (tempIncrement >= 180F) { tempIncrement = -180F; } 				// Rolling around
		            //if (tempIncrement < -180F) { tempIncrement = 180F; } 
		            //if (tempIncrement >= 2F) { tempIncrement = -2F; } 	// For translatef
		            //System.out.println("[RENDER] tempIncrement is " + tempIncrement);
		           
		           // Main.models[weaponID].render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		            if (stack.getItemDamage() >= stack.getMaxDamage() && Main.models.get(weaponID) instanceof EnderNymous_Model)
		            {
		            	EnderNymous_Model nym = (EnderNymous_Model) Main.models.get(weaponID);
		            	nym.renderEmpty((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);	// Trying something out here
		            }
		            else
		            {
		            	Main.models.get(weaponID).render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		            }
	            GL11.glPopMatrix();
	        break;
	
	        case ENTITY:			// Render lying around in the world
	            GL11.glPushMatrix();
		            scale = 1.5F;
		            GL11.glScalef(scale, scale, scale);
		            
		            this.setTexture(stack); // Grabbing the right texture for this frame first
		            Minecraft.getMinecraft().renderEngine.bindTexture(tex);	// And binding it
		            
		            GL11.glRotatef(90F, 1.0f, 0.0f, 0.0f);	// Flipped on its side... (rotate left)
		            GL11.glRotatef(0F, 0.0f, 1.0f, 0.0f);
		            GL11.glRotatef(45F, 0.0f, 0.0f, 1.0f);	// ...and angled slightly (up)
		            
		            //GL11.glTranslatef(-0.2F, 1F, 0F);
		           
		            //Main.models[weaponID].render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		            if (stack.getItemDamage() >= stack.getMaxDamage() && Main.models.get(weaponID) instanceof EnderNymous_Model)
		            {
		            	EnderNymous_Model nym = (EnderNymous_Model) Main.models.get(weaponID);
		            	nym.renderEmpty((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);	// Trying something out here
		            }
		            else
		            {
		            	Main.models.get(weaponID).render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		            }
	            GL11.glPopMatrix();
	        break;
	
	        default:
	        break;
        }
	}
}
