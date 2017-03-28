package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class FlintDuster_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel1;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Trigger;
	ModelRenderer Funnel1;
	ModelRenderer Funnel2;
	ModelRenderer Funnel3;
	ModelRenderer Funnel4;
	ModelRenderer Ammo1;
	ModelRenderer Ammo2;
	ModelRenderer Ammo3;
	
	public FlintDuster_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Barrel1 = new ModelRenderer(this, 0, 0);
		Barrel1.addBox(0F, 0F, 0F, 10, 4, 3);
		Barrel1.setRotationPoint(0F, -1F, -1.5F);
		Barrel1.setTextureSize(64, 32);
		Barrel1.mirror = true;
		setRotation(Barrel1, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 0, 8);
		Stock1.addBox(0F, 0F, 0F, 8, 3, 2);
		Stock1.setRotationPoint(-8F, 0F, -1F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 8);
		Stock2.addBox(0F, 0F, 0F, 8, 3, 2);
		Stock2.setRotationPoint(-10F, 1F, -1F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 21, 8);
		Trigger.addBox(0F, 0F, 0F, 4, 2, 1);
		Trigger.setRotationPoint(-3F, 3F, -0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Funnel1 = new ModelRenderer(this, 0, 14);
		Funnel1.addBox(0F, 0F, 0F, 1, 5, 4);
		Funnel1.setRotationPoint(10F, -1.5F, -2F);
		Funnel1.setTextureSize(64, 32);
		Funnel1.mirror = true;
		setRotation(Funnel1, 0F, 0F, 0F);
		Funnel2 = new ModelRenderer(this, 11, 14);
		Funnel2.addBox(0F, 0F, 0F, 1, 6, 5);
		Funnel2.setRotationPoint(11F, -2F, -2.5F);
		Funnel2.setTextureSize(64, 32);
		Funnel2.mirror = true;
		setRotation(Funnel2, 0F, 0F, 0F);
		Funnel3 = new ModelRenderer(this, 24, 14);
		Funnel3.addBox(0F, 0F, 0F, 1, 7, 6);
		Funnel3.setRotationPoint(12F, -2.5F, -3F);
		Funnel3.setTextureSize(64, 32);
		Funnel3.mirror = true;
		setRotation(Funnel3, 0F, 0F, 0F);
		Funnel4 = new ModelRenderer(this, 39, 14);
		Funnel4.addBox(0F, 0F, 0F, 2, 8, 7);
		Funnel4.setRotationPoint(13F, -3F, -3.5F);
		Funnel4.setTextureSize(64, 32);
		Funnel4.mirror = true;
		setRotation(Funnel4, 0F, 0F, 0F);
		Ammo1 = new ModelRenderer(this, 27, 0);
		Ammo1.addBox(0F, 0F, 0F, 4, 3, 1);
		Ammo1.setRotationPoint(3F, 2F, 1F);
		Ammo1.setTextureSize(64, 32);
		Ammo1.mirror = true;
		setRotation(Ammo1, 0F, 0F, 0F);
		Ammo2 = new ModelRenderer(this, 27, 0);
		Ammo2.addBox(0F, 0F, 0F, 4, 3, 1);
		Ammo2.setRotationPoint(3F, 2F, -2F);
		Ammo2.setTextureSize(64, 32);
		Ammo2.mirror = true;
		setRotation(Ammo2, 0F, 0F, 0F);
		Ammo3 = new ModelRenderer(this, 38, 0);
		Ammo3.addBox(0F, 0F, 0F, 6, 7, 3);
		Ammo3.setRotationPoint(2F, 3F, -1.5F);
		Ammo3.setTextureSize(64, 32);
		Ammo3.mirror = true;
		setRotation(Ammo3, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
	    super.render(entity, x, y, z, yaw, pitch, f5);
	    setRotationAngles(x, y, z, yaw, pitch, f5, entity);

	    Barrel1.render(f5);
	    Stock1.render(f5);
	    Stock2.render(f5);
	    Trigger.render(f5);
	    Funnel1.render(f5);
	    Funnel2.render(f5);
	    Funnel3.render(f5);
	    Funnel4.render(f5);
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
