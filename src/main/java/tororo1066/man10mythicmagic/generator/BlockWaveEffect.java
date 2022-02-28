package tororo1066.man10mythicmagic.generator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;
import tororo1066.man10mythicmagic.Man10MythicMagic;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BlockWaveEffect {

    protected Double velocity;




    protected int radius;

    protected int radiusY;

    protected int duration;

    protected double randomness;

    protected boolean sphere;

    protected Material material;

    protected boolean ignoreAir;

    private int radiusSq;
    public BlockWaveEffect(Double velocity, int radius, int radiusY, int duration, Material material,Location location){
        this.velocity = velocity;
        this.radius = radius;
        this.radiusY = radiusY;
        this.duration = duration;
        this.randomness = 0.0;
        this.sphere = true;
        this.material = material;
        this.ignoreAir = true;
        this.radiusSq = radius * radius;
        castAtLocation(location);
    }

    public void castAtLocation(Location target) {
        double bv = this.velocity;
        double bvh = 0.0;
        for (Location l : getBlocksInRadius(target, true)) {
            double v = -(bv / 2.0D) + bv * ThreadLocalRandom.current().nextDouble();
            double vh = -(bvh / 2.0D) + bvh * ThreadLocalRandom.current().nextDouble();
            FallingBlock block = l.getWorld().spawnFallingBlock(l.add(0.0,0.0,0.0),material.createBlockData());
            block.setDropItem(false);
            block.setHurtEntities(false);
            block.setGravity(true);
            block.setVelocity(new Vector(vh, v, vh));
            Man10MythicMagic.Companion.getFlyingBlocks().add(block.getUniqueId());
            Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(l,Material.AIR.createBlockData()));
            Bukkit.getScheduler().runTaskLater(Man10MythicMagic.plugin, ()->{
                Bukkit.getOnlinePlayers().forEach(player -> player.sendBlockChange(l,l.getBlock().getBlockData()));
                block.remove();
                Man10MythicMagic.Companion.getFlyingBlocks().remove(block.getUniqueId());
            },duration);
        }

    }

    private List<Location> getBlocksInRadius(Location loc, boolean randomize) {
        List<Location> blocks = new ArrayList<>();
        for (int x = -this.radius; x <= this.radius; x++) {
            for (int z = -this.radius; z <= this.radius; z++) {
                for (int y = this.radiusY; y >= -this.radiusY; y--) {
                    Location newloc = new Location(loc.getWorld(), (loc.getBlockX() + x), (loc.getBlockY() + y), (loc.getBlockZ() + z));
                    if (!this.sphere ||
                            loc.distanceSquared(newloc) <= this.radiusSq)
                        if (!randomize || this.randomness <= 0.0D ||
                                this.randomness >= ThreadLocalRandom.current().nextDouble())
                            if (!this.ignoreAir || newloc.getBlock().getType() != Material.AIR) {
                                blocks.add(newloc);
                                break;
                            }
                }
            }
        }
        return blocks;
    }

}
