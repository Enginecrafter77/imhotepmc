package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.SavedBlockState;
import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockRecordCompatTranslationTable implements BlueprintTranslation {
	private static final Pattern RL_BLOCK_SIGN_PATTERN = Pattern.compile("minecraft:([a-z_]+)_sign");

	@Nullable
	@Override
	public SavedTileState translate(BlueprintTranslationContext ctx, BlockPos pos, SavedTileState state)
	{
		SavedBlockState savedBlock = state.getSavedBlockState();
		ResourceLocation name = savedBlock.getBlockName();

		Matcher signMatcher = RL_BLOCK_SIGN_PATTERN.matcher(name.toString());
		if(signMatcher.matches())
		{
			String type = signMatcher.group(1);
			boolean isWall = type.endsWith("_wall");
			return translateName(isWall ? Blocks.WALL_SIGN : Blocks.STANDING_SIGN, state);
		}

		if(name.getPath().startsWith("oak_"))
		{
			name = new ResourceLocation(name.getNamespace(), name.getPath().replace("oak_", ""));
			Block oakStripped = Block.REGISTRY.getObject(name);
			return translateName(oakStripped, state);
		}

		return state;
	}

	protected SavedTileState translateName(Block to, SavedTileState orig)
	{
		return new SavedTileState(SavedBlockState.ofBlock(to).withProperties(orig.getSavedBlockState().getBlockProperties()), orig.getTileEntity());
	}

	private static final BlockRecordCompatTranslationTable INSTANCE = new BlockRecordCompatTranslationTable();
	public static BlockRecordCompatTranslationTable getInstance()
	{
		return INSTANCE;
	}
}
