package carpet.commands;

import carpet.CarpetSettings;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import carpet.utils.CommandHelper;
import carpet.utils.Messenger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class PathfindCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> command = literal("pathfind")
                .requires((player) -> CommandHelper.canUseCommand(player, CarpetSettings.commandPlayer))
                .then(argument("player", StringArgumentType.word())
                        .suggests((c, b) -> {
                            // Suggest online player names
                            return net.minecraft.commands.SharedSuggestionProvider.suggest(
                                c.getSource().getOnlinePlayerNames(), b
                            );
                        })
                        .then(literal("to")
                                .then(argument("destination", BlockPosArgument.blockPos())
                                        .executes(PathfindCommand::pathfind)
                                        .then(literal("with")
                                                .then(literal("max_distance")
                                                        .then(argument("max_dist", IntegerArgumentType.integer(1, 1000))
                                                                .executes(PathfindCommand::pathfindWithMaxDistance)))))));
        dispatcher.register(command);
    }

    private static int pathfind(CommandContext<CommandSourceStack> context)
    {
        return pathfindWithMaxDistance(context, 256);
    }

    private static int pathfindWithMaxDistance(CommandContext<CommandSourceStack> context)
    {
        int maxDistance = IntegerArgumentType.getInteger(context, "max_dist");
        return pathfindWithMaxDistance(context, maxDistance);
    }

    private static int pathfindWithMaxDistance(CommandContext<CommandSourceStack> context, int maxDistance)
    {
        String playerName = StringArgumentType.getString(context, "player");
        CommandSourceStack source = context.getSource();
        
        // Support 's' for self like in PlayerCommand
        ServerPlayer player;
        if (playerName.equals("s") && source.isPlayer())
        {
            player = source.getPlayer();
        }
        else
        {
            player = source.getServer().getPlayerList().getPlayerByName(playerName);
        }

        if (player == null)
        {
            Messenger.m(source, "r Player not found: " + playerName);
            return 0;
        }

        if (!(player instanceof EntityPlayerMPFake))
        {
            Messenger.m(source, "r Pathfinding only works for fake players");
            Messenger.m(source, "r Current player type: " + player.getClass().getSimpleName());
            return 0;
        }

        try
        {
            BlockPos destination = BlockPosArgument.getBlockPos(context, "destination");
            BlockPos start = player.blockPosition();
            
            double distance = Math.sqrt(start.distSqr(destination));

            Messenger.m(source, "g Bot will walk from " + start + " to " + destination);
            Messenger.m(source, "g Distance: " + String.format("%.1f", distance) + " blocks");

            // Get action pack
            EntityPlayerActionPack ap = ((ServerPlayerInterface) player).getActionPack();
            
            // Stop all actions first
            ap.stopAll();
            
            // Look at the target
            Vec3 targetVec = Vec3.atBottomCenterOf(destination);
            ap.lookAt(targetVec);
            
            // Set sprint and forward movement
            ap.setSprinting(true);
            ap.setForward(1);

            Messenger.m(source, "g Bot walking to: X=" + destination.getX() + ", Y=" + destination.getY() + ", Z=" + destination.getZ());
            Messenger.m(source, "y Use '/player " + playerName + " stop' to stop");

            return 1;
        }
        catch (Exception e)
        {
            Messenger.m(context.getSource(), "r Error during pathfinding: " + e.getMessage());
            return 0;
        }
    }

    private static List<BlockPos> findPath(Level level, BlockPos start, BlockPos goal, int maxDistance)
    {
        // A* pathfinding implementation with optimization
        PriorityQueue<PathNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<BlockPos> closedSet = new HashSet<>();
        Map<BlockPos, PathNode> allNodes = new HashMap<>();

        PathNode startNode = new PathNode(start, 0, heuristic(start, goal), null);
        openSet.add(startNode);
        allNodes.put(start, startNode);
        
        int maxIterations = 1000; // Prevent infinite loops and lag
        int iterations = 0;

        while (!openSet.isEmpty() && iterations < maxIterations)
        {
            iterations++;
            PathNode current = openSet.poll();

            // Check if we reached the goal or close enough
            if (current.pos.equals(goal) || current.pos.distSqr(goal) <= 2)
            {
                // Reconstruct path
                return reconstructPath(current);
            }

            closedSet.add(current.pos);

            // Check distance limit
            if (current.gScore > maxDistance)
            {
                continue;
            }

            // Explore neighbors (reduced set for performance)
            for (BlockPos neighbor : getNeighborsSimple(current.pos))
            {
                if (closedSet.contains(neighbor))
                {
                    continue;
                }

                // Skip validation for performance - trust the heuristic
                double tentativeGScore = current.gScore + current.pos.distManhattan(neighbor);

                PathNode neighborNode = allNodes.get(neighbor);
                if (neighborNode == null)
                {
                    neighborNode = new PathNode(neighbor, Double.POSITIVE_INFINITY, 0, null);
                    allNodes.put(neighbor, neighborNode);
                }

                if (tentativeGScore < neighborNode.gScore)
                {
                    neighborNode.parent = current;
                    neighborNode.gScore = tentativeGScore;
                    neighborNode.fScore = tentativeGScore + heuristic(neighbor, goal);

                    if (!openSet.contains(neighborNode))
                    {
                        openSet.add(neighborNode);
                    }
                }
            }
        }

        return null; // No path found
    }

    private static double heuristic(BlockPos a, BlockPos b)
    {
        // Euclidean distance heuristic (optimized)
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        int dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private static List<BlockPos> getNeighborsSimple(BlockPos pos)
    {
        // Simplified neighbor list for better performance
        List<BlockPos> neighbors = new ArrayList<>(6);
        
        // Only cardinal directions and vertical
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        neighbors.add(pos.above());
        neighbors.add(pos.below());

        return neighbors;
    }
    
    private static List<BlockPos> getNeighbors(BlockPos pos)
    {
        List<BlockPos> neighbors = new ArrayList<>();
        
        // Cardinal directions (horizontal movement)
        neighbors.add(pos.north());
        neighbors.add(pos.south());
        neighbors.add(pos.east());
        neighbors.add(pos.west());
        
        // Vertical movement
        neighbors.add(pos.above());
        neighbors.add(pos.below());
        
        // Diagonal movements on same level
        neighbors.add(pos.north().east());
        neighbors.add(pos.north().west());
        neighbors.add(pos.south().east());
        neighbors.add(pos.south().west());
        
        // Add stepping up/down diagonally for better pathfinding
        neighbors.add(pos.north().above());
        neighbors.add(pos.south().above());
        neighbors.add(pos.east().above());
        neighbors.add(pos.west().above());

        return neighbors;
    }

    private static boolean isWalkable(Level level, BlockPos pos)
    {
        // Check if the position is walkable
        BlockState blockState = level.getBlockState(pos);
        BlockState blockBelow = level.getBlockState(pos.below());
        BlockState above = level.getBlockState(pos.above());
        BlockState twoAbove = level.getBlockState(pos.above(2));

        // Check multiple scenarios for walkability
        
        // Scenario 1: Standing ON a solid block (feet on top of block)
        if (blockState.isAir() && blockBelow.isSolid())
        {
            // Space for player body (2 blocks tall)
            if (above.isAir() || !above.isSolid())
            {
                return true;
            }
        }
        
        // Scenario 2: Standing AT this position (block is passable)
        if (!blockState.isSolid() && !blockBelow.isAir())
        {
            // Has something under to stand on, and headroom clear
            if ((above.isAir() || !above.isSolid()) && (twoAbove.isAir() || !twoAbove.isSolid()))
            {
                return true;
            }
        }

        return false;
    }

    private static List<BlockPos> reconstructPath(PathNode endNode)
    {
        List<BlockPos> path = new ArrayList<>();
        PathNode current = endNode;

        while (current != null)
        {
            path.add(0, current.pos);
            current = current.parent;
        }

        return path;
    }

    private static void executePath(ServerPlayer player, List<BlockPos> path)
    {
        EntityPlayerActionPack ap = ((ServerPlayerInterface) player).getActionPack();
        
        // Stop all current actions first
        ap.stopAll();

        if (path.size() < 2)
        {
            return; // No path to execute
        }

        // DEBUG: Log the path
        BlockPos start = path.get(0);
        BlockPos end = path.get(path.size() - 1);
        
        // Look directly at the final destination for simplest navigation
        BlockPos destination = path.get(path.size() - 1);
        Vec3 targetVec = Vec3.atCenterOf(destination);
        
        // Look at destination first
        ap.lookAt(targetVec);
        
        // Then set movement - use the same pattern as PlayerCommand
        ap.setSprinting(true);
        ap.setForward(1);  // 1 = forward, -1 = backward
        
        // Note: Bot will walk straight toward destination
        // May get stuck on obstacles - use /player <name> stop to stop
    }

    private static class PathNode
    {
        BlockPos pos;
        double gScore; // Cost from start
        double fScore; // gScore + heuristic
        PathNode parent;

        PathNode(BlockPos pos, double gScore, double fScore, PathNode parent)
        {
            this.pos = pos;
            this.gScore = gScore;
            this.fScore = fScore;
            this.parent = parent;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (!(obj instanceof PathNode other)) return false;
            return pos.equals(other.pos);
        }

        @Override
        public int hashCode()
        {
            return pos.hashCode();
        }
    }
}
