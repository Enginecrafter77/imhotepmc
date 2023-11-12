package dev.enginecrafter77.imhotepmc.blueprint.translate;

import dev.enginecrafter77.imhotepmc.blueprint.SavedBlockState;
import dev.enginecrafter77.imhotepmc.blueprint.SavedTileState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHugeMushroom;
import net.minecraft.block.BlockStone;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockRecordCompatTranslationTable implements BlueprintTranslation {
	private static final Pattern RL_BLOCK_SIGN_PATTERN = Pattern.compile("minecraft:([a-z_]+)_sign");

	private static final ResourceLocation NAME_DIORITE = new ResourceLocation("minecraft:diorite");
	private static final ResourceLocation NAME_DIORITE_POL = new ResourceLocation("minecraft:polished_diorite");
	private static final ResourceLocation NAME_ANDESITE = new ResourceLocation("minecraft:andesite");
	private static final ResourceLocation NAME_ANDESITE_POL = new ResourceLocation("minecraft:polished_andesite");
	private static final ResourceLocation NAME_GRANITE = new ResourceLocation("minecraft:granite");
	private static final ResourceLocation NAME_GRANITE_POL = new ResourceLocation("minecraft:polished_granite");
	private static final ResourceLocation NAME_MUSHROOM_STEM = new ResourceLocation("minecraft:mushroom_stem");

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

		if(Objects.equals(name, NAME_DIORITE))
			return translateName(Blocks.STONE, state).withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);

		if(Objects.equals(name, NAME_DIORITE_POL))
			return translateName(Blocks.STONE, state).withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);

		if(Objects.equals(name, NAME_ANDESITE))
			return translateName(Blocks.STONE, state).withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE);

		if(Objects.equals(name, NAME_ANDESITE_POL))
			return translateName(Blocks.STONE, state).withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH);

		if(Objects.equals(name, NAME_GRANITE))
			return translateName(Blocks.STONE, state).withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE);

		if(Objects.equals(name, NAME_GRANITE_POL))
			return translateName(Blocks.STONE, state).withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);

		if(Objects.equals(name, NAME_MUSHROOM_STEM))
			return translateName(Blocks.BROWN_MUSHROOM_BLOCK, state).withProperty(BlockHugeMushroom.VARIANT, BlockHugeMushroom.EnumType.ALL_STEM);

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
