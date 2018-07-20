package net.machinemuse.powersuits.api.electricity.adapter;

import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.IElectricItem;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.machinemuse.powersuits.common.ModCompatibility;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Ported to Java by lehjr on 11/4/16.
 */
public abstract class ElectricAdapter {
    @Nullable
    public static ElectricAdapter wrap(@Nonnull ItemStack itemStack) {
        if (itemStack.isEmpty())
            return null;
        Item i = itemStack.getItem();
        if (i instanceof IMuseElectricItem) {
//
//            System.out.println("MuseElectricItem");
            return new MuseElectricAdapter(itemStack);

        // Forge Energy
        } else if (itemStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
//            System.out.println("Forge Energy");

                return new ForgeEnergyAdapter(itemStack);

        // TESLA (need all 3 in order to get power in and out)
        } else if (itemStack.hasCapability(TeslaCapabilities.CAPABILITY_HOLDER, null) &&
                itemStack.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, null) &&
                itemStack.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, null)) {

//            System.out.println("Tesla energy");
            return new TeslaEnergyAdapter(itemStack);

        // RF API
        } else if (ModCompatibility.isRFAPILoaded() && i instanceof IEnergyContainerItem) {

//            System.out.println("RF API");
            return new TEElectricAdapter(itemStack);

        // Industrialcraft
        } else if (ModCompatibility.isIndustrialCraftLoaded() && i instanceof IElectricItem) {
//            System.out.println("IC2 energy");
            return new IC2ElectricAdapter(itemStack);
        } else {
            return null;
        }
    }

    public abstract double getCurrentMPSEnergy();

    public abstract double getMaxMPSEnergy();

    public abstract double drainMPSEnergy(double requested);

    public abstract double giveMPSEnergy(double provided);
}