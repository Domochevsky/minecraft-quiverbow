package com.domochevsky.quiverbow.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class CrossbowAutoImp_Model extends ModelBase
{
	//fields
	ModelRenderer Body;
	ModelRenderer Body2;
	ModelRenderer Body3;
	ModelRenderer BodyMid;
	ModelRenderer BodyBottom;
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
	ModelRenderer Feather1;
	ModelRenderer Feather2;
	ModelRenderer Feather3;
	ModelRenderer StringLEmpty;
	ModelRenderer StringREmpty;
	ModelRenderer Arrow2;
	ModelRenderer Feather2b;
	ModelRenderer Feather3b;
	ModelRenderer Feather1b;
	ModelRenderer Arrow3;
	ModelRenderer Feather1c;
	ModelRenderer Feather2c;
	ModelRenderer Feather3c;
	ModelRenderer Arrow4;
	ModelRenderer Feather1d;
	ModelRenderer Feather2d;


	public CrossbowAutoImp_Model()
	{
		textureWidth = 64;
		textureHeight = 64;
				
		Body = new ModelRenderer(this, 0, 0);
		Body.addBox(0F, 0F, 0F, 4, 5, 3);
		Body.setRotationPoint(0F, 0F, -1.466667F);
		Body.setTextureSize(64, 64);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		Body2 = new ModelRenderer(this, 0, 28);
		Body2.addBox(0F, 0F, 0F, 17, 5, 4);
		Body2.setRotationPoint(1F, 0.1F, 2F);
		Body2.setTextureSize(64, 64);
		Body2.mirror = true;
		setRotation(Body2, 0F, 0F, 0F);
		Body3 = new ModelRenderer(this, 0, 28);
		Body3.addBox(0F, 0F, 0F, 17, 5, 4);
		Body3.setRotationPoint(1F, 0.1F, -6F);
		Body3.setTextureSize(64, 64);
		Body3.mirror = true;
		setRotation(Body3, 0F, 0F, 0F);
		BodyMid = new ModelRenderer(this, 41, 0);
		BodyMid.addBox(0F, 0F, 0F, 2, 3, 4);
		BodyMid.setRotationPoint(2F, 1F, -2F);
		BodyMid.setTextureSize(64, 64);
		BodyMid.mirror = true;
		setRotation(BodyMid, 0F, 0F, 0F);
		BodyBottom = new ModelRenderer(this, 0, 38);
		BodyBottom.addBox(0F, 0F, 0F, 12, 1, 4);
		BodyBottom.setRotationPoint(3F, 4.6F, -2F);
		BodyBottom.setTextureSize(64, 64);
		BodyBottom.mirror = true;
		setRotation(BodyBottom, 0F, 0F, 0F);
		
		Stock1 = new ModelRenderer(this, 15, 0);
		Stock1.addBox(0F, 0F, 0F, 6, 3, 2);
		Stock1.setRotationPoint(-5F, 1F, -1F);
		Stock1.setTextureSize(64, 64);
		Stock1.mirror = true;
		setRotation(Stock1, 0F, 0F, 0F);
		Stock2 = new ModelRenderer(this, 15, 0);
		Stock2.addBox(0F, 0F, 0F, 7, 3, 2);
		Stock2.setRotationPoint(-10F, 2F, -1F);
		Stock2.setTextureSize(64, 64);
		Stock2.mirror = true;
		setRotation(Stock2, 0F, 0F, 0F);
		
		Front1 = new ModelRenderer(this, 0, 15);
		Front1.addBox(0F, 0F, 0F, 1, 2, 10);
		Front1.setRotationPoint(16F, 0F, 0F);
		Front1.setTextureSize(64, 64);
		Front1.mirror = true;
		setRotation(Front1, 0F, -3.141593F, 0F);
		Front2 = new ModelRenderer(this, 0, 15);
		Front2.addBox(0F, 0F, 0F, 1, 2, 10);
		Front2.setRotationPoint(15F, 0F, 0F);
		Front2.setTextureSize(64, 64);
		Front2.mirror = true;
		setRotation(Front2, 0F, 0F, 0F);
		FrontL = new ModelRenderer(this, 23, 15);
		FrontL.addBox(0F, 0F, 0F, 1, 2, 10);
		FrontL.setRotationPoint(14.5F, -1F, 8F);
		FrontL.setTextureSize(64, 64);
		FrontL.mirror = true;
		setRotation(FrontL, 0F, -0.1858931F, 0F);
		FrontR = new ModelRenderer(this, 23, 15);
		FrontR.addBox(0F, 0F, 0F, 1, 2, 10);
		FrontR.setRotationPoint(15.5F, -1F, -8F);
		FrontR.setTextureSize(64, 64);
		FrontR.mirror = true;
		setRotation(FrontR, 0F, -2.9557F, 0F);
		
		Trigger = new ModelRenderer(this, 34, 0);
		Trigger.addBox(0F, 0F, 0F, 2, 1, 1);
		Trigger.setRotationPoint(-2F, 4F, -0.5F);
		Trigger.setTextureSize(64, 64);
		Trigger.mirror = true;
		setRotation(Trigger, 0F, 0F, 0F);
		
		Arrow = new ModelRenderer(this, 0, 12);
		Arrow.addBox(0F, 0F, 0F, 15, 1, 1);
		Arrow.setRotationPoint(2F, 4F, -0.5F);
		Arrow.setTextureSize(64, 64);
		Arrow.mirror = true;
		setRotation(Arrow, 0F, 0F, 0F);
		
		StringL = new ModelRenderer(this, 0, 9);
		StringL.addBox(0F, 0F, 0F, 20, 1, 0);
		StringL.setRotationPoint(2F, -1F, 0.4F);
		StringL.setTextureSize(64, 64);
		StringL.mirror = true;
		setRotation(StringL, 0F, -1.003822F, 0F);
		StringR = new ModelRenderer(this, 0, 9);
		StringR.addBox(0F, 0F, 0F, 20, 1, 0);
		StringR.setRotationPoint(2F, -1F, -0.4F);
		StringR.setTextureSize(64, 64);
		StringR.mirror = true;
		setRotation(StringR, 0F, 1.003815F, 0F);
		
		Feather1 = new ModelRenderer(this, 33, 12);
		Feather1.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather1.setRotationPoint(3F, 4F, 0.5F);
		Feather1.setTextureSize(64, 64);
		Feather1.mirror = true;
		setRotation(Feather1, 0F, 0F, 0F);
		Feather2 = new ModelRenderer(this, 33, 12);
		Feather2.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather2.setRotationPoint(3F, 4F, -1.5F);
		Feather2.setTextureSize(64, 64);
		Feather2.mirror = true;
		setRotation(Feather2, 0F, 0F, 0F);
		Feather3 = new ModelRenderer(this, 33, 12);
		Feather3.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather3.setRotationPoint(3F, 3F, -0.5333334F);
		Feather3.setTextureSize(64, 64);
		Feather3.mirror = true;
		setRotation(Feather3, 0F, 0F, 0F);
		
		StringLEmpty = new ModelRenderer(this, 0, 10);
		StringLEmpty.addBox(0F, 0F, 0F, 17, 1, 0);
		StringLEmpty.setRotationPoint(12.6F, -1F, 0F);
		StringLEmpty.setTextureSize(64, 64);
		StringLEmpty.mirror = true;
		setRotation(StringLEmpty, 0F, -1.553343F, 0F);
		StringREmpty = new ModelRenderer(this, 0, 10);
		StringREmpty.addBox(0F, 0F, 0F, 17, 1, 0);
		StringREmpty.setRotationPoint(12.6F, -1F, 0F);
		StringREmpty.setTextureSize(64, 64);
		StringREmpty.mirror = true;
		setRotation(StringREmpty, 0F, 1.56207F, 0F);
		
		Arrow2 = new ModelRenderer(this, 0, 12);
		Arrow2.addBox(0F, 0F, 0F, 15, 1, 1);
		Arrow2.setRotationPoint(2F, -1F, -0.5F);
		Arrow2.setTextureSize(64, 64);
		Arrow2.mirror = true;
		setRotation(Arrow2, 0F, 0F, 0F);
		Feather2b = new ModelRenderer(this, 33, 12);
		Feather2b.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather2b.setRotationPoint(3F, -1F, -1.5F);
		Feather2b.setTextureSize(64, 64);
		Feather2b.mirror = true;
		setRotation(Feather2b, 0F, 0F, 0F);
		Feather3b = new ModelRenderer(this, 33, 12);
		Feather3b.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather3b.setRotationPoint(3F, -2F, -0.5333334F);
		Feather3b.setTextureSize(64, 64);
		Feather3b.mirror = true;
		setRotation(Feather3b, 0F, 0F, 0F);
		Feather1b = new ModelRenderer(this, 33, 12);
		Feather1b.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather1b.setRotationPoint(3F, -1F, 0.5F);
		Feather1b.setTextureSize(64, 64);
		Feather1b.mirror = true;
		setRotation(Feather1b, 0F, 0F, 0F);
		
		Arrow3 = new ModelRenderer(this, 0, 12);
		Arrow3.addBox(0F, 0F, 0F, 15, 1, 1);
		Arrow3.setRotationPoint(2.666667F, 3F, -0.5F);
		Arrow3.setTextureSize(64, 64);
		Arrow3.mirror = true;
		setRotation(Arrow3, 0F, 0F, 0F);
		Feather1c = new ModelRenderer(this, 33, 12);
		Feather1c.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather1c.setRotationPoint(3.666667F, 3F, 0.5F);
		Feather1c.setTextureSize(64, 64);
		Feather1c.mirror = true;
		setRotation(Feather1c, 0F, 0F, 0F);
		Feather2c = new ModelRenderer(this, 33, 12);
		Feather2c.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather2c.setRotationPoint(3.666667F, 3F, -1.5F);
		Feather2c.setTextureSize(64, 64);
		Feather2c.mirror = true;
		setRotation(Feather2c, 0F, 0F, 0F);
		Feather3c = new ModelRenderer(this, 33, 12);
		Feather3c.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather3c.setRotationPoint(3F, 1F, -0.5333334F);
		Feather3c.setTextureSize(64, 64);
		Feather3c.mirror = true;
		setRotation(Feather3c, 0F, 0F, 0F);
		
		Arrow4 = new ModelRenderer(this, 0, 12);
		Arrow4.addBox(0F, 0F, 0F, 15, 1, 1);
		Arrow4.setRotationPoint(2F, 2F, -0.5F);
		Arrow4.setTextureSize(64, 64);
		Arrow4.mirror = true;
		setRotation(Arrow4, 0F, 0F, 0F);
		Feather1d = new ModelRenderer(this, 33, 12);
		Feather1d.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather1d.setRotationPoint(3F, 2F, 0.5F);
		Feather1d.setTextureSize(64, 64);
		Feather1d.mirror = true;
		setRotation(Feather1d, 0F, 0F, 0F);
		Feather2d = new ModelRenderer(this, 33, 12);
		Feather2d.addBox(0F, 0F, 0F, 4, 1, 1);
		Feather2d.setRotationPoint(3F, 2F, -1.5F);
		Feather2d.setTextureSize(64, 64);
		Feather2d.mirror = true;
		setRotation(Feather2d, 0F, 0F, 0F);
	}

	public void render(Entity entity, float x, float y, float z, float yaw, float pitch, float f5)
	{
		super.render(entity, x, y, z, yaw, pitch, f5);
		setRotationAngles(x, y, z, yaw, pitch, f5, entity);
		
		Body.render(f5);
		Body2.render(f5);
		Body3.render(f5);
		BodyMid.render(f5);
		BodyBottom.render(f5);
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
		Feather1.render(f5);
		Feather2.render(f5);
		Feather3.render(f5);
		StringLEmpty.render(f5);
		StringREmpty.render(f5);
		Arrow2.render(f5);
		Feather2b.render(f5);
		Feather3b.render(f5);
		Feather1b.render(f5);
		Arrow3.render(f5);
		Feather1c.render(f5);
		Feather2c.render(f5);
		Feather3c.render(f5);
		Arrow4.render(f5);
		Feather1d.render(f5);
		Feather2d.render(f5);
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
