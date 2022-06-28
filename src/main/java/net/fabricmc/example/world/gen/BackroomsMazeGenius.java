package net.fabricmc.example.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;

import java.security.SecureRandom;

/*

This class is responsible for the generation of the walls and rooms.
Hallways are bsp tree partition lines and rooms are leaves.
To find the block at a specific pos, a tree lookup will occur and the tree will be generated more if needed.

 */
public class BackroomsMazeGenius
{
	private static BackroomsMazeGenius instance = null;
	private static final SecureRandom rand = new SecureRandom();
	private PartitionHallway root;
	private static long worldSeed;
	
	private BackroomsMazeGenius() {}
	
	public static BackroomsMazeGenius getInstance()
	{
		if(instance == null) {
			instance = new BackroomsMazeGenius();
		}
		return instance;
	}
	
	public void setWorldSeed(long worldSeed)
	{
		BackroomsMazeGenius.worldSeed = worldSeed;
	}
	
	public BlockState getBlockStateAt(Vec3i pos) {
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
	
	private static void setSeed(long seed) {
		rand.setSeed(worldSeed + seed);
	}
	
	// Use a normal distribution to generate numbers that are more likely to be average (so rooms are more likely to
	// be square-ish rather than very long and skinny)
	private static int randomNormal(int min, int max)
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
	
	boolean isBisectable(int x1, int y1, int x2, int y2)
	{
		assert (x1 < x2);
		assert (y1 < y2);
		
		return (x2 - x1) > 10 || (y2 - y1) > 10;
	}
	
	public static class Area
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
		
		long getSeedFromState() {
			return ((long) (x1+y1))<<32 + ((long) (x2+y2));
		}
		
		void setSeedFromState() {
			long seed = getSeedFromState();
			setSeed(seed);
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
		
		
	}
	
	private static class PartitionHallway extends Area
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
	
	public static abstract class Room extends Area {
		
		protected static final BlockState WALL = Registry.BLOCK.get(new Identifier("backrooms:wallpaper")).getDefaultState();
		protected static final BlockState AIR = Registry.BLOCK.get(new Identifier("minecraft:air")).getDefaultState();
		public Room(int x1, int y1, int x2, int y2)
		{
			super(x1, y1, x2, y2);
		}
		
		Vec3i getRelativePos(Vec3i pos) {
			
			return pos.add(-x1, 0, -y1);
		}
		
		public BlockState getBlockAt(Vec3i pos) {
			if(!generated) {
				generate();
				generated = true;
			}
			
			return getBlockAtRelative(getRelativePos(pos));
		}
		
		protected abstract BlockState getBlockAtRelative(Vec3i pos);
		
		boolean generated = false;
		protected abstract void generate();
		
	}
	
	private static class Hallway extends Room
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
	
	public static class WalledRoom extends Room
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
					} else {
						blockStates[y][x] = AIR;
					}
				}
			}
		}
		
	}
	
	public static class WalledDooredRoom extends WalledRoom {

		public WalledDooredRoom(int x1, int y1, int x2, int y2)
		{
			super(x1, y1, x2, y2);
		}
		
		protected void generate() {
			
			super.generate();
			
			// Randomly add doors
			setSeedFromState();
			
			// "Discover" adjacent rooms
			final BackroomsMazeGenius bmg = BackroomsMazeGenius.getInstance();
			
			// Left
			Room adjacent = bmg.getRoomAt(new Vec3i(x1-1, 0, y1));
			boolean replaceStart = false;
			do {
				if(rand.nextInt(5) <= 2) {
					int start = Math.max(0, adjacent.y1-y1) + (replaceStart ? 0 : 1);
					int end = Math.min(getHeight(), adjacent.y2-y1);
					for(int y = start; y < end; y++) {
						blockStates[y][0] = AIR;
					}
					replaceStart = true;
				} else {
					replaceStart = false;
				}
				
				if(adjacent.y2 >= y2) break;

				adjacent = bmg.getRoomAt(new Vec3i(x1 - 1, 0, adjacent.y2));
				
			} while (true);
			
			// Down
			adjacent = bmg.getRoomAt(new Vec3i(x1, 0, y1-1));
			replaceStart = false;
			do {
				if(rand.nextInt(5) <= 2) {
					int start = Math.max(0, adjacent.x1-x1) + (replaceStart ? 0 : 1);
					int end = Math.min(getWidth(), adjacent.x2-x1);
					for(int x = start; x < end; x++) {
						blockStates[0][x] = AIR;
					}
					replaceStart = true;
				} else {
					replaceStart = false;
				}
				
				if(adjacent.x2 >= x2) break;
				
				adjacent = bmg.getRoomAt(new Vec3i(adjacent.x2, 0, y1-1));
				
			} while (true);
			
			// Remove the corner if it is by itself
			if(blockStates[1][0] == AIR && blockStates[0][1] == AIR) blockStates[0][0] = AIR;
			
		}
	}
}
