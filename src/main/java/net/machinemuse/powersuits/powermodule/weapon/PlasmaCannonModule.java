package net.machinemuse.powersuits.powermodule.weapon;

import net.machinemuse.numina.api.module.EnumModuleCategory;
import net.machinemuse.numina.api.module.EnumModuleTarget;
import net.machinemuse.numina.api.module.IRightClickModule;
import net.machinemuse.numina.general.MuseMathUtils;
import net.machinemuse.numina.utils.item.MuseItemUtils;
import net.machinemuse.powersuits.api.module.ModuleManager;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.entity.EntityPlasmaBolt;
import net.machinemuse.powersuits.item.ItemComponent;
import net.machinemuse.powersuits.powermodule.PowerModuleBase;
import net.machinemuse.powersuits.utils.ElectricItemUtils;
import net.machinemuse.powersuits.utils.MuseHeatUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//import net.machinemuse.powersuits.network.packets.MusePacketPlasmaBolt;

public class PlasmaCannonModule extends PowerModuleBase implements IRightClickModule {
    public static final String MODULE_PLASMA_CANNON = "Plasma Cannon";
    public static final String PLASMA_CANNON_ENERGY_PER_TICK = "Plasma Energy Per Tick";
    public static final String PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE = "Plasma Damage At Full Charge";
    public static final String PLASMA_CANNON_EXPLOSIVENESS = "Plasma Explosiveness";

    public PlasmaCannonModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        addBaseProperty(PLASMA_CANNON_ENERGY_PER_TICK, 10, "J");
        addBaseProperty(PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 2, "pt");
        addTradeoffProperty("Amperage", PLASMA_CANNON_ENERGY_PER_TICK, 150, "J");
        addTradeoffProperty("Amperage", PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE, 38, "pt");
        addTradeoffProperty("Voltage", PLASMA_CANNON_ENERGY_PER_TICK, 50, "J");
        addTradeoffProperty("Voltage", PLASMA_CANNON_EXPLOSIVENESS, 0.5, "Creeper");
        ModuleManager.INSTANCE.addInstallCost(getDataName(), MuseItemUtils.copyAndResize(ItemComponent.fieldEmitter, 2));
        ModuleManager.INSTANCE.addInstallCost(getDataName(), MuseItemUtils.copyAndResize(ItemComponent.hvcapacitor, 2));
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.CATEGORY_WEAPON;
    }

    @Override
    public String getDataName() {
        return MODULE_PLASMA_CANNON;
    }

    @Override
    public String getUnlocalizedName() { return "plasmaCannon";
    }

    @Override
    public ActionResult onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (hand == EnumHand.MAIN_HAND && ElectricItemUtils.getPlayerEnergy(playerIn) > 500) {
            playerIn.setActiveHand(hand);
            return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
        }
        return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return EnumActionResult.PASS;
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return EnumActionResult.PASS;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack itemStack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        int chargeTicks = (int) MuseMathUtils.clampDouble(itemStack.getMaxItemUseDuration() - timeLeft, 10, 50);

        if (!worldIn.isRemote) {
            double energyConsumption = ModuleManager.INSTANCE.computeModularProperty(itemStack, PlasmaCannonModule.PLASMA_CANNON_ENERGY_PER_TICK) * chargeTicks;
            if (entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityLiving;
                MuseHeatUtils.heatPlayer(player, energyConsumption / 500);
                if (ElectricItemUtils.getPlayerEnergy(player) > energyConsumption) {
                    ElectricItemUtils.drainPlayerEnergy(player, energyConsumption);
                    double explosiveness = ModuleManager.INSTANCE.computeModularProperty(itemStack, PlasmaCannonModule.PLASMA_CANNON_EXPLOSIVENESS);
                    double damagingness = ModuleManager.INSTANCE.computeModularProperty(itemStack, PlasmaCannonModule.PLASMA_CANNON_DAMAGE_AT_FULL_CHARGE);

                    EntityPlasmaBolt plasmaBolt = new EntityPlasmaBolt(worldIn, player, explosiveness, damagingness, chargeTicks);
                    worldIn.spawnEntity(plasmaBolt);
                    // switched to IEntityAdditionalSpawnData
                    //MusePacketPlasmaBolt packet = new MusePacketPlasmaBolt(player, plasmaBolt.getEntityId(), plasmaBolt.size);
                    //PacketSender.sendToAll(packet);
                }
            }
        }
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.plasmaCannon;
    }
}