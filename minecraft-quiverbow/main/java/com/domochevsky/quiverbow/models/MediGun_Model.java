package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class MediGun_Model extends ModelBase
{
	ModelRenderer Barrel;
	ModelRenderer Beacon;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Stock3;
	ModelRenderer Trigger;

	public MediGun_Model()
	{
		textureWidth = 32;
		textureHeight = 32;
	
		Barrel = new ModelRenderer(this, 0, 0);
		Barrel.addBox(0F, 0F, 0F, 13, 4, 3);
		Barrel.setRotationPoint(0F, 0F, -1F);
		Barrel.setTextureSize(32, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		Beacon = new ModelRenderer(this, 0, 8);
		Beacon.addBox(0F, 0F, 0F, 4, 3, 2);
		Beacon.setRotationPoint(13F, 0.5F, -0.5F);
		Beacon.setTextureSize(32, 32);
		Beacon.mirror = true;
		setRotation(Beacon, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 13, 8);
		Stock1.addBox(0F, 0F, 0F, 2, 3, 2);
		Stock1.setRotationPoint(-2F, 1F, -0.5333334F);
		Stock1.setTextureSize(32, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 13, 8);
		Stock2.addBox(0F, 0F, 0F, 2, 3, 2);
		Stock2.setRotationPoint(-3F, 2F, -0.5F);
		Stock2.setTextureSize(32, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Stock3 = new ModelRenderer(this, 0, 14);
		Stock3.addBox(0F, 0F, 0F, 6, 3, 2);
		Stock3.setRotationPoint(-8F, 3F, -0.5F);
		Stock3.setTextureSize(32, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
		Trigger = new ModelRenderer(this, 0, 20);
		Trigger.addBox(0F, 0F, 0F, 4, 3, 1);
		Trigger.setRotationPoint(-2F, 3.466667F, 0F);
		Trigger.setTextureSize(32, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Barrel.render(f5);
		Beacon.render(f5);
		Stock1.render(f5);
		Stock2.render(f5);
		Stock3.render(f5);
		Trigger.render(f5);
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
