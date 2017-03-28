package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CrossbowDouble_Model extends ModelBase
{
	//fields
	ModelRenderer Body;
	ModelRenderer Stock1;
	ModelRenderer Stock2;
	ModelRenderer Front1;
	ModelRenderer Front2;
	ModelRenderer FrontL;
	ModelRenderer FrontR;
	ModelRenderer Trigger;
	ModelRenderer Arrow;
	ModelRenderer StringL;
	ModelRenderer StringR;
	ModelRenderer String3;
	ModelRenderer Feather1;
	ModelRenderer Feather2;
	ModelRenderer Feather3;
	ModelRenderer StringLEmpty;
	ModelRenderer StringREmpty;
	ModelRenderer Arrow2;
	ModelRenderer Feather3b;
	ModelRenderer Feather1b;
	ModelRenderer Feather2b;

	public CrossbowDouble_Model()
	{
		textureWidth = 64;
		textureHeight = 32;
		
		Body = new ModelRenderer(this, 0, 0);
		Body.addBox(0F, 0F, 0F, 15, 3, 6);
		Body.setRotationPoint(0F, 0F, -3F);
		Body.setTextureSize(64, 32);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		Stock1 = new ModelRenderer(this, 48, 0);
		Stock1.addBox(0F, 0F, 0F, 6, 3, 2);
		Stock1.setRotationPoint(-5F, 1F, -1F);
		Stock1.setTextureSize(64, 32);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 46, 6);
		Stock2.addBox(0F, 0F, 0F, 7, 3, 2);
		Stock2.setRotationPoint(-10F, 2F, -1F);
		Stock2.setTextureSize(64, 32);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		Front1 = new ModelRenderer(this, 0, 20);
		Front1.addBox(0F, 0F, 0F, 1, 2, 10);
		Front1.setRotationPoint(16F, 0F, 0F);
		Front1.setTextureSize(64, 32);
		Front1.mirror = true;
		setRotation(Front1, 0F, -3.141593F, 0F);
		Front2 = new ModelRenderer(this, 0, 20);
		Front2.addBox(0F, 0F, 0F, 1, 2, 10);
		Front2.setRotationPoint(15F, 0F, 0F);
		Front2.setTextureSize(64, 32);
		Front2.mirror = true;
		setRotation(Front2, 0F, 0F, 0F);
		FrontL = new ModelRenderer(this, 23, 20);
		FrontL.addBox(0F, 0F, 0F, 1, 2, 10);
		FrontL.setRotationPoint(14.5F, -1F, 8F);
		FrontL.setTextureSize(64, 32);
		FrontL.mirror = true;
		setRotation(FrontL, 0F, 0F, 0F);
		FrontR = new ModelRenderer(this, 23, 20);
		FrontR.addBox(0F, 0F, 0F, 1, 2, 10);
		FrontR.setRotationPoint(15.5F, -1F, -8F);
		FrontR.setTextureSize(64, 32);
		FrontR.mirror = true;
		setRotation(FrontR, 0F, -3.141593F, 0F);
		Trigger = new ModelRenderer(this, 37, 0);
		Trigger.addBox(0F, 0F, 0F, 4, 2, 1);
		Trigger.setRotationPoint(-1F, 3F, -0.5F);
		Trigger.setTextureSize(64, 32);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		Arrow = new ModelRenderer(this, 0, 15);
		Arrow.addBox(0F, 0F, 0F, 15, 1, 1);
		Arrow.setRotationPoint(2F, -1F, 1F);
		Arrow.setTextureSize(64, 32);
		Arrow.mirror = true;
		setRotation(Arrow, 0F, 0F, 0F);
		StringL = new ModelRenderer(this, 0, 11);
		StringL.addBox(0F, 0F, 0F, 20, 1, 0);
		StringL.setRotationPoint(2F, -1F, 1.4F);
		StringL.setTextureSize(64, 32);
		StringL.mirror = true;
		setRotation(StringL, 0F, -0.8922867F, 0F);
		StringR = new ModelRenderer(this, 0, 11);
		StringR.addBox(0F, 0F, 0F, 20, 1, 0);
		StringR.setRotationPoint(2F, -1F, -1.4F);
		StringR.setTextureSize(64, 32);
		StringR.mirror = true;
		setRotation(StringR, 0F, 0.892279F, 0F);
		String3 = new ModelRenderer(this, 0, 8);
		String3.addBox(0F, 0F, 0F, 0, 1, 3);
		String3.setRotationPoint(2F, -1F, -1.5F);
		String3.setTextureSize(64, 32);
		String3.mirror = true;
		setRotation(String3, 0F, 0F, 0F);
		Feather1 = new ModelRenderer(this, 0, 18);
		Feather1.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather1.setRotationPoint(3F, -1F, 2F);
		Feather1.setTextureSize(64, 32);
		Feather1.mirror = true;
		setRotation(Feather1, 0F, 0F, 0F);
		Feather2 = new ModelRenderer(this, 0, 18);
		Feather2.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather2.setRotationPoint(3F, -1F, 0F);
		Feather2.setTextureSize(64, 32);
		Feather2.mirror = true;
		setRotation(Feather2, 0F, 0F, 0F);
		Feather3 = new ModelRenderer(this, 0, 18);
		Feather3.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather3.setRotationPoint(3F, -2F, 0.9666666F);
		Feather3.setTextureSize(64, 32);
		Feather3.mirror = true;
		setRotation(Feather3, 0F, 0F, 0F);
		StringLEmpty = new ModelRenderer(this, 0, 10);
		StringLEmpty.addBox(0F, 0F, 0F, 17, 1, 0);
		StringLEmpty.setRotationPoint(14F, -1F, 0F);
		StringLEmpty.setTextureSize(64, 32);
		StringLEmpty.mirror = true;
		setRotation(StringLEmpty, 0F, -1.553343F, 0F);
		StringREmpty = new ModelRenderer(this, 0, 10);
		StringREmpty.addBox(0F, 0F, 0F, 17, 1, 0);
		StringREmpty.setRotationPoint(14F, -1F, 0F);
		StringREmpty.setTextureSize(64, 32);
		StringREmpty.mirror = true;
		setRotation(StringREmpty, 0F, 1.56207F, 0F);
		Arrow2 = new ModelRenderer(this, 0, 13);
		Arrow2.addBox(0F, 0F, 0F, 15, 1, 1);
		Arrow2.setRotationPoint(2F, -1F, -2F);
		Arrow2.setTextureSize(64, 32);
		Arrow2.mirror = true;
		setRotation(Arrow2, 0F, 0F, 0F);
		Feather3b = new ModelRenderer(this, 11, 18);
		Feather3b.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather3b.setRotationPoint(3F, -1F, -3F);
		Feather3b.setTextureSize(64, 32);
		Feather3b.mirror = true;
		setRotation(Feather3b, 0F, 0F, 0F);
		Feather1b = new ModelRenderer(this, 11, 18);
		Feather1b.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather1b.setRotationPoint(3F, -1F, -1F);
		Feather1b.setTextureSize(64, 32);
		Feather1b.mirror = true;
		setRotation(Feather1b, 0F, 0F, 0F);
		Feather2b = new ModelRenderer(this, 11, 18);
		Feather2b.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather2b.setRotationPoint(3F, -2F, -2F);
		Feather2b.setTextureSize(64, 32);
		Feather2b.mirror = true;
		setRotation(Feather2b, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Body.render(f5);
		Stock1.render(f5);
		Stock2.render(f5);
		Front1.render(f5);
		Front2.render(f5);
		FrontL.render(f5);
		FrontR.render(f5);
		Trigger.render(f5);
		Arrow.render(f5);
		StringL.render(f5);
		StringR.render(f5);
		String3.render(f5);
		Feather1.render(f5);
		Feather2.render(f5);
		Feather3.render(f5);
		StringLEmpty.render(f5);
		StringREmpty.render(f5);
		Arrow2.render(f5);
		Feather3b.render(f5);
		Feather1b.render(f5);
		Feather2b.render(f5);
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
