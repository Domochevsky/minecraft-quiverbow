package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class LightningRed_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel;
	ModelRenderer Stock2;
	ModelRenderer Stock3;
	ModelRenderer Trigger;
	ModelRenderer ProngTop;
	ModelRenderer ProngBottom;
	ModelRenderer CrisTop;
	ModelRenderer Ammo1;
	ModelRenderer Ammo2;
	ModelRenderer Ammo3;

	public LightningRed_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Barrel = new ModelRenderer(this, 0, 0);
		Barrel.addBox(0F, 0F, 0F, 5, 4, 3);
		Barrel.setRotationPoint(0F, 0F, -0.5F);
		Barrel.setTextureSize(64, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 8);
		Stock2.addBox(0F, 0F, 0F, 4, 3, 2);
		Stock2.setRotationPoint(-4F, 1F, 0F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Stock3 = new ModelRenderer(this, 15, 8);
		Stock3.addBox(0F, 0F, 0F, 6, 3, 2);
		Stock3.setRotationPoint(-8F, 2F, 0F);
		Stock3.setTextureSize(64, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 14);
		Trigger.addBox(0F, 0F, 0F, 5, 2, 1);
		Trigger.setRotationPoint(-4.5F, 4F, 0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		ProngTop = new ModelRenderer(this, 17, 0);
		ProngTop.addBox(0F, 0F, 0F, 14, 2, 1);
		ProngTop.setRotationPoint(4F, -0.5F, 0.5F);
		ProngTop.setTextureSize(64, 32);
		ProngTop.mirror = true;
		setRotation(ProngTop, 0F, 0F, 0F);
		ProngBottom = new ModelRenderer(this, 17, 4);
		ProngBottom.addBox(0F, 0F, 0F, 18, 2, 1);
		ProngBottom.setRotationPoint(0F, 2.5F, 0.5F);
		ProngBottom.setTextureSize(64, 32);
		ProngBottom.mirror = true;
		setRotation(ProngBottom, 0F, 0F, 0F);
		CrisTop = new ModelRenderer(this, 24, 14);
		CrisTop.addBox(0F, 0F, 0F, 2, 4, 2);
		CrisTop.setRotationPoint(1F, -4F, 0F);
		CrisTop.setTextureSize(64, 32);
		CrisTop.mirror = true;
		setRotation(CrisTop, 0F, 0F, 0F);
		Ammo1 = new ModelRenderer(this, 13, 14);
		Ammo1.addBox(0F, 0F, 0F, 1, 7, 4);
		Ammo1.setRotationPoint(2F, 3F, -1F);
		Ammo1.setTextureSize(64, 32);
		Ammo1.mirror = true;
		setRotation(Ammo1, 0F, 0F, 0F);
		Ammo2 = new ModelRenderer(this, 0, 19);
		Ammo2.addBox(0F, 0F, 0F, 3, 2, 3);
		Ammo2.setRotationPoint(1F, 5F, -0.5F);
		Ammo2.setTextureSize(64, 32);
		Ammo2.mirror = true;
		setRotation(Ammo2, 0F, 0F, 0F);
		Ammo3 = new ModelRenderer(this, 0, 19);
		Ammo3.addBox(0F, 0F, 0F, 3, 2, 3);
		Ammo3.setRotationPoint(1F, 7.5F, -0.5F);
		Ammo3.setTextureSize(64, 32);
		Ammo3.mirror = true;
		setRotation(Ammo3, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);
	    
	    Barrel.render(f5);
	    Stock2.render(f5);
	    Stock3.render(f5);
	    Trigger.render(f5);
	    ProngTop.render(f5);
	    ProngBottom.render(f5);
	    CrisTop.render(f5);
	    Ammo1.render(f5);
	    Ammo2.render(f5);
	    Ammo3.render(f5);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
  
	public void setRotationAngles(float x, float y, float z, float yaw, float pitch, float tick, Entity entity)
	{
		super.setRotationAngles(x, y, z, yaw, pitch, tick, entity);
	}
}
