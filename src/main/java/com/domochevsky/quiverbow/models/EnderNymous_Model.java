package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class EnderNymous_Model extends ModelBase
{
	//fields
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Stock3;
	ModelRenderer Trigger;
	ModelRenderer Barrel;
	ModelRenderer Barrel2;
	ModelRenderer Barrel3;
	ModelRenderer Barrel4;


	public EnderNymous_Model()
	{
		textureWidth = 32;
		textureHeight = 32;

		Stock1 = new ModelRenderer(this, 0, 7);
		Stock1.addBox(0F, 0F, 0F, 2, 2, 2);
		Stock1.setRotationPoint(-2F, 1F, -1F);
		Stock1.setTextureSize(32, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		
		Stock2 = new ModelRenderer(this, 0, 12);
		Stock2.addBox(0F, 0F, 0F, 2, 3, 2);
		Stock2.setRotationPoint(-3F, 2F, -1F);
		Stock2.setTextureSize(32, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		
		Stock3 = new ModelRenderer(this, 0, 12);
		Stock3.addBox(0F, 0F, 0F, 2, 3, 2);
		Stock3.setRotationPoint(-4F, 3F, -1F);
		Stock3.setTextureSize(32, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
		
		Trigger = new ModelRenderer(this, 9, 7);
		Trigger.addBox(0F, 0F, 0F, 3, 2, 1);
		Trigger.setRotationPoint(-1F, 2.466667F, -0.5333334F);
		Trigger.setTextureSize(32, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		
		Barrel = new ModelRenderer(this, 0, 0);
		Barrel.addBox(0F, -3F, -1.466667F, 10, 3, 3);
		Barrel.setRotationPoint(0F, 3F, 0F);
		Barrel.setTextureSize(32, 32);
		Barrel.mirror = true;
		setRotation(Barrel, 0F, 0F, 0F);
		
		Barrel2 = new ModelRenderer(this, 8, 11);
		Barrel2.addBox(0F, -4.5F, -1F, 10, 1, 2);
		Barrel2.setRotationPoint(0F, 3F, 0F);
		Barrel2.setTextureSize(32, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		
		Barrel3 = new ModelRenderer(this, 8, 3);
		Barrel3.addBox(8F, -4F, -0.5F, 1, 1, 1);
		Barrel3.setRotationPoint(0F, 3F, 0F);
		Barrel3.setTextureSize(32, 32);
		Barrel3.mirror = true;
		setRotation(Barrel3, 0F, 0F, 0F);
		
		Barrel4 = new ModelRenderer(this, 8, 3);
		Barrel4.addBox(1F, -4F, -0.5F, 1, 1, 1);
		Barrel4.setRotationPoint(0F, 3F, 0F);
		Barrel4.setTextureSize(32, 32);
		Barrel4.mirror = true;
		setRotation(Barrel4, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Stock1.render(f5);
		Stock2.render(f5);
		Stock3.render(f5);
		Trigger.render(f5);
		
		// Reset
		
		Barrel.rotateAngleZ = 0.0f;
		Barrel2.rotateAngleZ = 0.0f;
		Barrel3.rotateAngleZ = 0.0f;
		Barrel4.rotateAngleZ = 0.0f;
		
		Barrel.render(f5);
		Barrel2.render(f5);
		Barrel3.render(f5);
		Barrel4.render(f5);
	}
	
	public void renderEmpty(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Stock1.render(f5);
		Stock2.render(f5);
		Stock3.render(f5);
		Trigger.render(f5);
		
		// Rotate based on empty state
		
		Barrel.rotateAngleZ = 45.0f;
		Barrel2.rotateAngleZ = 45.0f;
		Barrel3.rotateAngleZ = 45.0f;
		Barrel4.rotateAngleZ = 45.0f;
		
		Barrel.render(f5);
		Barrel2.render(f5);
		Barrel3.render(f5);
		Barrel4.render(f5);
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
