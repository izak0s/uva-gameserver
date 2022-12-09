package amsterdam.izak.progproj.platforms;

import amsterdam.izak.progproj.GameServer;
import amsterdam.izak.progproj.data.Platform;
import amsterdam.izak.progproj.network.packets.game.UpdateMapPacket;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlatformManager {
    private final byte size = 5;
    private List<Platform> platforms;
    private List<Platform> defaultPlatform;
    private final List<Color> availableColors;
    private List<Color> assignedColors;
    @Getter
    private Color currentColor;
    public final Color defaultColor = new Color(52, 73, 94);
    private final Random random;


    public PlatformManager() {
        this.random = new Random();
        this.platforms = new ArrayList<>();
        this.defaultPlatform = new ArrayList<>();
        this.availableColors = new ArrayList<>();
        this.assignedColors = new ArrayList<>();
        initColors();

        for (int i = 0; i < size * size; i++) {
            platforms.add(new Platform(true, defaultColor));
            defaultPlatform.add(new Platform(true, defaultColor));
        }
    }

    private void initColors() {
        availableColors.add(new Color(52, 152, 219));
        availableColors.add(new Color(46, 204, 113));
        availableColors.add(new Color(231, 76, 60));
        availableColors.add(new Color(243, 156, 18));
        availableColors.add(new Color(142, 68, 173));
        currentColor = defaultColor;

        for (int i = 0; i < size * size; i++) {
            assignedColors.add(availableColors.get(i % size));
        }
    }

    public PlatformManager randomizeMap() {
        Collections.shuffle(assignedColors);

        for (int i = 0; i < platforms.size(); i++) {
            platforms.get(i).setColor(assignedColors.get(i));
        }

        return this;
    }

    public void sendDefaultMap() throws Exception {
        GameServer.getInstance().sendToAll(new UpdateMapPacket(size, defaultPlatform));
    }

    public void sendMap() throws Exception {
        GameServer.getInstance().sendToAll(new UpdateMapPacket(size, platforms));
    }

    public Color pickColor() throws Exception {
        currentColor = availableColors.get(random.nextInt(availableColors.size()));

        return currentColor;
    }

    public void pickPlatformsWithColor() throws Exception {
        platforms.stream()
                .filter(platform -> !platform.getColor().equals(currentColor))
                .forEach(platform -> platform.setActive(false));

        GameServer.getInstance().sendToAll(new UpdateMapPacket(size, platforms));
    }

    public PlatformManager resetPlatforms() {
        platforms.forEach(platform -> platform.setActive(true));

        return this;
    }



}
