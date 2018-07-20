package net.machinemuse.powersuits.powermodule.tool;

import net.machinemuse.numina.api.module.EnumModuleCategory;
import net.machinemuse.numina.api.module.EnumModuleTarget;
import net.machinemuse.numina.api.module.IBlockBreakingModule;
import net.machinemuse.numina.api.module.IToggleableModule;
import net.machinemuse.numina.utils.item.MuseItemUtils;
import net.machinemuse.powersuits.api.module.ModuleManager;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.machinemuse.powersuits.utils.ElectricItemUtils;
import net.machinemuse.powersuits.utils.MuseCommonStrings;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

public class AquaAffinityModule extends PowerModuleBase implements IBlockBreakingModule, IToggleableModule {
    public static final String MODULE_AQUA_AFFINITY = "Aqua Affinity";
    public static final String AQUA_AFFINITY_ENERGY_CONSUMPTION = "Underwater Energy Consumption";
    public static final String UNDERWATER_HARVEST_SPEED = "Underwater Harvest Speed";

    public AquaAffinityModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), MuseItemUtils.copyAndResize(ItemComponent.servoMotor, 1));
        addBaseProperty(AQUA_AFFINITY_ENERGY_CONSUMPTION, 0, "J");
        addBaseProperty(UNDERWATER_HARVEST_SPEED, 0.2, "%");
        addTradeoffProperty("Power", AQUA_AFFINITY_ENERGY_CONSUMPTION, 100);
        addTradeoffProperty("Power", UNDERWATER_HARVEST_SPEED, 0.8);
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.CATEGORY_SPECIAL;
    }

    @Override
    public String getDataName() {
        return MODULE_AQUA_AFFINITY;
    }

    @Override
    public String getUnlocalizedName() {
        return "aquaAffinity";
    }

    @Override
    public boolean canHarvestBlock(ItemStack stack, IBlockState state, EntityPlayer player) {
        return false;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if (entityLiving.isInsideOfMaterial(Material.WATER) || !entityLiving.onGround) {
            ElectricItemUtils.drainPlayerEnergy((EntityPlayer) entityLiving,
                    ModuleManager.INSTANCE.computeModularProperty(stack, AQUA_AFFINITY_ENERGY_CONSUMPTION));
        }
        return true;
    }

    @Override
    public void handleBreakSpeed(BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = player.inventory.getCurrentItem();
        if (event.getNewSpeed() > 1
                && (player.isInsideOfMaterial(Material.WATER) || !player.onGround)
                && ElectricItemUtils.getPlayerEnergy(player) > ModuleManager.INSTANCE.computeModularProperty(stack, AQUA_AFFINITY_ENERGY_CONSUMPTION)) {
            event.setNewSpeed((float) (event.getNewSpeed() * 5 * ModuleManager.INSTANCE.computeModularProperty(stack, UNDERWATER_HARVEST_SPEED)));
        }
    }

    @Override
    public ItemStack getEmulatedTool() {
        return null;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.aquaAffinity;
    }
}