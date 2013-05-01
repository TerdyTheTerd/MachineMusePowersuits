package net.machinemuse.powersuits.client.render.modelspec

import net.minecraftforge.client.model.obj.WavefrontObject
import net.minecraftforge.client.model.AdvancedModelLoader
import net.machinemuse.utils.{MuseRegistry, MuseStringUtils}
import net.minecraft.util.Vec3
import net.machinemuse.general.geometry.Colour
import net.machinemuse.general.MuseLogger
import net.minecraft.nbt.NBTTagCompound

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:44 AM, 4/28/13
 */
object ModelRegistry extends MuseRegistry[ModelSpec] {
  def loadModel(resource: String): Option[WavefrontObject] = {
    val name = MuseStringUtils.extractName(resource)
    get(name) match {
      case Some(i) => Some(i.model)
      case None => wrap(resource)
    }
  }

  def wrap(resource: String): Option[WavefrontObject] = {
    MuseLogger.logDebug("Loading " + resource + " as " + MuseStringUtils.extractName(resource))
    AdvancedModelLoader.loadModel(resource) match {
      case m: WavefrontObject => Some(m)
      case _ => MuseLogger.logError("Model loading failed :( " + resource)
    }
  }

  def getModel(nbt: NBTTagCompound): Option[ModelSpec] = {
    get(nbt.getString("model"))
  }

  def getPart(nbt: NBTTagCompound, model: ModelSpec) = {
    model.get(nbt getString "part")
  }

  def getPart(nbt: NBTTagCompound): Option[ModelPartSpec] = {
    getModel(nbt).flatMap(m => m.get(nbt getString "part"))
  }
}

class ModelSpec(val model: WavefrontObject,
                val textures: Array[String],
                val offset: Option[Vec3],
                val rotation: Option[Vec3],
                val filename: String
                 ) extends MuseRegistry[ModelPartSpec] {
  def applyOffsetAndRotation = {

  }

}

class ModelPartSpec(val modelSpec: ModelSpec,
                    val morph: MorphTarget,
                    val partName: String,
                    val slot: Int,
                    val defaultcolour: Colour = Colour.WHITE,
                    val defaultglow: Boolean = false,
                    val displayName: String) {

  def getTexture(nbt: NBTTagCompound): String = {
    if (nbt hasKey "texture") nbt getString "texture" else modelSpec.textures.head
  }

  def setTexture(nbt: NBTTagCompound, s: String) = {
    if (s.equals("") || s.equalsIgnoreCase(modelSpec.textures.head)) nbt removeTag "texture" else nbt setString("texture", s)
  }

  def getColour(nbt: NBTTagCompound): Colour = {
    if (nbt hasKey "colour") new Colour(nbt getInteger "colour") else defaultcolour
  }

  def setColour(nbt: NBTTagCompound, c: Colour) = {
    val cint = c.getInt
    if (cint equals defaultcolour.getInt) nbt removeTag "colour" else nbt setInteger("colour", cint)
  }

  def getGlow(nbt: NBTTagCompound): Boolean = {
    if (nbt hasKey "glow") nbt getBoolean "glow" else defaultglow
  }

  def setGlow(nbt: NBTTagCompound, g: Boolean) = {
    if (g == defaultglow) nbt removeTag "glow" else nbt setBoolean("glow", g)
  }

  def setModel(nbt: NBTTagCompound, model: ModelSpec) {
    setModel(nbt, ModelRegistry.inverse.get(model).getOrElse(""))
  }

  def setModel(nbt: NBTTagCompound, modelname: String) {
    nbt setString("model", modelname)
  }

  def setPart(nbt: NBTTagCompound) {
    nbt setString("part", partName)
  }

  def setPartAndModel(nbt: NBTTagCompound) {
  }

  def multiSet(nbt: NBTTagCompound, tex: Option[String], glow: Option[Boolean], c: Option[Colour]): NBTTagCompound = {
    setPart(nbt)
    setModel(nbt, this.modelSpec)
    setTexture(nbt, tex.getOrElse(""))
    setGlow(nbt, glow.getOrElse(false))
    setColour(nbt, c.getOrElse(defaultcolour))
    nbt
  }

}