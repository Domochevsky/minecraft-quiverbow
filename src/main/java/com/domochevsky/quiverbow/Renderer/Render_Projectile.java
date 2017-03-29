package com.domochevsky.quiverbow.Renderer;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.domochevsky.quiverbow.projectiles._ProjectileBase;

public class Render_Projectile extends Render
{	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) 
	{
		_ProjectileBase shot = (_ProjectileBase) entity;
		
		byte[] type = shot.getRenderType();
		
		if (type[0] == 1)	// Regular arrow
		{
			this.doRender_Arrow(shot, x, y, z, yaw, tick);
			//this.doRender_DefaultProjectile((Entity)entity, x, y, z, yaw, tick, type[1], type[2]);
		}
		
		else if (type[0] == 2)	// Generic projectile
		{
			this.doRender_DefaultProjectile(shot, x, y, z, yaw, tick, type[1], type[2]);
		}
		
		else if (type[0] == 3)	// Item icon
		{
			this.doRender_TossedItem(shot, x, y, z, type[1]);
		}
		
		else if (type[0] == 4)	// Arrow, predictive ender projectile
		{
			this.doRender_PredictiveArrow(shot, x, y, z, yaw, tick);
		}
		
		else if (type[0] == 5)	// Beam (Flint Drill)
		{
			this.doRender_Flint_Beam(shot, x, y, z, yaw, tick); 
		}
		
		else if (type[0] == 6)	// Beam (Lightning Red)
		{
			this.doRender_LightningRed(shot, x, y, z, yaw, tick, type[1], type[2]);
    		this.doRender_LightningRed_Beam(shot, x, y, z, yaw, tick);
		}
		
		else if (type[0] == 7)	// Beam (Sunray)
		{
			this.doRender_Sun_Beam(shot, x, y, z, yaw, tick); 
		}
		
		else if (type[0] == 8)	// Beam (MediGun)
		{
			this.doRender_Medi_Beam(shot, x, y, z, yaw, tick); 
		}
		// else, don't know what that is, so not rendering it
	}
	

	@Override
	protected ResourceLocation getEntityTexture(Entity entity)
	{		
		_ProjectileBase shot = (_ProjectileBase) entity;
		String loc = shot.getEntityTexturePath();
		
		if (loc != null) { return new ResourceLocation("quiverchevsky", loc); }
		else	// Texture path is null. Is this using an MC-internal texture?
		{			
			byte[] type = shot.getRenderType();
			
			if (type[0] == 1) { return new ResourceLocation("textures/entity/arrow.png"); }	// Regular arrow
		}
		
		return TextureMap.locationItemsTexture;	// Fallback. Can't remember the damn texture map location, which is what I need for items
	}
	
	
	public void doRender_Arrow(_ProjectileBase shot, double x, double y, double z, float par8, float par9)
    {
        this.bindEntityTexture(shot);
       
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(shot.prevRotationYaw + (shot.rotationYaw - shot.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(shot.prevRotationPitch + (shot.rotationPitch - shot.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);
        
        Tessellator tessellator = Tessellator.instance;
        
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float)(0 + b0 * 10) / 32.0F;
        float f5 = (float)(5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float)(5 + b0 * 10) / 32.0F;
        float f9 = (float)(10 + b0 * 10) / 32.0F;
        float f10 = 0.05625F;
       
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        float f11 = (float) shot.arrowShake - par9;

        if (f11 > 0.0F)
        {
            float f12 = -MathHelper.sin(f11 * 3.0F) * f11;
            GL11.glRotatef(f12, 0.0F, 0.0F, 1.0F);
        }

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();
       
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double)f2, (double)f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double)f3, (double)f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double)f3, (double)f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double)f2, (double)f5);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
	
	
	public void doRender_DefaultProjectile(Entity par1Entity, double x, double y, double z, float yaw, float tick, double length, double width) 
    {
    	GL11.glPushMatrix();
        
        this.bindEntityTexture(par1Entity);
        
        GL11.glDisable(GL11.GL_LIGHTING);			// For proper world lighting, not internal lighting. Otherwise the entity is too dark
        GL11.glDisable(GL11.GL_CULL_FACE);			// Disables the culling of back faces. Good for not having to draw front and back of a thing
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        
        Tessellator tes = Tessellator.instance;		// Grab a instance of the tesselator here (non-static, as they say)
        
        GL11.glRotatef(par1Entity.prevRotationYaw + (par1Entity.rotationYaw - par1Entity.prevRotationYaw) * tick - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(par1Entity.prevRotationPitch + (par1Entity.rotationPitch - par1Entity.prevRotationPitch) * tick, 0.0F, 0.0F, 1.0F);

        float f10 = 0.05625F;
        
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        
        for (int i = 0; i < 2; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            
            tes.startDrawingQuads();
            //			 		 X      Y     Z     TX,   TY
            tes.addVertexWithUV(-length, -width, 0.0D, 0, 0);
            tes.addVertexWithUV( length, -width, 0.0D, 0, 1);
            tes.addVertexWithUV( length,  width, 0.0D, 1, 1);
            tes.addVertexWithUV(-length,  width, 0.0D, 1, 0);
            tes.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
	
	
	public void doRender_TossedItem(Entity shot, double x, double y, double z, int type)
    {
        GL11.glPushMatrix();
	        this.bindEntityTexture(shot);
	        GL11.glTranslatef((float)x, (float)y, (float)z);
	        
	        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        
	        float f2 = 0.5f;

			GL11.glScalef(f2 / 1.0F, f2 / 1.0F, f2 / 1.0F);
	        
	        IIcon icon = Items.baked_potato.getIconFromDamage(0);	// Default, to be overridden with the actual thing
	        
	        if (type == 1) { icon = Items.gold_nugget.getIconFromDamage(0); }
	        else if (type == 2) { icon = Items.glass_bottle.getIconFromDamage(0); }
	        else if (type == 3) { icon = Items.baked_potato.getIconFromDamage(0); }
	        else if (type == 4) { icon = Items.melon_seeds.getIconFromDamage(0); }
	        else if (type == 5) { icon = Items.glowstone_dust.getIconFromDamage(0); }
	        else if (type == 6) { icon = Items.water_bucket.getIconFromDamage(0); }
	        else if (type == 7) { icon = Items.snowball.getIconFromDamage(0); }
	        
	        Tessellator tessellator = Tessellator.instance;
	        
			float f3 = icon.getMinU();
	        float f4 = icon.getMaxU();
	        float f5 = icon.getMinV();
	        float f6 = icon.getMaxV();
	        float f7 = 1.0F;
	        float f8 = 0.5F;
	        float f9 = 0.25F;
	        
	        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
	        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
	        
	        tessellator.startDrawingQuads();
		        tessellator.setNormal(0.0F, 1.0F, 0.0F);
		        tessellator.addVertexWithUV((double)(0.0F - f8), (double)(0.0F - f9), 0.0D, (double)f3, (double)f6);
		        tessellator.addVertexWithUV((double)(f7 - f8), (double)(0.0F - f9), 0.0D, (double)f4, (double)f6);
		        tessellator.addVertexWithUV((double)(f7 - f8), (double)(1.0F - f9), 0.0D, (double)f4, (double)f5);
		        tessellator.addVertexWithUV((double)(0.0F - f8), (double)(1.0F - f9), 0.0D, (double)f3, (double)f5);
	        tessellator.draw();
	        
	        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
	
	
	public void doRender_PredictiveArrow(Entity par1EntityArrow, double par2, double par4, double par6, float par8, float par9)
    {
        this.bindEntityTexture(par1EntityArrow);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(par1EntityArrow.prevRotationYaw + (par1EntityArrow.rotationYaw - par1EntityArrow.prevRotationYaw) * par9 - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(par1EntityArrow.prevRotationPitch + (par1EntityArrow.rotationPitch - par1EntityArrow.prevRotationPitch) * par9, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.instance;
        byte b0 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (float)(0 + b0 * 10) / 32.0F;
        float f5 = (float)(5 + b0 * 10) / 32.0F;
        float f6 = 0.0F;
        float f7 = 0.15625F;
        float f8 = (float)(5 + b0 * 10) / 32.0F;
        float f9 = (float)(10 + b0 * 10) / 32.0F;
        float f10 = 0.05625F;
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();
        GL11.glNormal3f(-f10, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(-7.0D, 2.0D, -2.0D, (double)f6, (double)f8);
        tessellator.addVertexWithUV(-7.0D, 2.0D, 2.0D, (double)f7, (double)f8);
        tessellator.addVertexWithUV(-7.0D, -2.0D, 2.0D, (double)f7, (double)f9);
        tessellator.addVertexWithUV(-7.0D, -2.0D, -2.0D, (double)f6, (double)f9);
        tessellator.draw();

        for (int i = 0; i < 4; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(-8.0D, -2.0D, 0.0D, (double)f2, (double)f4);
            tessellator.addVertexWithUV(8.0D, -2.0D, 0.0D, (double)f3, (double)f4);
            tessellator.addVertexWithUV(8.0D, 2.0D, 0.0D, (double)f3, (double)f5);
            tessellator.addVertexWithUV(-8.0D, 2.0D, 0.0D, (double)f2, (double)f5);
            tessellator.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }
	
	
	public void doRender_LightningRed(_ProjectileBase entity, double x, double y, double z, float yaw, float tick, double length, double width) 
    {
    	GL11.glPushMatrix();
        
        this.bindEntityTexture(entity);
        
        GL11.glDisable(GL11.GL_LIGHTING);			// For proper world lighting, not internal lighting. Otherwise the entity is too dark
        GL11.glDisable(GL11.GL_CULL_FACE);			// Disables the culling of back faces. Good for not having to draw front and back of a thing
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        
        Tessellator tes = Tessellator.instance;		// Grab a instance of the tesselator here (non-static, as they say)
        
        GL11.glRotatef(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * tick - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * tick, 0.0F, 0.0F, 1.0F);

        float f10 = 0.05625F;
        
        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(f10, f10, f10);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        GL11.glNormal3f(f10, 0.0F, 0.0F);
        
        for (int i = 0; i < 2; ++i)
        {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, f10);
            
            tes.startDrawingQuads();
            //			 		 X      Y     Z     TX,   TY
            tes.addVertexWithUV(-length, -width, 0.0D, 0, 0);
            tes.addVertexWithUV( length, -width, 0.0D, 0, 1);
            tes.addVertexWithUV( length,  width, 0.0D, 1, 1);
            tes.addVertexWithUV(-length,  width, 0.0D, 1, 0);
            tes.draw();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }
	
	
	// Trying some beam rendering here
    public void doRender_LightningRed_Beam(_ProjectileBase entity, double x, double y, double z, float yaw, float tick) 
    {
    	GL11.glPushMatrix();
    	
    	this.bindEntityTexture(entity);
    	this.bindTexture(new ResourceLocation("quiverchevsky","textures/entity/redbeam.png"));
    	
    	this.doRender_NeutralBeam(entity, x, y, z, yaw, tick, 0.15f);
    }
	
	
	// The Flint Drill beam
    public void doRender_Flint_Beam(_ProjectileBase entity, double x, double y, double z, float yaw, float tick) 
    {	
    	GL11.glPushMatrix();	// Need to push this first, to get our location set up properly, apparently
    	
    	this.bindEntityTexture(entity);
    	this.bindTexture(new ResourceLocation("quiverchevsky","textures/entity/flintbeam.png"));
    	
    	this.doRender_NeutralBeam(entity, x, y, z, yaw, tick, 0.10f);
    }
    
    
    // Sunray beam
    public void doRender_Sun_Beam(_ProjectileBase entity, double x, double y, double z, float yaw, float tick) 
    {
    	GL11.glPushMatrix();
    		
    	this.bindEntityTexture(entity);
		this.bindTexture(new ResourceLocation("quiverchevsky","textures/entity/sunbeam.png"));
		
		this.doRender_NeutralBeam(entity, x, y, z, yaw, tick, 0.10f);
    }
    
    
    public void doRender_Medi_Beam(_ProjectileBase entity, double x, double y, double z, float yaw, float tick) 
    {
    	GL11.glPushMatrix();
    		
    	this.bindEntityTexture(entity);
		this.bindTexture(new ResourceLocation("quiverchevsky","textures/entity/healthbeam.png"));
		
		this.doRender_NeutralBeam(entity, x, y, z, yaw, tick, 0.05f);
    }
    
    
    public void doRender_NeutralBeam(_ProjectileBase entity, double x, double y, double z, float yaw, float tick, float beamWidth) 
    {
    	//GL11.glPushMatrix();
    	
		//float f2 = par9;// (float) player.innerRotation + par9;
		float f3 = MathHelper.sin(tick * 0.2F) / 2.0F + 0.5F;
		f3 = (f3 * f3 + f3) * 0.2F;
		
		float xPosEnd = (float) (entity.ownerX - entity.posX - (entity.prevPosX - entity.posX) * (double) (1.0F - tick));
		float yPosEnd = (float) ((double) f3 + entity.ownerY - entity.posY - (entity.prevPosY - entity.posY) * (double) (1.0F - tick));
		float zPosEnd = (float) (entity.ownerZ - entity.posZ - (entity.prevPosZ - entity.posZ) * (double) (1.0F - tick));
		
		//for rotation/distance
		float f7 = MathHelper.sqrt_float(xPosEnd * xPosEnd + zPosEnd * zPosEnd);
		float f8 = MathHelper.sqrt_float(xPosEnd * xPosEnd + yPosEnd * yPosEnd + zPosEnd * zPosEnd);
		
		// Changing the starting point here slightly, so it doesn't come out of the center
		//int distance = 6;	// Increasing this will likely increase the distance
		//double adjustmentZ = -MathHelper.cos((entity.shootingEntity.rotationYaw) * (float)Math.PI / 180.0F) * (distance * 0.08);
		//double adjustmentX = MathHelper.sin((entity.shootingEntity.rotationYaw) * (float)Math.PI / 180.0F) * (distance * 0.08);
		
		// Start drawing (i think)
		GL11.glTranslated((float) x, (float) y, (float) z);	// Adjusted with distance to shooter
		GL11.glRotatef((float) (-Math.atan2((double) zPosEnd, (double) xPosEnd)) * 180.0F / (float) Math.PI - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef((float) (-Math.atan2((double) f7, (double) yPosEnd)) * 180.0F / (float) Math.PI - 90.0F, 1.0F, 0.0F, 0.0F);
		Tessellator tessellator = Tessellator.instance;
		
		//Makes the beam look nice
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_CULL_FACE);

		//Shading?
		GL11.glShadeModel(GL11.GL_SMOOTH);

		//from what i can tell f9 and f10 make the beam change (looks like its rotating or whatnot)
		float f9 = 0.0F - ((float) entity.ticksExisted + tick) * 0.01F;
		float f10 = MathHelper.sqrt_float(xPosEnd * xPosEnd + yPosEnd * yPosEnd + zPosEnd * zPosEnd) / 32.0F - ((float) entity.ticksExisted + tick) * 0.01F;

		// change draw mode
		tessellator.startDrawing(5);
		byte b0 = 8;	// how many sides the circle has?
		
		//float beamWidth = 0.15f;

		for (int i = 0; i <= b0; ++i) 	// makes one side circular(fancy math stuff)
		{
	        float f11 = MathHelper.sin((float) (i % b0) * (float) Math.PI * 2.0F / (float) b0) * beamWidth;		// "changing the 2 or .75 should change size of circle"
	        float f12 = MathHelper.cos((float) (i % b0) * (float) Math.PI * 2.0F / (float) b0) * beamWidth;		// The 2 seems to be there to describe a full circle
	        float f13 = (float) (i % b0) * 1.0F / (float) b0;												// 1 makes it a half circle
	        tessellator.setColorOpaque_I(0);
	        tessellator.addVertexWithUV((double) (f11 * 0.2F), (double) (f12 * 0.2F), 0.0D, (double) f13, (double) f10);	// Now how do I shorten the beam slightly
	        tessellator.setColorOpaque_I(16777215);																			// so it doesn't appear from the center of the shooter
	        tessellator.addVertexWithUV((double) f11, (double) f12, (double) f8, (double) f13, (double) f9);				
		}
		
		tessellator.draw();	// draw added vertices
	   
		// reset openGl stuff?
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glShadeModel(GL11.GL_FLAT);
		RenderHelper.enableStandardItemLighting();
    	
    	GL11.glPopMatrix();
    }
}
