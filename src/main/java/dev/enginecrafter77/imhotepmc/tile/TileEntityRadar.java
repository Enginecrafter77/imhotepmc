package dev.enginecrafter77.imhotepmc.tile;

import cofh.api.tileentity.ITileInfo;
import dev.enginecrafter77.imhotepmc.radar.CapabilityRadarHandler;
import dev.enginecrafter77.imhotepmc.radar.RadarHandler;
import dev.enginecrafter77.imhotepmc.util.transaction.EnergyConsumeTransaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.Optional;

import javax.annotation.Nullable;
import java.util.List;

@Optional.Interface(iface = "cofh.api.tileentity.ITileInfo", modid = "cofhcore")
public class TileEntityRadar extends TileEntity implements ITickable, ITileInfo {
	private final EnergyStorage battery;

	private final EnergyConsumeTransaction pingConsumeTransaction;

	private int pingedBlocks;

	public TileEntityRadar()
	{
		this.battery = new EnergyStorage(16000, 1000, 1000);
		this.pingConsumeTransaction = new EnergyConsumeTransaction(this.battery, 100);
		this.pingedBlocks = 0;
	}

	@Override
	public void update()
	{
		if(this.world.isRemote)
			return;

		RadarHandler handler = this.world.getCapability(CapabilityRadarHandler.RADAR, null);
		if(handler == null)
			return;

		if(!this.pingConsumeTransaction.tryCommit())
		{
			this.pingedBlocks = 0;
			return;
		}

		this.pingedBlocks = handler.ping(this.pos.up()).size();
	}

	@Override
	public void getTileInfo(List<ITextComponent> info, EnumFacing side, EntityPlayer player, boolean debug)
	{
		info.add(new TextComponentTranslation("info.cave_filler.scanned_blocks", this.pingedBlocks));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
	{
		return capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
	}

	@Override
	@Nullable
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
	{
		if(capability == CapabilityEnergy.ENERGY)
			return CapabilityEnergy.ENERGY.cast(this.battery);
		return super.getCapability(capability, facing);
	}
}
