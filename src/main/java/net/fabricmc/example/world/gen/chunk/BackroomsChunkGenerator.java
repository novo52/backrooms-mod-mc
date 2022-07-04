package net.fabricmc.example.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.example.world.biome.ModBiomes;
import net.fabricmc.example.world.dimension.ModDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.FixedBiomeSource;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.Blender;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import qouteall.imm_ptl.core.portal.Portal;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BackroomsChunkGenerator extends ChunkGenerator
{
	public static final Codec<BackroomsChunkGenerator> CODEC = RecordCodecBuilder.create((instance) ->
			method_41042(instance).and(
							RegistryOps.createRegistryCodec(Registry.BIOME_KEY).forGetter((generator) -> generator.biomeRegistry)
					)
					.apply(instance, instance.stable(BackroomsChunkGenerator::new))
	);
	private static final SecureRandom rand = new SecureRandom();
	private final Registry<Biome> biomeRegistry;
	private PartitionHallway root;
	private long worldSeed;
	
	public BackroomsChunkGenerator(Registry<StructureSet> registry, Registry<Biome> biomeRegistry)
	{
		super(registry, Optional.empty(), new FixedBiomeSource(biomeRegistry.getOrCreateEntry(ModBiomes.getLevel1())));
		this.biomeRegistry = biomeRegistry;
	}
	
	@Override
	protected Codec<? extends ChunkGenerator> getCodec()
	{
		return CODEC;
	}
	
	@Override
	public ChunkGenerator withSeed(long seed)
	{
		this.worldSeed = seed;
		return this;
	}
	
	@Override
	public MultiNoiseUtil.MultiNoiseSampler getMultiNoiseSampler()
	{
		// Mirror what Vanilla does in the debug chunk generator
		return MultiNoiseUtil.method_40443();
	}
	
	@Override
	public void carve(ChunkRegion chunkRegion, long l, BiomeAccess biomeAccess, StructureAccessor structureAccessor,
					  Chunk chunk, GenerationStep.Carver carver)
	{
	}
	
	@Override
	public void generateFeatures(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor)
	{
		// spawn portals
		for(int k = 0; k < 16; ++k) {
			for(int l = 0; l < 16; ++l) {
				int x = chunk.getPos().getOffsetX(k);
				int z = chunk.getPos().getOffsetZ(l);
				Vec3i pos = new Vec3i(x, 0, z);
				WalledDooredRoom innerRoom = (WalledDooredRoom) getRoomAt(pos);
				
				if(innerRoom.portalsSpawned) continue;
				innerRoom.portalsSpawned = true;
				
				if(innerRoom.portalLocations.size() == 0) continue;
				
				Area area = innerRoom.portalLocations.get(0);
				createShortcutPortalsFromArea(world.toServerWorld(), area);
				
				
			}
		}
	}
	
	@Override
	public void buildSurface(ChunkRegion region, StructureAccessor structureAccessor, Chunk chunk)
	{
	}
	
	@Override
	public void populateEntities(ChunkRegion region)
	{
	
	}
	
	public int getSpawnHeight(HeightLimitView world)
	{
		return 1;
	}
	
	@Override
	public int getWorldHeight()
	{
		return 16;
	}
	
	@Override
	public void setStructureStarts(DynamicRegistryManager registryManager, StructureAccessor world, Chunk chunk,
								   StructureManager structureManager, long worldSeed)
	{
	
	}
	
	@Override
	public CompletableFuture<Chunk> populateNoise(Executor executor, Blender blender,
												  StructureAccessor structureAccessor, Chunk chunk)
	{
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Heightmap oceanFloorHeightmap = chunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
		Heightmap worldSurfaceHeightmap = chunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);
		
		BlockState floorState = Registry.BLOCK.get(new Identifier("backrooms:moldy_carpet")).getDefaultState();
		int floorHeight = 0;
		
		BlockState ceilTileState = Registry.BLOCK.get(new Identifier("backrooms:ceiling_tile")).getDefaultState();
		BlockState ceilLightState = Registry.BLOCK.get(new Identifier("backrooms:ceiling_light")).getDefaultState();
		int ceilHeight = 5;
		
		BlockState innerRoomState;
		
		for(int k = 0; k < 16; ++k) {
			for(int l = 0; l < 16; ++l) {
				// Floor
				chunk.setBlockState(mutable.set(k, floorHeight, l), floorState, false);
				oceanFloorHeightmap.trackUpdate(k, floorHeight, l, floorState);
				worldSurfaceHeightmap.trackUpdate(k, floorHeight, l, floorState);
				
				// Ceiling
				if(k % 4 == 0 && (l & 2) != 0) { // Placeholder for lights pattern on the ceiling
					chunk.setBlockState(mutable.set(k, ceilHeight, l), ceilLightState, false);
				}
				else {
					chunk.setBlockState(mutable.set(k, ceilHeight, l), ceilTileState, false);
				}
				
				// Walls
				innerRoomState = getBlockStateAt(chunk.getPos().getBlockPos(k, 0, l));
				if(!innerRoomState.isAir()) {
					for(int i = 1; i < ceilHeight; i++) {
						chunk.setBlockState(mutable.set(k, i, l), innerRoomState, false);
					}
				}
			}
		}
		
		return CompletableFuture.completedFuture(chunk);
	}
	
	@Override
	public int getSeaLevel()
	{
		return 0;
	}
	
	@Override
	public int getMinimumY()
	{
		return 0;
	}
	
	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmap, HeightLimitView world)
	{
		return 1;
	}
	
	@Override
	public VerticalBlockSample getColumnSample(int x, int z, HeightLimitView heightLimitView)
	{
		return new VerticalBlockSample(heightLimitView.getBottomY(),
				new BlockState[]{Registry.BLOCK.get(new Identifier("minecraft:red_stained_glass")).getDefaultState()});
	}
	
	@Override
	public void getDebugHudText(List<String> list, BlockPos blockPos)
	{
	}

	public BlockState getBlockStateAt(Vec3i pos)
	{
		return getRoomAt(pos).getBlockAt(pos);
	}

	// Returns the room object at a position
	public Room getRoomAt(Vec3i pos)
	{
		
		// create root if needed
		if(root == null) {
			final int worldBorder = 32767;
			root = new PartitionHallway(-worldBorder, -worldBorder, worldBorder, worldBorder, rand.nextBoolean());
		}
		
		// start at root and iterate thru the tree, creating partitions if needed
		PartitionHallway current = root;
		
		do {
			
			int x1 = current.x1;
			int y1 = current.y1;
			int x2 = current.x2;
			int y2 = current.y2;
			
			if(current.isVertical) {
				if(pos.getX() < current.hallway.x1) { // Left
					if(current.back == null) {
						x2 = current.hallway.x1;
						if(isBisectable(x1, y1, x2, y2)) {
							current.back = new PartitionHallway(x1, y1, x2, y2);
						}
						else {
							current.back = new WalledDooredRoom(x1, y1, x2, y2);
							return (Room) current.back;
						}
					}
					if(current.back.getClass() == WalledDooredRoom.class) return (Room) current.back;
					current = (PartitionHallway) current.back;
				}
				else if(pos.getX() >= current.hallway.x2) { // Right
					if(current.front == null) {
						x1 = current.hallway.x2;
						if(isBisectable(x1, y1, x2, y2)) {
							current.front = new PartitionHallway(x1, y1, x2, y2);
						}
						else {
							current.front = new WalledDooredRoom(x1, y1, x2, y2);
							return (Room) current.front;
						}
					}
					if(current.front.getClass() == WalledDooredRoom.class) return (Room) current.front;
					current = (PartitionHallway) current.front;
				}
				else {
					return current.hallway; // If it is not on either side, it must be inside it
				}
			}
			else {
				if(pos.getZ() < current.hallway.y1) { // Down
					if(current.back == null) {
						y2 = current.hallway.y1;
						if(isBisectable(x1, y1, x2, y2)) {
							current.back = new PartitionHallway(x1, y1, x2, y2);
						}
						else {
							current.back = new WalledDooredRoom(x1, y1, x2, y2);
							return (Room) current.back;
						}
					}
					if(current.back.getClass() == WalledDooredRoom.class) return (Room) current.back;
					current = (PartitionHallway) current.back;
				}
				else if(pos.getZ() >= current.hallway.y2) { // Up
					if(current.front == null) {
						y1 = current.hallway.y2;
						if(isBisectable(x1, y1, x2, y2)) {
							current.front = new PartitionHallway(x1, y1, x2, y2);
						}
						else {
							current.front = new WalledDooredRoom(x1, y1, x2, y2);
							return (Room) current.front;
						}
					}
					if(current.front.getClass() == WalledDooredRoom.class) return (Room) current.front;
					current = (PartitionHallway) current.front;
				}
				else {
					return current.hallway; // If it is not on either side, it must be inside it
				}
			}
		} while(true);
	}
	
	private void createShortcutPortalsFromArea(World world, Area area)
	{
		
		final Vec3d xUnit = new Vec3d(1, 0, 0);
		final Vec3d yUnit = new Vec3d(0, 1, 0);
		final Vec3d zUnit = new Vec3d(0, 0, 1);
		
		// Z-Axis aligned
		if(area.getWidth() == 0) {
			
			final Vec3d pos = area.getCentre().add(0.25, 2.5, 0);
			createPortal(world, pos, pos.add(0.5, 0, 0), zUnit, yUnit, area.getHeight(), 5);
			
			final Vec3d pos1 = area.getCentre().add(0.75, 2.5, 0);
			createPortal(world, pos1, pos1.add(-0.5, 0, 0), zUnit.multiply(-1), yUnit, area.getHeight(), 5);
		}
		
		// X-Axis aligned
		else if(area.getHeight() == 0) {
			final Vec3d pos = area.getCentre().add(0, 2.5, 0.25);
			createPortal(world, pos, pos.add(0, 0, 0.5), xUnit.multiply(-1), yUnit, area.getWidth(), 5);
			
			final Vec3d pos1 = area.getCentre().add(0, 2.5, 0.75);
			createPortal(world, pos1, pos1.add(0, 0, -0.5), xUnit, yUnit, area.getWidth(), 5);
		}
	}
	
	boolean isBisectable(int x1, int y1, int x2, int y2)
	{
		assert (x1 < x2);
		assert (y1 < y2);
		
		return (x2 - x1) > 10 || (y2 - y1) > 10;
	}
	
	// implementing an O(nlog(n)) algorithm in O(n^5) then walking away and continuing development 🥰
//	public List<Room> getRoomsIn(Area area) {
//
//		ArrayList<Room> rooms = new ArrayList<>();
//
//		for(int y = area.y1; y < area.y2; y++) {
//			for(int x = area.x1; x < area.x2; /* 😳 */) {
//				Room room = getRoomAt(new Vec3i(x, 0, y));
//
//				if(!rooms.contains(room)) rooms.add(room);
//
//				// Skip to the x coord of the next room
//				if(room.x2 < area.x2) {
//					x = room.x2-1;
//				} else {
//					x = area.x2-1;
//				}
//			}
//		}
//
//		return rooms;
//	}
	
	private Portal createPortal(World world, Vec3d originPos, Vec3d destinationPos, Vec3d axisW, Vec3d axisH,
								double width, double height)
	{
		Portal portal = Portal.entityType.create(world);
		assert (portal != null);
		portal.setOriginPos(originPos);
		portal.setDestinationDimension(ModDimensions.ROOMS);
		portal.setDestination(destinationPos);
		portal.setOrientationAndSize(axisW, axisH, width, height);
		portal.world.spawnEntity(portal);
		
		return portal;
	}
	
	private void setSeed(long seed)
	{
		rand.setSeed(worldSeed + seed);
	}
	
	// Use a normal distribution to generate numbers that are more likely to be average (so rooms are more likely to
	// be square-ish rather than very long and skinny)
	private int randomNormal(int min, int max)
	{
		
		assert (min <= max);
		
		final double wackinessFactor = 2; // Lower values make more uniform rooms, higher values make wackier rooms
		
		int difference = max - min;
		int average = (min + max) / 2;
		double gaussian = rand.nextGaussian();
		int scaledGaussian = (int) ((gaussian * difference * 0.15d * wackinessFactor) + average);
		if(scaledGaussian > max || scaledGaussian < min) scaledGaussian = average;
		return scaledGaussian;
	}
	
	public class Area
	{
		public final int x1;
		public final int y1;
		public final int x2;
		public final int y2;
		
		public Area(int x1, int y1, int x2, int y2)
		{
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			
			assert (this.x1 <= this.x2);
			assert (this.y1 <= this.y2);
		}
		
		void setSeedFromState()
		{
			long seed = getSeedFromState();
			setSeed(seed);
		}
		
		long getSeedFromState()
		{
			return ((long) (x1 + y1)) << 32 + ((long) (x2 + y2));
		}
		
		public int getArea()
		{
			return Math.abs((x1 - x2) * (y1 - y2));
		}
		
		public int getWidth()
		{
			return Math.abs(x1 - x2);
		}
		
		public int getHeight()
		{
			return Math.abs(y1 - y2);
		}
		
		public Vec3d getCentre()
		{
			return new Vec3d(((double) x1 + (double) x2) / 2.0d, 0, ((double) y1 + (double) y2) / 2.0d);
		}
		
		public boolean isInside(Vec3d pos)
		{
			return pos.getX() >= x1 && pos.getX() < x2 && pos.getY() >= y1 && pos.getY() < y2;
		}
		
		
	}
	
	private class PartitionHallway extends Area
	{
		
		Hallway hallway;
		// Could be another partition hallway or just a room
		Area front;
		Area back;
		boolean isVertical;
		
		public PartitionHallway(int x1, int y1, int x2, int y2)
		{
			this(x1, y1, x2, y2, (x2 - x1) > (y2 - y1));
		}
		
		public PartitionHallway(int x1, int y1, int x2, int y2, boolean isVertical)
		{
			super(x1, y1, x2, y2);
			
			setSeedFromState();
			
			if(isVertical) {
				int centre = randomNormal(x1 + 5, x2 - 5);
				hallway = new Hallway(centre, y1, centre, y2);
			}
			else {
				int centre = randomNormal(y1 + 5, y2 - 5);
				hallway = new Hallway(x1, centre, x2, centre);
			}
			
			this.isVertical = isVertical;
		}
		
		long getSeedFromState()
		{
			return (super.getSeedFromState() << 1) + (isVertical ? 1 : 0);
		}
		
	}
	
	public abstract class Room extends Area
	{
		
		protected static final BlockState WALL =
				Registry.BLOCK.get(new Identifier("backrooms:wallpaper")).getDefaultState();
		protected static final BlockState AIR = Registry.BLOCK.get(new Identifier("minecraft:air")).getDefaultState();
		boolean generated = false;
		
		public Room(int x1, int y1, int x2, int y2)
		{
			super(x1, y1, x2, y2);
		}
		
		public BlockState getBlockAt(Vec3i pos)
		{
			if(!generated) {
				generate();
				generated = true;
			}
			
			return getBlockAtRelative(getRelativePos(pos));
		}
		
		protected abstract void generate();
		
		protected abstract BlockState getBlockAtRelative(Vec3i pos);
		
		Vec3i getRelativePos(Vec3i pos)
		{
			
			return pos.add(-x1, 0, -y1);
		}
		
	}
	
	private class Hallway extends Room
	{
		public Hallway(int x1, int y1, int x2, int y2)
		{
			super(x1, y1, x2, y2);
		}
		
		@Override
		public BlockState getBlockAt(Vec3i pos)
		{
			assert (pos.getX() >= x1 && pos.getX() < x2);
			assert (pos.getZ() >= y1 && pos.getZ() < y2);
			
			return AIR;
		}
		
		@Override
		protected BlockState getBlockAtRelative(Vec3i pos)
		{
			return null;
		}
		
		@Override
		protected void generate()
		{}
		
	}
	
	public class WalledRoom extends Room
	{
		BlockState[][] blockStates;
		
		public WalledRoom(int x1, int y1, int x2, int y2)
		{
			super(x1, y1, x2, y2);
			
		}
		
		@Override
		protected BlockState getBlockAtRelative(Vec3i pos)
		{
			assert (pos.getX() >= 0 && pos.getX() < getWidth());
			assert (pos.getZ() >= 0 && pos.getZ() < getHeight());
			
			return blockStates[pos.getZ()][pos.getX()];
		}
		
		@Override
		protected void generate()
		{
			blockStates = new BlockState[getHeight()][getWidth()];
			
			for(int y = 0; y < blockStates.length; y++) {
				for(int x = 0; x < blockStates[0].length; x++) {
					
					if(x == 0 || y == 0) { //|| x == blockStates[0].length-1 || y == blockStates.length-1) {
						blockStates[y][x] = WALL;
					}
					else {
						blockStates[y][x] = AIR;
					}
				}
			}
		}
		
	}
	
	public class WalledDooredRoom extends WalledRoom
	{
		
		public final List<Area> portalLocations;
		public boolean portalsSpawned = false;
		
		public WalledDooredRoom(int x1, int y1, int x2, int y2)
		{
			super(x1, y1, x2, y2);
			portalLocations = new ArrayList<>();
		}
		
		protected void generate()
		{
			
			super.generate();
			
			// Randomly add doors
			setSeedFromState();
			
			// Left
			Room adjacent = getRoomAt(new Vec3i(x1 - 1, 0, y1));
			boolean replaceStart = false;
			do {
				if(rand.nextInt(5) <= 2) {
					int start = Math.max(0, adjacent.y1 - y1) + (replaceStart ? 0 : 1);
					int end = Math.min(getHeight(), adjacent.y2 - y1);
					for(int y = start; y < end; y++) {
						blockStates[y][0] = AIR;
					}
					
					// place portal
					if(end - start > 2 && rand.nextInt(10) == 4) {
						portalLocations.add(new Area(x1, y1 + start + 1, x1, y1 + end - 1));
						
						blockStates[start][0] = WALL;
						blockStates[end - 1][0] = WALL;
						
					}
					
					replaceStart = true;
				}
				else {
					replaceStart = false;
				}
				
				if(adjacent.y2 >= y2) break;
				
				adjacent = getRoomAt(new Vec3i(x1 - 1, 0, adjacent.y2));
				
			} while(true);
			
			// Down
			adjacent = getRoomAt(new Vec3i(x1, 0, y1 - 1));
			replaceStart = false;
			do {
				if(rand.nextInt(5) <= 2) {
					int start = Math.max(0, adjacent.x1 - x1) + (replaceStart ? 0 : 1);
					int end = Math.min(getWidth(), adjacent.x2 - x1);
					for(int x = start; x < end; x++) {
						blockStates[0][x] = AIR;
					}
					
					if(end - start > 2 && rand.nextInt(10) == 4) {
						portalLocations.add(new Area(x1 + start + 1, y1, x1 + end - 1, y1));
						
						blockStates[0][start] = WALL;
						blockStates[0][end - 1] = WALL;
						
					}
					
					replaceStart = true;
				}
				else {
					replaceStart = false;
				}
				
				if(adjacent.x2 >= x2) break;
				
				adjacent = getRoomAt(new Vec3i(adjacent.x2, 0, y1 - 1));
				
			} while(true);
			
			// Remove the corner if it is by itself
			if(blockStates[1][0] == AIR && blockStates[0][1] == AIR) blockStates[0][0] = AIR;
			
		}
	}
}

