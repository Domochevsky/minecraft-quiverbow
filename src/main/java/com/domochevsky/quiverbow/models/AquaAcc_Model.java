package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class AquaAcc_Model extends ModelBase
{
	//fields
	ModelRenderer Barrel1;
	ModelRenderer Barrel2;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Stock3;
	ModelRenderer Shape1;

	public AquaAcc_Model()
	{
		textureWidth = 32;
		textureHeight = 32;
		
		Barrel1 = new ModelRenderer(this, 18, 13);
		Barrel1.addBox(0F, 0F, 0F, 2, 4, 4);
		Barrel1.setRotationPoint(0F, 0F, -2F);
		Barrel1.setTextureSize(32, 32);
		Barrel1.mirror = true;
		setRotation(Barrel1, 0F, 0F, 0F);
		Barrel2 = new ModelRenderer(this, 0, 0);
		Barrel2.addBox(0F, 0F, 0F, 9, 6, 6);
		Barrel2.setRotationPoint(2F, -1F, -3F);
		Barrel2.setTextureSize(32, 32);
		Barrel2.mirror = true;
		setRotation(Barrel2, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 9, 14);
		Stock1.addBox(0F, 0F, 0F, 2, 2, 2);
		Stock1.setRotationPoint(-2F, 1F, -1F);
		Stock1.setTextureSize(32, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 0, 14);
		Stock2.addBox(0F, 0F, 0F, 2, 3, 2);
		Stock2.setRotationPoint(-3F, 2F, -1F);
		Stock2.setTextureSize(32, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Stock3 = new ModelRenderer(this, 0, 20);
		Stock3.addBox(0F, 0F, 0F, 2, 3, 2);
		Stock3.setRotationPoint(-4F, 3F, -1F);
		Stock3.setTextureSize(32, 32);
		Stock3.mirror = true;
		setRotation(Stock3, 0F, 0F, 0F);
		Shape1 = new ModelRenderer(this, 0, 26);
		Shape1.addBox(0F, 0F, 0F, 3, 2, 1);
		Shape1.setRotationPoint(-1F, 2.466667F, -0.5333334F);
		Shape1.setTextureSize(32, 32);
		Shape1.mirror = true;
		setRotation(Shape1, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Barrel1.render(f5);
		Barrel2.render(f5);
		Stock1.render(f5);
		Stock2.render(f5);
		Stock3.render(f5);
		Shape1.render(f5);

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
