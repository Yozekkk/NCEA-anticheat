package com.ncea.anticheat.paper;

import com.ncea.anticheat.core.*;
import com.ncea.anticheat.core.branding.*;
import java.nio.charset.Charset;
import java.io.File;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class NceaPlugin extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {
    private final PlayerRegistry players = new PlayerRegistry();
    private boolean started;
    private final Map<UUID, Long> grace = new HashMap<>();
    private final Set<UUID> exempt = new HashSet<>();
    private final Set<UUID> alerts = new HashSet<>();
    private final CheckManager checks = BasicChecks.create();
    private Settings settings;
    private BrandingLoader brandingLoader;
    private BrandingLoader.Loaded branding;
    private String brandingValidation = "Not loaded";
    private long[] nanos = new long[4];
    private long[] calls = new long[4];

    @Override public void onEnable() {
        long startup = System.nanoTime();
        String version = Bukkit.getBukkitVersion().split("-")[0];
        if (!version.equals("1.20") && !version.equals("1.20.1")) {
            getLogger().severe("Unsupported Paper version " + version + "; requires 1.20 or 1.20.1");
            Bukkit.getPluginManager().disablePlugin(this); return;
        }
        boolean legacyConfig = new File(getDataFolder(), "config.yml").exists();
        saveDefaultConfig();
        if (!new File(getDataFolder(), "checks.yml").exists()) saveResource("checks.yml", false);
        if (!new File(getDataFolder(), "custom-banner.txt").exists()) saveResource("custom-banner.txt", false);
        settings = new Settings(getDataFolder(), getLogger());
        if (!settings.load()) { getLogger().severe("Invalid configuration; plugin disabled"); Bukkit.getPluginManager().disablePlugin(this); return; }
        brandingLoader = new BrandingLoader(getDataFolder(), getLogger(), () -> getResource("branding.yml"));
        try {
            branding = brandingLoader.load(legacyConfig, settings.banner());
            brandingValidation = "OK: branding.yml";
        } catch (Exception ex) {
            brandingValidation = "ERROR: " + ex.getMessage();
            getLogger().severe("Invalid branding configuration: " + ex.getMessage());
            Bukkit.getPluginManager().disablePlugin(this); return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("nceaac")).setExecutor(this);
        Objects.requireNonNull(getCommand("nceaac")).setTabCompleter(this);
        for (Player p : Bukkit.getOnlinePlayers()) players.get(p.getUniqueId());
        started = true;
        StartupBanner.Info info = new StartupBanner.Info(getDescription().getVersion(), version,
                Runtime.version().feature(), settings.activeChecks(), "OBSERVE",
                (System.nanoTime() - startup) / 1_000_000, StartupBanner.Status.ENABLED);
        logBanner(branding, info);
    }
    @Override public void onDisable() {
        players.clear(); grace.clear(); exempt.clear(); alerts.clear();
        if (started && branding != null && branding.settings().enabled() && branding.settings().shutdownEnabled()) {
            getLogger().info("NCEA AntiCheat v" + getDescription().getVersion() + " - Disabled");
            if (branding.settings().shutdownStyle() == BrandingSettings.ShutdownStyle.COMPACT)
                getLogger().info("Thank you for using NCEA AntiCheat.");
        }
        started = false;
    }
    private void logBanner(BrandingLoader.Loaded loaded, StartupBanner.Info info) {
        try {
            ConsoleCapabilities capabilities = ConsoleCapabilities.detect(System.console() != null,
                    System.getenv(), Charset.defaultCharset());
            for (String line : BannerRenderer.render(loaded.settings(), info, capabilities, loaded.customTemplate()))
                getLogger().info(line);
        } catch (RuntimeException | LinkageError ex) {
            getLogger().warning("Console branding fallback: " + ex.getMessage());
            getLogger().info("NCEA AntiCheat v" + info.version() + " | Paper " + info.paper() + " | Status " + info.status());
        }
    }
    private PlayerData data(Player p) { return players.get(p.getUniqueId()); }
    private boolean eligible(Player p) {
        return !exempt.contains(p.getUniqueId()) && !p.hasPermission("nceaac.bypass") && p.getGameMode() == GameMode.SURVIVAL
            && !p.getAllowFlight() && !p.isFlying() && !p.isInsideVehicle() && !p.isGliding() && !p.isSwimming()
            && !p.isRiptiding() && !p.hasPotionEffect(org.bukkit.potion.PotionEffectType.SPEED)
            && !p.hasPotionEffect(org.bukkit.potion.PotionEffectType.JUMP)
            && !p.hasPotionEffect(org.bukkit.potion.PotionEffectType.LEVITATION)
            && !p.hasPotionEffect(org.bukkit.potion.PotionEffectType.SLOW_FALLING)
            && p.getWalkSpeed() == 0.2f
            && System.currentTimeMillis() > grace.getOrDefault(p.getUniqueId(), 0L)
            && p.getPing() <= settings.maxPing && Bukkit.getTPS()[0] >= settings.minTps;
    }
    private boolean safeTerrain(Player p, Location loc) {
        Block feet = loc.getBlock();
        Block below = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        if (feet.isLiquid() || below.isLiquid()) return false;
        return !unsafeBlock(feet) && !unsafeBlock(below) && !p.isSneaking() && !p.isClimbing();
    }
    private boolean unsafeBlock(Block block) {
        String name = block.getType().name();
        return name.contains("LADDER") || name.contains("VINE") || name.contains("SCAFFOLD") || name.contains("SLIME")
            || name.contains("HONEY") || name.contains("WEB") || name.contains("ICE") || name.contains("SOUL")
            || name.contains("BUBBLE") || name.contains("STAIRS") || name.contains("SLAB") || name.contains("PISTON");
    }
    private void inspect(Player p, PlayerData d, Observation o) {
        long start = System.nanoTime();
        for (CheckManager.Flag flag : checks.inspect(d, o, id -> settings.rule(id).enabled())) {
            Settings.Rule rule = settings.rule(flag.check());
            Violation violation = d.violations.computeIfAbsent(flag.check(), ignored -> new Violation());
            double vl = violation.add(rule.weight(), rule.decay(), o.timeMillis(), flag.evidence());
            if (vl >= rule.alertThreshold() && d.mayAlert(flag.check(), o.timeMillis(), 3000)) {
                String message = "[NCEA] " + p.getName() + " " + flag.check() + " VL=" + String.format(Locale.ROOT, "%.1f", vl) + " " + flag.evidence();
                for (Player staff : Bukkit.getOnlinePlayers()) if (staff.hasPermission("nceaac.alerts") && alerts.contains(staff.getUniqueId())) staff.sendMessage(Component.text(message, NamedTextColor.YELLOW));
                getLogger().info(message);
            }
        }
        int index = switch(o.kind()) { case MOVE -> 0; case ATTACK -> 1; case PLACE -> 2; case BREAK -> 3; };
        nanos[index] += System.nanoTime() - start; calls[index]++;
    }
    @EventHandler public void join(PlayerJoinEvent e) { data(e.getPlayer()); grace.put(e.getPlayer().getUniqueId(), System.currentTimeMillis()+3000); }
    @EventHandler public void quit(PlayerQuitEvent e) { UUID id=e.getPlayer().getUniqueId(); players.remove(id); grace.remove(id); exempt.remove(id); alerts.remove(id); }
    @EventHandler public void teleport(PlayerTeleportEvent e) { grace.put(e.getPlayer().getUniqueId(), System.currentTimeMillis()+2500); data(e.getPlayer()).resetCheckState(); }
    @EventHandler public void respawn(PlayerRespawnEvent e) { grace.put(e.getPlayer().getUniqueId(), System.currentTimeMillis()+3000); data(e.getPlayer()).resetCheckState(); }
    @EventHandler public void velocity(PlayerVelocityEvent e) { grace.put(e.getPlayer().getUniqueId(), System.currentTimeMillis()+2000); data(e.getPlayer()).resetCheckState(); }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) public void move(PlayerMoveEvent e) {
        Location from = e.getFrom(), to = e.getTo();
        if (to == null || from.getWorld() != to.getWorld()) { data(e.getPlayer()).resetCheckState(); return; }
        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) return;
        Player p = e.getPlayer(); PlayerData d = data(p); long now = System.currentTimeMillis();
        boolean finite = Double.isFinite(from.getX()) && Double.isFinite(from.getY()) && Double.isFinite(from.getZ())
                && Double.isFinite(to.getX()) && Double.isFinite(to.getY()) && Double.isFinite(to.getZ());
        boolean safe = finite && eligible(p) && safeTerrain(p, to);
        MovementSampler.sample(d, now, from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ(),
                p.isOnGround(), safe).ifPresent(o -> inspect(p, d, o));
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) public void damage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p) || !eligible(p)) return;
        if (!(e.getEntity() instanceof Player target)) return;
        Location a=p.getEyeLocation();
        org.bukkit.util.BoundingBox box=target.getBoundingBox();
        double x=Math.max(box.getMinX(),Math.min(a.getX(),box.getMaxX()));
        double y=Math.max(box.getMinY(),Math.min(a.getY(),box.getMaxY()));
        double z=Math.max(box.getMinZ(),Math.min(a.getZ(),box.getMaxZ()));
        double reach=Math.sqrt(Math.pow(a.getX()-x,2)+Math.pow(a.getY()-y,2)+Math.pow(a.getZ()-z,2));
        inspect(p,data(p),new Observation(Observation.Kind.ATTACK,System.currentTimeMillis(),0,0,false,reach,0,true));
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true) public void place(BlockPlaceEvent e) {
        Player p=e.getPlayer(); PlayerData d=data(p); long now=System.currentTimeMillis();
        if (eligible(p)) {
            inspect(p,d,new Observation(Observation.Kind.PLACE,now,0,0,false,0,0,true));
            d.lastPlace=now;
        } else d.lastPlace=0;
    }
    private void msg(CommandSender s, String text) { s.sendMessage(Component.text("[NCEA] " + text, NamedTextColor.AQUA)); }
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String requested = args.length == 0 ? "help" : args[0];
        if (!AccessPolicy.canExecute(requested, sender.hasPermission("nceaac.admin"), sender.hasPermission("nceaac.alerts"))) {
            msg(sender,"No permission / Нет прав"); return true;
        }
        if (args.length==0 || args[0].equalsIgnoreCase("help")) { msg(sender,"help, reload, status, checks, alerts, violations/profile/debug <player>, reset/exempt/unexempt <player>, performance, version, config validate"); return true; }
        String sub=args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("reload")) {
            BrandingLoader.Loaded next;
            try { next = brandingLoader.load(false, settings.banner()); brandingValidation = "OK: branding.yml"; }
            catch (Exception ex) { brandingValidation = "ERROR: " + ex.getMessage(); msg(sender, brandingValidation); return true; }
            if (settings.load()) {
                branding = next;
                for (Player p : Bukkit.getOnlinePlayers()) data(p).resetCheckState();
                msg(sender,"Configuration reloaded / Конфигурация обновлена");
            } else msg(sender,"Invalid configuration / Ошибка конфигурации");
            return true;
        }
        if (sub.equals("status") || sub.equals("version")) { msg(sender,"0.1.0 Development; active players="+players.size()+"; Paper="+Bukkit.getBukkitVersion()); return true; }
        if (sub.equals("checks")) { msg(sender,checks.checks().stream().map(c->c.id()+"="+(settings.rule(c.id()).enabled()?"on":"off")).toList().toString()); return true; }
        if (sub.equals("performance")) { for(int i=0;i<4;i++) msg(sender,new String[]{"move","attack","place","break"}[i]+": calls="+calls[i]+", avg ns="+(calls[i]==0?0:nanos[i]/calls[i])); return true; }
        if (sub.equals("alerts")) { if(!(sender instanceof Player p)) { msg(sender,"Player only / Только игрок"); return true; } if(!alerts.add(p.getUniqueId())) alerts.remove(p.getUniqueId()); msg(sender,"Alerts="+alerts.contains(p.getUniqueId())); return true; }
        if (sub.equals("config") && args.length>1 && args[1].equalsIgnoreCase("validate")) { msg(sender,settings.validate() + "; " + brandingValidation); return true; }
        if (Set.of("violations","profile","reset","exempt","unexempt","debug").contains(sub)) {
            if(args.length<2) { msg(sender,"Specify player / Укажите игрока"); return true; }
            Player p=Bukkit.getPlayerExact(args[1]); if(p==null) { msg(sender,"Player offline / Игрок не в сети"); return true; }
            UUID id=p.getUniqueId();
            switch(sub) {
                case "reset" -> { data(p).violations.clear(); data(p).lastAlerts.clear(); data(p).resetCheckState(); msg(sender,"Violations cleared"); }
                case "exempt" -> { exempt.add(id); data(p).resetCheckState(); msg(sender,"Exempted"); }
                case "unexempt" -> { exempt.remove(id); data(p).resetCheckState(); msg(sender,"Exemption removed"); }
                default -> msg(sender,data(p).violations.entrySet().stream().map(x->x.getKey()+"="+String.format(Locale.ROOT,"%.1f",x.getValue().current(settings.rule(x.getKey()).decay(),System.currentTimeMillis()))+" ["+x.getValue().evidence()+"]").toList().toString());
            } return true;
        }
        msg(sender,"Unknown command / Неизвестная команда"); return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("nceaac.admin")) return sender.hasPermission("nceaac.alerts") && args.length == 1 ? List.of("alerts").stream().filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT))).toList() : List.of();
        if(args.length==1) return List.of("help","reload","status","checks","alerts","violations","profile","reset","exempt","unexempt","debug","performance","version","config").stream().filter(s->s.startsWith(args[0].toLowerCase(Locale.ROOT))).toList();
        if(args.length==2 && Set.of("violations","profile","reset","exempt","unexempt","debug").contains(args[0].toLowerCase(Locale.ROOT))) return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s->s.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))).toList();
        if(args.length==2 && args[0].equalsIgnoreCase("config")) return List.of("validate");
        return List.of();
    }
}
