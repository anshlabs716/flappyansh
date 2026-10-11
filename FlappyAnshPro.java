import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

class FlappyAnshPro extends JPanel implements Runnable, KeyListener {
    private final int WIDTH = 600, HEIGHT = 550, GROUND_Y = 440;

    private enum State { MENU, BIRD_SELECT, DIFF_SELECT, PLAYING, GAMEOVER, PREVIEW, CREDITS, EASTER_EGG_UNLOCKED }
    private enum Weather { SUNNY, RAINY, WINDY, CLOUDY, STORMY }
    private enum Platform {
        WINDOWS("Windows", "WIN"),
        MAC("macOS", "MAC"),
        LINUX("Linux", "LINUX"),
        CHROMEOS("ChromeOS", "CHROME"),
        OTHER("Unknown OS", "OS");

        private final String label;
        private final String shortLabel;

        Platform(String label, String shortLabel) {
            this.label = label;
            this.shortLabel = shortLabel;
        }

        static Platform detect() {
            String systemText = (System.getProperty("os.name", "") + " " +
                    System.getProperty("os.version", "") + " " +
                    System.getenv().getOrDefault("XDG_CURRENT_DESKTOP", "") + " " +
                    System.getenv().getOrDefault("DESKTOP_SESSION", "") + " " +
                    System.getenv().getOrDefault("CHROMEOS_RELEASE_NAME", "") + " " +
                    System.getenv().getOrDefault("CHROMEOS_RELEASE_VERSION", "")).toLowerCase(Locale.ROOT);
            if (systemText.contains("chrome") || systemText.contains("cros")) return CHROMEOS;
            if (systemText.contains("win")) return WINDOWS;
            if (systemText.contains("mac") || systemText.contains("darwin")) return MAC;
            if (systemText.contains("linux") || systemText.contains("nux") || systemText.contains("nix")) return LINUX;
            return OTHER;
        }
    }

    private final Platform platform = Platform.detect();
    private State currentState = State.MENU;
    private Weather currentWeather = Weather.SUNNY, nextWeather = Weather.SUNNY;
    private int weatherTransition = 0, lastWeatherScore = 0;

    private final double[] difficultyGravity = {0.0, 0.18, 0.21, 0.24, 0.27, 0.30};
    private final double[] difficultyJump = {0.0, -5.5, -5.8, -6.1, -6.4, -6.7};
    private final double[] difficultyFallSpeed = {0.0, 4.6, 5.0, 5.4, 5.8, 6.2};
    private final int[] difficultyPipeSpeed = {0, 2, 3, 4, 5, 6};
    private final int[] difficultyPipeGap = {0, 180, 165, 150, 138, 126};

    private double birdY = 200, birdVel = 0, gravity = difficultyGravity[1], jump = difficultyJump[1], maxFallSpeed = difficultyFallSpeed[1];
    private int pipeSpeed = 5;
    private int pipeGap = 150;
    private int score = 0;
    private int highScore = 0;
    private int anshCoins = 0;
    private final ArrayList<Rectangle> pipes = new ArrayList<>();

    private boolean autoMode = false;
    private int currentDifficulty = 1;

    private final Color WII_HILL_DARK = new Color(58, 153, 62);
    private final Color WII_HILL_LIGHT = new Color(92, 196, 98);
    private final Color WII_PIPE = new Color(45, 180, 0);
    private final Color WII_GROUND_TOP = new Color(120, 200, 60);
    private final Color WII_GROUND_DIRT = new Color(150, 100, 50);

    private Color birdColor = Color.YELLOW;
    private boolean easterEggEnabled = false, isSecretUnlocked = false;
    private int secretCounter = 0;
    private volatile BufferedImage devImage;
    private boolean devImageLoadStarted = false;

    private int shakeTicks = 0, flashTicks = 0;
    private long lastInputTime = System.currentTimeMillis();
    private final Random rand = new Random();
    
    // Particle system
    private final ArrayList<Particle> particles = new ArrayList<>();
    
    // Power-ups
    private boolean hasShield = false;
    private boolean hasDoubleScore = false;
    private int shieldTimer = 0;
    private int doubleScoreTimer = 0;
    private final Rectangle powerUp = new Rectangle();
    private boolean powerUpActive = false;
    private int powerUpType = 0; // 0: shield, 1: double score, 2: slow motion
    
    // Achievements
    private final boolean[] achievements = new boolean[10];
    private final String[] achievementNames = {
        "FIRST FLAP", "SCORE 10", "SCORE 50", "SCORE 100",
        "UNLOCK SECRET", "WEATHER MASTER", "COIN COLLECTOR",
        "PERFECT GAME", "SPEED DEMON", "LEGENDARY"
    };
    private int achievementDisplayTimer = 0;
    private String lastAchievement = "";
    // Particle class for visual effects
    private static class Particle {
        double x, y, vx, vy;
        Color color;
        int life, maxLife;
        double size;
        
        Particle(double x, double y, double vx, double vy, Color color, int life, double size) {
            this.x = x; this.y = y; this.vx = vx; this.vy = vy;
            this.color = color; this.life = life; this.maxLife = life;
            this.size = size;
        }
        
        void update() {
            x += vx; y += vy;
            vy += 0.1; // gravity
            life--;
        }
        
        void draw(Graphics2D g) {
            float alpha = (float) life / maxLife;
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
            g.fillOval((int)x, (int)y, (int)size, (int)size);
        }
    }
    
    private final String[] INSULTS = {
            "💀 L + RATIO + PIPE 💀", "📉 AURA DEBT DETECTED 📉", "🤖 NPC FLIGHT PATH 🤖", "😭 CHAT SAW THAT 😭", "🚫 RIZZ ON AIRPLANE MODE 🚫",
            "🧢 CAPTAIN CAP JUST CRASHED 🧢", "📱 SCREEN RECORDING THAT L 📱", "🫠 VIBES GOT FOLDED 🫠", "🎯 AIM LEFT THE GROUP CHAT 🎯", "🔥 COOKED IN 4K 🔥",
            "💅 SLAYED BY GRAVITY 💅", "🧃 NO JUICE LEFT 🧃", "📉 AURA STOCKS CRASHED 📉", "🫡 FLAP CAREER OVER 🫡", "🧠 BRAINROT SPEEDRUN 🧠",
            "🚧 PIPE SAID NAH 🚧", "💀 BRO GOT CLIPPED 💀", "🛑 SKILL ISSUE LIVE 🛑", "🤡 GOOBER MODE ACTIVATED 🤡", "📦 PACKED UP BY A PIPE 📦",
            "😬 THAT WAS NOT SIGMA 😬", "🥶 ZERO AURA MOMENT 🥶", "🧍 MAIN CHARACTER ARC DENIED 🧍", "🎮 CONTROLLER COPIUM 🎮", "🚀 TAKEOFF FAILED BADLY 🚀",
            "🫥 STEALTH SKILL MISSING 🫥", "🧊 ICE COLD SCORE 🧊", "💔 FLAP RIZZ REJECTED 💔", "🕳️ SENT TO THE L DIMENSION 🕳️", "🤨 BRO THOUGHT HE HAD IT 🤨",
            "📛 CERTIFIED OOF MOMENT 📛", "🔔 SKILL ALARM RINGING 🔔", "🧲 PIPE MAGNET ENERGY 🧲", "🥴 DELULU PILOTING 🥴", "📺 REPLAY THAT L 📺",
            "💥 GAP SAID ACCESS DENIED 💥", "🫵 YOU JUST GOT PIPE-CHECKED 🫵", "🌪️ MOVEMENT IN SHAMBLES 🌪️", "😭 NOT EVEN CLOSE BESTIE 😭", "🚨 EMERGENCY AURA LOSS 🚨",
            "🛫 FLIGHT LICENSE REVOKED 🛫", "🧯 THE FLAME GOT PUT OUT 🧯", "🎲 RNG COULD NOT SAVE YOU 🎲", "🧱 FLEW LIKE A BRICK 🧱", "🫡 RESPECTFULLY COOKED 🫡",
            "📉 SCORE SAID BYE 📉", "💀 BRO GOT SENT 💀", "🎪 ABSOLUTE CIRCUS FLAPS 🎪", "🤳 POV: INSTANT REGRET 🤳", "🥀 DREAM ENDED MID-FLAP 🥀",
            "🧠 ONE BRAIN CELL BUFFERING 🧠", "💸 AURA BANKRUPTCY 💸", "🍳 FRIED BY THE GAP 🍳", "😵 REACTION TIME LAGGING 😵", "🚪 VICTORY DOOR LOCKED 🚪",
            "🤌 SKILL LEFT NO NOTES 🤌", "🧢 THAT FLAP WAS CAP 🧢", "👀 EVERYONE SAW THAT 👀", "🫠 MELTED MID-AIR 🫠", "📵 NO SIGNAL TO THE BRAIN 📵",
            "🏁 LAST PLACE ENERGY 🏁", "🎯 GAP NOT FOUND 🎯", "💅 SERVING ZERO ALTITUDE 💅", "🚧 DIRECT PIPE DELIVERY 🚧", "🥶 COLD GAMEPLAY ALERT 🥶",
            "🤖 BOT BEHAVIOR CONFIRMED 🤖", "🔻 ALTITUDE GOT DELETED 🔻", "😭 BRO LOST TO A RECTANGLE 😭", "🧃 FLAP JUICE EMPTY 🧃", "💥 PIPE JUMPSCARE WON 💥",
            "🧊 FROZEN FINGERS MOMENT 🧊", "📉 MOMENTUM LEFT THE CHAT 📉", "😬 THAT WAS WILDLY UNSERIOUS 😬", "🕹️ BUTTONS COULD NOT CARRY 🕹️", "🫥 STEALTH MODE INTO FAILURE 🫥",
            "🤨 SUS FLAP DETECTED 🤨", "🔥 ROASTED BY THE HITBOX 🔥", "💀 DELETED BY GRAVITY 💀", "🎮 SKILL TREE UNINSTALLED 🎮", "🧱 WALL WITH WINGS ENERGY 🧱",
            "📦 SAME-DAY L DELIVERY 📦", "🚫 GAP ACCESS REVOKED 🚫", "💸 AURA IN OVERDRAFT 💸", "🧠 PILOT.EXE STOPPED 🧠", "🥴 FLAP TIMING IN SHAMBLES 🥴",
            "📜 L ADDED TO HISTORY 📜", "⚠️ LOW BATTERY GAMEPLAY ⚠️", "🧨 PERFORMANCE GOT NUKED 🧨", "🥶 ZERO CHILL ZERO SCORE 🥶", "💀 CHAT IS THIS REAL 💀",
            "🚀 BRO LAUNCHED INTO FAILURE 🚀", "🧲 ATTRACTED TO LOSING 🧲", "📉 CONFIDENCE CRASHED 📉", "🫡 F TO THE FLAPS 🫡", "🤡 GOOFY AHH FLIGHT 🤡",
            "🍝 SPAGHETTI MOVEMENT 🍝", "🛑 FLAP PRIVILEGES PAUSED 🛑", "😵 DIZZY FROM LOSING 😵", "🎪 CLOWN CAR PILOTING 🎪", "💅 AESTHETICALLY DEFEATED 💅",
            "🧠 IQ LEFT MID-FLAP 🧠", "🚧 PIPE HAD FINAL SAY 🚧", "📦 BOXED BY THE OBSTACLE 📦", "🥀 MAIN QUEST FAILED 🥀", "🔥 EXTRA CRISPY L 🔥"
    };
    private String currentInsult = "";

    public FlappyAnshPro() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                handlePointerPress(e.getX(), e.getY());
            }
        });
    }

    private Point toGamePoint(int screenX, int screenY) {
        double scaleX = getWidth() / (double) WIDTH;
        double scaleY = getHeight() / (double) HEIGHT;
        int gameX = (int) Math.round(screenX / Math.max(0.01, scaleX));
        int gameY = (int) Math.round(screenY / Math.max(0.01, scaleY));
        return new Point(Math.max(0, Math.min(WIDTH, gameX)), Math.max(0, Math.min(HEIGHT, gameY)));
    }

    private boolean hitMenuRow(Point p, int baselineY) {
        return p.x >= 110 && p.x <= 490 && p.y >= baselineY - 28 && p.y <= baselineY + 12;
    }

    private int hitListRow(Point p, int centerY, int rowHeight, int count) {
        int top = centerY - rowHeight / 2;
        if (p.y < top || p.y >= top + rowHeight * count || p.x < 115 || p.x > 500) return -1;
        return (p.y - top) / rowHeight;
    }

    private void handlePointerPress(int screenX, int screenY) {
        lastInputTime = System.currentTimeMillis();
        Point p = toGamePoint(screenX, screenY);

        if (currentState == State.MENU) {
            if (hitMenuRow(p, 220)) openDifficultyMenu();
            else if (hitMenuRow(p, 260)) openCredits();
            else if (hitMenuRow(p, 300)) System.exit(0);
            else if (hitMenuRow(p, 465)) advanceSecretUnlock();
        }
        else if (currentState == State.DIFF_SELECT) {
            int row = hitListRow(p, 160, 50, 6);
            if (row >= 0 && row <= 4) chooseDifficulty(row + 1);
            else if (row == 5) chooseAutoDifficulty();
        }
        else if (currentState == State.BIRD_SELECT) {
            int row = hitListRow(p, 110, 42, isSecretUnlocked ? 8 : 7);
            if (row >= 0 && row <= 6) chooseBird(row);
            else if (row == 7) chooseSecretBird();
        }
        else if (currentState == State.PLAYING) {
            flap();
        }
        else if (currentState == State.PREVIEW || currentState == State.GAMEOVER ||
                currentState == State.CREDITS || currentState == State.EASTER_EGG_UNLOCKED) {
            returnToMenu();
        }
    }

    private void returnToMenu() {
        currentState = State.MENU;
        lastInputTime = System.currentTimeMillis();
    }

    private void openDifficultyMenu() {
        currentState = State.DIFF_SELECT;
    }

    private void openCredits() {
        currentState = State.CREDITS;
    }

    private void advanceSecretUnlock() {
        secretCounter++;
        if (secretCounter >= 6) {
            isSecretUnlocked = true;
            loadEasterEggImageAsync();
            currentState = State.EASTER_EGG_UNLOCKED;
            secretCounter = 0;
        }
    }

    private void chooseDifficulty(int level) {
        autoMode = false;
        setDifficulty(level);
    }

    private void chooseAutoDifficulty() {
        autoMode = true;
        currentDifficulty = 3;
        setDifficulty(3);
    }

    private void chooseBird(int index) {
        Color[] colors = {Color.YELLOW, new Color(255,255,255,100), new Color(255,215,0), Color.RED, Color.MAGENTA, Color.BLACK, Color.CYAN};
        birdColor = colors[index];
        easterEggEnabled = false;
        resetGame();
    }

    private void chooseSecretBird() {
        if (!isSecretUnlocked) return;
        loadEasterEggImageAsync();
        easterEggEnabled = true;
        resetGame();
    }

    private void flap() {
        if (currentState == State.PLAYING || currentState == State.PREVIEW) {
            birdVel = jump;
            playJumpSound();
            // Spawn fewer flap particles for performance
            synchronized(particles) {
                for (int i = 0; i < 2; i++) {
                    particles.add(new Particle(
                        150, (int)birdY + 10,
                        (rand.nextDouble() - 0.5) * 3,
                        rand.nextDouble() * 2 + 1,
                        birdColor, 15, 5 + rand.nextDouble() * 3
                    ));
                }
            }
            // First flap achievement
            if (!achievements[0]) {
                achievements[0] = true;
                showAchievement("FIRST FLAP");
            }
        }
    }

    private synchronized void loadEasterEggImageAsync() {
        if (devImageLoadStarted) return;
        devImageLoadStarted = true;
        Thread imageLoader = new Thread(this::loadEasterEggImage, "anshyboii-image-loader");
        imageLoader.setDaemon(true);
        imageLoader.start();
    }

    private void loadEasterEggImage() {
        String name = "IMG_20251226_203036301_HDR.jpg";
        String[] paths = { name, "src/" + name, "bin/" + name, "../" + name };

        for (String path : paths) {
            try {
                File f = new File(path);
                if (f.exists()) {
                    devImage = ImageIO.read(f);
                    System.out.println("Loaded Anshyboii from: " + f.getAbsolutePath());
                    return;
                }
            } catch (IOException e) { /* try next path */ }
        }
        System.out.println("CRITICAL: Place '" + name + "' in your project folder!");
    }

    private void playSound(int startFreq, int endFreq, int duration) {
        new Thread(() -> {
            try {
                int sampleRate = 44100;
                byte[] audioData = new byte[sampleRate * duration / 1000];
                for (int i = 0; i < audioData.length; i++) {
                    double t = (double) i / sampleRate;
                    double freq = startFreq + (endFreq - startFreq) * (t / (duration / 1000.0));
                    audioData[i] = (byte) (Math.sin(2.0 * Math.PI * freq * t) * 127);
                }

                AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
                SourceDataLine line = AudioSystem.getSourceDataLine(format);
                line.open(format);
                line.start();
                line.write(audioData, 0, audioData.length);
                line.drain();
                line.close();
            } catch (Exception e) { /* no audio available */ }
        }).start();
    }

    private void playJumpSound() { playSound(400, 800, 100); }
    private void playScoreSound() { playSound(1200, 1200, 150); }
    private void playDeathSound() { playSound(300, 150, 500); }
    
    private void showAchievement(String name) {
        lastAchievement = name;
        achievementDisplayTimer = 180; // 3 seconds at 60fps
        playSound(800, 1200, 200);
    }
    
    private void spawnPowerUp() {
        if (!powerUpActive && rand.nextInt(500) < 3) { // 0.6% chance per frame (less frequent)
            powerUpActive = true;
            powerUpType = rand.nextInt(3);
            // Position power-up in a safe area between pipes
            int minY = 150;
            int maxY = GROUND_Y - 100;
            powerUp.setBounds(WIDTH + 50, minY + rand.nextInt(maxY - minY), 30, 30);
        }
    }
    
    private void updatePowerUps() {
        if (powerUpActive) {
            powerUp.x -= pipeSpeed;
            // Check collision with bird
            if (powerUp.intersects(new Rectangle(150-16, (int)birdY-16, 32, 32))) {
                powerUpActive = false;
                switch (powerUpType) {
                    case 0: // Shield
                        hasShield = true;
                        shieldTimer = 300;
                        showAchievement("SHIELD ACTIVATED");
                        break;
                    case 1: // Double Score
                        hasDoubleScore = true;
                        doubleScoreTimer = 300;
                        showAchievement("DOUBLE SCORE");
                        break;
                    case 2: // Slow Motion
                        pipeSpeed = Math.max(1, pipeSpeed - 2);
                        showAchievement("SLOW MOTION");
                        break;
                }
                // Spawn collection particles (reduced for performance)
                synchronized(particles) {
                    for (int i = 0; i < 5; i++) {
                        Color pColor = powerUpType == 0 ? Color.CYAN : 
                                       powerUpType == 1 ? Color.YELLOW : Color.MAGENTA;
                        particles.add(new Particle(
                            powerUp.x + 15, powerUp.y + 15,
                            (rand.nextDouble() - 0.5) * 8,
                            (rand.nextDouble() - 0.5) * 8,
                            pColor, 20, 8
                        ));
                    }
                }
            }
            if (powerUp.x < -50) powerUpActive = false;
        }
        
        // Update power-up timers
        if (hasShield) {
            shieldTimer--;
            if (shieldTimer <= 0) hasShield = false;
        }
        if (hasDoubleScore) {
            doubleScoreTimer--;
            if (doubleScoreTimer <= 0) hasDoubleScore = false;
        }
    }
    
    private void checkAchievements() {
        if (score >= 10 && !achievements[1]) {
            achievements[1] = true;
            showAchievement("SCORE 10");
        }
        if (score >= 50 && !achievements[2]) {
            achievements[2] = true;
            showAchievement("SCORE 50");
        }
        if (score >= 100 && !achievements[3]) {
            achievements[3] = true;
            showAchievement("SCORE 100");
        }
        if (isSecretUnlocked && !achievements[4]) {
            achievements[4] = true;
            showAchievement("UNLOCK SECRET");
        }
        if (currentWeather == Weather.STORMY && !achievements[5]) {
            achievements[5] = true;
            showAchievement("WEATHER MASTER");
        }
        if (anshCoins >= 50 && !achievements[6]) {
            achievements[6] = true;
            showAchievement("COIN COLLECTOR");
        }
    }

    private void update() {
        if (currentState == State.MENU && System.currentTimeMillis() - lastInputTime > 5000) {
            startPreview();
        }

        if (currentState == State.PLAYING || currentState == State.PREVIEW) {
            if (currentState == State.PREVIEW) runAI();

            // Weather transition logic every 3 points
            if (currentState == State.PLAYING && score > 0 && score % 3 == 0 && score != lastWeatherScore) {
                lastWeatherScore = score;
                nextWeather = Weather.values()[rand.nextInt(Weather.values().length)];
                weatherTransition = 30; // 30 frames for smooth transition
            }

            // Smooth weather transition
            if (weatherTransition > 0) {
                weatherTransition--;
                if (weatherTransition == 0) currentWeather = nextWeather;
            }

            birdVel += gravity;
            birdVel = Math.min(birdVel, maxFallSpeed);
            birdY += birdVel;
            
            // Spawn and update power-ups
            if (currentState == State.PLAYING) {
                spawnPowerUp();
                updatePowerUps();
                checkAchievements();
            }
            
            for (Rectangle p : pipes) {
                p.x -= pipeSpeed;
                if (p.intersects(new Rectangle(150-16, (int)birdY-16, 32, 32))) {
                    if (hasShield) {
                        hasShield = false;
                        shieldTimer = 0;
                        // Shield break particles (reduced for performance)
                        synchronized(particles) {
                            for (int i = 0; i < 5; i++) {
                                particles.add(new Particle(
                                    150, (int)birdY,
                                    (rand.nextDouble() - 0.5) * 10,
                                    (rand.nextDouble() - 0.5) * 10,
                                    Color.CYAN, 20, 6
                                ));
                            }
                        }
                        playSound(200, 100, 300);
                    } else if (currentState == State.PREVIEW) {
                        startPreview();
                    } else {
                        triggerDeath();
                    }
                }
            }
            if (!pipes.isEmpty() && pipes.get(0).x < -100) {
                pipes.remove(0); pipes.remove(0); spawnPipe(); 
                int pointsGained = hasDoubleScore ? 2 : 1;
                score += pointsGained;
                if (score % 2 == 0) anshCoins++;
                playScoreSound();
                if (score > highScore) highScore = score;

                // Auto difficulty: increase every 5 successful pipes
                if (autoMode && score % 5 == 0 && currentDifficulty < 5) {
                    currentDifficulty++;
                    setDifficulty(currentDifficulty);
                }
                
                // Score particles
                synchronized(particles) {
                    for (int i = 0; i < 3; i++) {
                        particles.add(new Particle(
                            WIDTH / 2, 80,
                            (rand.nextDouble() - 0.5) * 6,
                            (rand.nextDouble() - 0.5) * 6,
                            Color.YELLOW, 20, 6
                        ));
                    }
                }
            }
            if (birdY > GROUND_Y - 20 || birdY < 0) {
                if (currentState == State.PREVIEW) startPreview(); else triggerDeath();
            }
        }
        
        // Update particles
        try {
            ArrayList<Particle> toRemove = new ArrayList<>();
            for (Particle p : particles) {
                if (p != null) {
                    p.update();
                    if (p.life <= 0) toRemove.add(p);
                }
            }
            particles.removeAll(toRemove);
        } catch (Exception e) {
            // Skip particle update if error occurs
        }
        
        // Update achievement display timer
        if (achievementDisplayTimer > 0) achievementDisplayTimer--;
        
        if (shakeTicks > 0) shakeTicks--;
        if (flashTicks > 0) flashTicks--;
    }

    private void startPreview() {
        lastInputTime = System.currentTimeMillis();
        currentState = State.PREVIEW;
        birdY = 200; birdVel = 0; score = 0;
        pipes.clear();
        setDifficulty(2);
        spawnPipe();
    }

    private void triggerDeath() {
        currentState = State.GAMEOVER;
        shakeTicks = 15;
        flashTicks = 5;
        // Mix of generic and OS-specific insults
        String[] osInsults = getOSInsults();
        if (rand.nextBoolean()) {
            currentInsult = osInsults[rand.nextInt(osInsults.length)];
        } else {
            currentInsult = tuneEmojiForPlatform(INSULTS[rand.nextInt(INSULTS.length)]);
        }
        playDeathSound();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Scale to fill the screen (stretch if necessary)
        double scaleX = getWidth() / (double) WIDTH;
        double scaleY = getHeight() / (double) HEIGHT;

        g2.scale(scaleX, scaleY);
        if (shakeTicks > 0) g2.translate(rand.nextInt(10)-5, rand.nextInt(10)-5);

        // Get sky color based on weather with smooth transition
        Color skyColor = getWeatherSkyColor();
        g2.setColor(skyColor); g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(WII_HILL_DARK); g2.fillOval(-80, GROUND_Y - 120, 300, 150);
        g2.fillOval(350, GROUND_Y - 100, 350, 150);
        g2.setColor(WII_HILL_LIGHT); g2.fillOval(120, GROUND_Y - 80, 280, 100);

        g2.setColor(WII_GROUND_TOP); g2.fillRect(0, GROUND_Y, WIDTH, 12);
        g2.setColor(WII_GROUND_DIRT); g2.fillRect(0, GROUND_Y + 12, WIDTH, HEIGHT - GROUND_Y);
        g2.setColor(new Color(0, 0, 0, 30));
        for(int i = 0; i < WIDTH; i += 40) g2.fillRect(i, GROUND_Y + 12, 20, HEIGHT);

        if (currentState == State.MENU) drawMenu(g2);
        else if (currentState == State.BIRD_SELECT) drawBirdMenu(g2);
        else if (currentState == State.DIFF_SELECT) drawDiffMenu(g2);
        else if (currentState == State.CREDITS) drawCredits(g2);
        else if (currentState == State.EASTER_EGG_UNLOCKED) drawUnlockScreen(g2);
        else {
            drawPipes(g2);
            if (easterEggEnabled) {
                if (devImage != null) g2.drawImage(devImage, 150-18, (int)birdY-18, 36, 36, null);
                else drawBird(g2, 150, (int)birdY, new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)), birdVel);
            } else {
                drawBird(g2, 150, (int)birdY, (currentState == State.PREVIEW ? Color.LIGHT_GRAY : birdColor), birdVel);
            }

            if (currentState == State.PREVIEW) {
                g2.setColor(new Color(255, 255, 255, 150));
                g2.setFont(new Font("Impact", Font.ITALIC, 30));
                g2.drawString("AUTO MODE - TAP OR PRESS ESC", 115, 40);
            }

            if (autoMode && currentState == State.PLAYING) {
                g2.setColor(new Color(255, 255, 0, 180));
                g2.setFont(new Font("Impact", Font.ITALIC, 20));
                g2.drawString("AUTO LVL: " + currentDifficulty, 20, 40);
            }

            g2.setColor(Color.WHITE); g2.setFont(new Font("Impact", Font.PLAIN, 45));
            String s = "" + score; g2.drawString(s, WIDTH/2 - g2.getFontMetrics().stringWidth(s)/2, 85);

            // Draw weather indicator
            if (currentState == State.PLAYING) {
                g2.setColor(new Color(255, 255, 255, 150));
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString("Weather: " + currentWeather, 450, 40);
            }

            // Draw weather effects
            if (currentWeather == Weather.RAINY || currentWeather == Weather.STORMY) {
                drawRain(g2);
            }
            
            // Draw power-ups
            if (powerUpActive) {
                Color pColor = powerUpType == 0 ? Color.CYAN : 
                               powerUpType == 1 ? Color.YELLOW : Color.MAGENTA;
                g2.setColor(pColor);
                g2.fillOval(powerUp.x, powerUp.y, powerUp.width, powerUp.height);
                g2.setColor(Color.WHITE);
                g2.drawOval(powerUp.x, powerUp.y, powerUp.width, powerUp.height);
                // Power-up icon
                g2.setColor(Color.BLACK);
                String icon = powerUpType == 0 ? "🛡️" : 
                              powerUpType == 1 ? "2x" : "⏱️";
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString(icon, powerUp.x + 8, powerUp.y + 20);
            }
            
            // Draw shield effect
            if (hasShield) {
                g2.setColor(new Color(0, 255, 255, 100));
                g2.fillOval(150 - 25, (int)birdY - 25, 50, 50);
                g2.setColor(new Color(0, 255, 255, 200));
                g2.drawOval(150 - 25, (int)birdY - 25, 50, 50);
            }
            
            // Draw double score indicator
            if (hasDoubleScore) {
                g2.setColor(new Color(255, 255, 0, 180));
                g2.setFont(new Font("Impact", Font.ITALIC, 18));
                g2.drawString("2X SCORE", 20, 65);
            }
        }
        
        // Draw particles
        try {
            for (Particle p : particles) {
                if (p != null) p.draw(g2);
            }
        } catch (Exception e) {
            // Skip particle drawing if error occurs
        }
        
        // Draw achievement notification
        if (achievementDisplayTimer > 0) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(50, HEIGHT - 80, WIDTH - 100, 50);
            g2.setColor(new Color(255, 215, 0));
            g2.drawRect(50, HEIGHT - 80, WIDTH - 100, 50);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            g2.drawString("🏆 ACHIEVEMENT UNLOCKED! " + getOSEmoji(), 70, HEIGHT - 55);
            g2.setColor(new Color(255, 255, 100));
            g2.drawString(lastAchievement, 70, HEIGHT - 35);
        }
        
        if (flashTicks > 0) { g2.setColor(new Color(255, 255, 255, 180)); g2.fillRect(0, 0, WIDTH, HEIGHT); }
        if (currentState == State.GAMEOVER) drawGameOver(g2);
    }

    private void drawMenu(Graphics2D g) {
        // Animated background gradient
        long time = System.currentTimeMillis();
        float hue = (time % 10000) / 10000.0f;
        Color gradientColor = Color.getHSBColor(hue, 0.3f, 0.9f);
        g.setColor(new Color(gradientColor.getRed(), gradientColor.getGreen(), gradientColor.getBlue(), 50));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        String osEmoji = getOSEmoji();
        drawPoppingText(g, "FLAPPY BIRD " + osEmoji, new Font("Impact", Font.PLAIN, 58), Color.WHITE, 95, 0);
        drawPoppingText(g, "ANSH EDITION", new Font("Impact", Font.PLAIN, 42), new Color(255, 215, 0), 145, 80);
        
        // Enhanced menu buttons with glow effect
        String[] menuItems = {"[ 1 ] START GAME", "[ 2 ] CREDITS", "[ 3 ] EXIT"};
        int[] menuY = {220, 260, 300};
        Color[] menuColors = {new Color(0, 200, 100), new Color(100, 150, 255), new Color(255, 100, 100)};
        
        for (int i = 0; i < 3; i++) {
            // Glow effect
            g.setColor(new Color(menuColors[i].getRed(), menuColors[i].getGreen(), menuColors[i].getBlue(), 100));
            g.fillRoundRect(130, menuY[i] - 25, 340, 40, 20, 20);
            drawPoppingText(g, menuItems[i], new Font("Arial", Font.BOLD, 22), menuColors[i], menuY[i], i * 100);
        }
        
        drawPoppingText(g, "HIGH SCORE: " + highScore, new Font("Arial", Font.BOLD, 22), Color.WHITE, 380, 360);
        drawPoppingText(g, "ANSHCOINS: " + anshCoins, new Font("Arial", Font.BOLD, 22), new Color(255, 215, 0), 420, 440);
        drawPoppingText(g, "SECRET: TAP 6 TIMES OR PRESS [ 6 ]", new Font("Arial", Font.BOLD, 18), Color.RED, 465, 520);
        
        // Animated particles in menu
        if (time % 10 < 5) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillOval(100 + (int)(Math.sin(time / 500.0) * 50), 200 + (int)(Math.cos(time / 500.0) * 30), 8, 8);
            g.fillOval(500 - (int)(Math.sin(time / 500.0) * 50), 250 + (int)(Math.cos(time / 500.0) * 30), 8, 8);
        }
        
        drawPlatformLine(g);
    }

    private void drawPoppingText(Graphics2D g, String text, Font font, Color color, int baselineY, long offsetMs) {
        Graphics2D textG = (Graphics2D) g.create();
        textG.setFont(font);
        FontMetrics metrics = textG.getFontMetrics();
        double pulse = (Math.sin((System.currentTimeMillis() + offsetMs) / 260.0) + 1.0) / 2.0;
        double scale = 1.0 + (pulse * 0.045);
        textG.translate(300, baselineY);
        textG.scale(scale, scale);
        int x = -metrics.stringWidth(text) / 2;
        textG.setColor(new Color(0, 0, 0, 90));
        textG.drawString(text, x + 3, 3);
        textG.setColor(color);
        textG.drawString(text, x, 0);
        textG.dispose();
    }

    private void drawUnlockScreen(Graphics2D g) {
        g.setColor(Color.BLACK); g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.RED); g.setFont(new Font("Impact", Font.PLAIN, 35)); g.drawString("ANSHYBOII UNLOCKED", 160, 100);
        if (devImage != null) g.drawImage(devImage, 240, 160, 120, 120, null);
        else { g.setColor(Color.WHITE); g.drawString("IMAGE MISSING - CHECK CONSOLE", 130, 220); }
        g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 18)); g.drawString("ENTER / TAP TO CONTINUE", 190, 350);
    }

    private void drawBirdMenu(Graphics2D g) {
        g.setColor(Color.BLACK); g.setFont(new Font("Impact", Font.PLAIN, 35)); g.drawString("PICK YOUR BIRD", 200, 60);
        String[] names = {"1. YELLOW", "2. GHOST", "3. GOLD", "4. FIRE", "5. GLITCH", "6. VOID", "7. NEON"};
        Color[] colors = {Color.YELLOW, new Color(255,255,255,100), new Color(255,215,0), Color.RED, Color.MAGENTA, Color.BLACK, Color.CYAN};
        for(int i=0; i<7; i++) {
            int yPos = 110 + (i*42); drawBird(g, 190, yPos, colors[i], 0);
            g.setColor(Color.BLACK); g.setFont(new Font("Arial", Font.BOLD, 18)); g.drawString(names[i], 230, yPos + 6);
        }
        if (isSecretUnlocked) { g.setColor(Color.RED); g.drawString("0. ANSHYBOII", 230, 110 + (7*42) + 6); }
    }

    private void drawBird(Graphics2D g, int x, int y, Color body, double vel) {
        Graphics2D bG = (Graphics2D) g.create(); bG.translate(x, y); bG.rotate(Math.max(-0.4, Math.min(0.4, vel * 0.05)));
        bG.setColor(body); bG.fillOval(-16, -13, 32, 26); bG.setColor(Color.WHITE); bG.fillOval(7, -7, 7, 7);
        bG.setColor(Color.BLACK); bG.fillOval(10, -5, 3, 3); bG.setColor(Color.ORANGE); bG.fillPolygon(new int[]{15, 25, 15}, new int[]{-3, 0, 3}, 3); bG.dispose();
    }

    private void drawGameOver(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 200)); g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.RED); g.setFont(new Font("Impact", Font.PLAIN, 50)); g.drawString("YOU GOT COOKED " + getOSEmoji(), 155, 160);
        drawGameOverInsult(g);
        g.setFont(new Font("Impact", Font.PLAIN, 28));
        g.setColor(Color.YELLOW); g.drawString("SCORE: " + score, 255, 300); g.setFont(new Font("Arial", Font.BOLD, 16)); g.drawString("ENTER / TAP FOR MENU", 215, 380);
    }

    private String tuneEmojiForPlatform(String text) {
        return text;
    }
    
    private String getOSEmoji() {
        return switch (platform) {
            case WINDOWS -> "🪟";
            case MAC -> "🍎";
            case LINUX -> "🐧";
            case CHROMEOS -> "🌐";
            case OTHER -> "🎮";
        };
    }
    
    private String[] getOSInsults() {
        return switch (platform) {
            case WINDOWS -> new String[]{
                "💀 BLUE SCREEN OF DEATH 💀", "🪟 WINDOWS UPDATE FAILED 🪟", 
                "🐛 DLL ERROR DETECTED 🐛", "🔄 RESTART REQUIRED BRO 🔄",
                "📉 PERFORMANCE TANKED 📉"
            };
            case MAC -> new String[]{
                "💀 SPINNING WHEEL OF DEATH 💀", "🍎 APPLE CARE NEEDED 🍎",
                "💸 OVERPRICED FAILURE 💸", "🔋 BATTERY DIED 🔋",
                "📱 ICLOUD SYNC FAILED 📱"
            };
            case LINUX -> new String[]{
                "💀 KERNEL PANIC 💀", "🐧 PENGUIN FROZE 🐧",
                "📦 DEPENDENCY HELL 📦", "⌨️ TERMINAL ERROR ⌨️",
                "🔧 COMPILER FAILED 🔧"
            };
            case CHROMEOS -> new String[]{
                "💀 CHROME TAB CRASH 💀", "🌐 ANDROID APP FAILED 🌐",
                "📱 PLAY STORE ERROR 📱", "🔋 BATTERY OPTIMIZED 🔋",
                "📶 WIFI DISCONNECTED 📶"
            };
            case OTHER -> new String[]{
                "💀 MYSTERIOUS CRASH 💀", "🎮 CONTROLLER DISCONNECTED 🎮",
                "📱 UNKNOWN ERROR 📱", "🔧 SYSTEM FAILURE 🔧",
                "⚠️ GENERIC ERROR ⚠️"
            };
        };
    }

    private void drawPlatformLine(Graphics2D g) {
        String text = "OS: " + platform.label + " " + getOSEmoji();
        Font font = new Font("Arial", Font.BOLD, 15);
        FontMetrics metrics = g.getFontMetrics(font);
        int iconSize = 27;
        int gap = 8;
        int totalWidth = iconSize + gap + metrics.stringWidth(text);
        int x = (WIDTH - totalWidth) / 2;
        int y = 504;

        drawPlatformEmoji(g, x, y - iconSize + 7, iconSize);
        g.setFont(font);
        g.setColor(new Color(0, 0, 0, 95));
        g.drawString(text, x + iconSize + gap + 2, y + 2);
        g.setColor(Color.WHITE);
        g.drawString(text, x + iconSize + gap, y);
    }

    private void drawGameOverInsult(Graphics2D g) {
        Font font = fitFont(g, currentInsult, new Font(Font.DIALOG, Font.BOLD, 28), 464);
        FontMetrics metrics = g.getFontMetrics(font);
        int iconSize = 34;
        int gap = 10;
        int textWidth = metrics.stringWidth(currentInsult);
        int totalWidth = iconSize + gap + textWidth + gap + iconSize;
        int x = (WIDTH - totalWidth) / 2;
        int y = 200;

        drawPlatformEmoji(g, x, y, iconSize);
        g.setFont(font);
        g.setColor(new Color(0, 0, 0, 100));
        g.drawString(currentInsult, x + iconSize + gap + 2, 232);
        g.setColor(Color.WHITE);
        g.drawString(currentInsult, x + iconSize + gap, 230);
        drawPlatformEmoji(g, x + iconSize + gap + textWidth + gap, y, iconSize);
    }

    private void drawPlatformEmoji(Graphics2D g, int x, int y, int size) {
        Graphics2D icon = (Graphics2D) g.create();
        icon.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        icon.translate(x, y);
        icon.setColor(new Color(0, 0, 0, 70));
        icon.fillRoundRect(1, 2, size, size, size / 3, size / 3);
        icon.setColor(new Color(255, 255, 255, 242));
        icon.fillRoundRect(0, 0, size, size, size / 3, size / 3);

        switch (platform) {
            case WINDOWS -> drawWindowsEmoji(icon, size);
            case MAC -> drawMacEmoji(icon, size);
            case LINUX -> drawLinuxEmoji(icon, size);
            case CHROMEOS -> drawChromeEmoji(icon, size);
            case OTHER -> drawGamepadEmoji(icon, size);
        }
        icon.dispose();
    }

    private void drawWindowsEmoji(Graphics2D g, int s) {
        g.setColor(new Color(0, 120, 215));
        int gap = Math.max(2, s / 12);
        int pane = (s - 10 - gap) / 2;
        int x = (s - pane * 2 - gap) / 2;
        int y = (s - pane * 2 - gap) / 2;
        g.fillRect(x, y, pane, pane);
        g.fillRect(x + pane + gap, y, pane, pane);
        g.fillRect(x, y + pane + gap, pane, pane);
        g.fillRect(x + pane + gap, y + pane + gap, pane, pane);
        g.setColor(new Color(255, 255, 255, 90));
        g.drawLine(x + pane + gap / 2, y, x + pane + gap / 2, y + pane * 2 + gap);
        g.drawLine(x, y + pane + gap / 2, x + pane * 2 + gap, y + pane + gap / 2);
    }

    private void drawMacEmoji(Graphics2D g, int s) {
        g.setColor(new Color(35, 35, 40));
        g.fillOval((int)(s * 0.25), (int)(s * 0.32), (int)(s * 0.36), (int)(s * 0.46));
        g.fillOval((int)(s * 0.43), (int)(s * 0.29), (int)(s * 0.38), (int)(s * 0.49));
        g.fillOval((int)(s * 0.32), (int)(s * 0.48), (int)(s * 0.39), (int)(s * 0.34));
        g.setColor(new Color(64, 150, 72));
        g.fillOval((int)(s * 0.50), (int)(s * 0.13), (int)(s * 0.24), (int)(s * 0.13));
        g.setColor(new Color(255, 255, 255, 242));
        g.fillOval((int)(s * 0.68), (int)(s * 0.38), (int)(s * 0.22), (int)(s * 0.22));
    }

    private void drawLinuxEmoji(Graphics2D g, int s) {
        g.setColor(new Color(245, 155, 30));
        g.fillOval((int)(s * 0.22), (int)(s * 0.76), (int)(s * 0.26), (int)(s * 0.12));
        g.fillOval((int)(s * 0.52), (int)(s * 0.76), (int)(s * 0.26), (int)(s * 0.12));
        g.setColor(Color.BLACK);
        g.fillOval((int)(s * 0.28), (int)(s * 0.12), (int)(s * 0.44), (int)(s * 0.70));
        g.setColor(Color.WHITE);
        g.fillOval((int)(s * 0.36), (int)(s * 0.36), (int)(s * 0.28), (int)(s * 0.38));
        g.fillOval((int)(s * 0.34), (int)(s * 0.24), (int)(s * 0.14), (int)(s * 0.15));
        g.fillOval((int)(s * 0.52), (int)(s * 0.24), (int)(s * 0.14), (int)(s * 0.15));
        g.setColor(Color.BLACK);
        g.fillOval((int)(s * 0.39), (int)(s * 0.29), Math.max(2, s / 11), Math.max(2, s / 11));
        g.fillOval((int)(s * 0.55), (int)(s * 0.29), Math.max(2, s / 11), Math.max(2, s / 11));
        g.setColor(new Color(245, 155, 30));
        g.fillPolygon(new int[]{s / 2 - s / 9, s / 2 + s / 9, s / 2},
                new int[]{(int)(s * 0.45), (int)(s * 0.45), (int)(s * 0.56)}, 3);
    }

    private void drawChromeEmoji(Graphics2D g, int s) {
        int pad = Math.max(3, s / 7);
        int d = s - pad * 2;
        g.setColor(new Color(231, 59, 45));
        g.fillArc(pad, pad, d, d, 20, 120);
        g.setColor(new Color(252, 197, 54));
        g.fillArc(pad, pad, d, d, 140, 120);
        g.setColor(new Color(43, 150, 80));
        g.fillArc(pad, pad, d, d, 260, 120);
        g.setColor(Color.WHITE);
        g.fillOval(s / 2 - s / 5, s / 2 - s / 5, (s * 2) / 5, (s * 2) / 5);
        g.setColor(new Color(54, 126, 238));
        g.fillOval(s / 2 - s / 7, s / 2 - s / 7, (s * 2) / 7, (s * 2) / 7);
    }

    private void drawGamepadEmoji(Graphics2D g, int s) {
        g.setColor(new Color(95, 77, 150));
        g.fillRoundRect(s / 4, s / 3, s / 2, s / 3, s / 7, s / 7);
        g.setColor(Color.WHITE);
        g.fillRect((int)(s * 0.35), (int)(s * 0.46), s / 5, Math.max(2, s / 12));
        g.fillRect((int)(s * 0.42), (int)(s * 0.39), Math.max(2, s / 12), s / 5);
        g.fillOval((int)(s * 0.62), (int)(s * 0.43), Math.max(3, s / 9), Math.max(3, s / 9));
    }

    private void drawCenteredFittedText(Graphics2D g, String text, Font baseFont, Color color, int baselineY, int maxWidth) {
        Font font = fitFont(g, text, baseFont, maxWidth);
        FontMetrics metrics = g.getFontMetrics(font);

        g.setColor(new Color(0, 0, 0, 100));
        g.setFont(font);
        int x = (WIDTH - metrics.stringWidth(text)) / 2;
        g.drawString(text, x + 2, baselineY + 2);
        g.setColor(color);
        g.drawString(text, x, baselineY);
    }

    private Font fitFont(Graphics2D g, String text, Font baseFont, int maxWidth) {
        Font font = baseFont;
        FontMetrics metrics = g.getFontMetrics(font);
        while (font.getSize() > 14 && metrics.stringWidth(text) > maxWidth) {
            font = font.deriveFont((float) font.getSize() - 1);
            metrics = g.getFontMetrics(font);
        }
        return font;
    }

    private void drawDiffMenu(Graphics2D g) {
        g.setColor(Color.BLACK); g.setFont(new Font("Impact", Font.PLAIN, 35)); g.drawString("SELECT DEMON LEVEL", 165, 80);
        String[] diffs = {"[1] EASY DEMON", "[2] MEDIUM DEMON", "[3] HARD DEMON", "[4] INSANE DEMON", "[5] EXTREME DEMON", "[6] AUTO DEMON"};
        Color[] dCols = {Color.CYAN, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.RED, Color.YELLOW};
        for(int i=0; i<6; i++) { g.setColor(dCols[i]); g.setFont(new Font("Arial", Font.BOLD, 22)); g.drawString(diffs[i], 200, 160 + (i*50)); }
    }

    private void drawCredits(Graphics2D g) {
        g.setColor(Color.BLACK); g.setFont(new Font("Impact", Font.PLAIN, 50)); g.drawString("THE GOATS", 200, 100);
        g.setFont(new Font("Arial", Font.BOLD, 20)); g.drawString("ANSH - OWNER", 190, 155); 
        g.drawString("CHATGPT - IDEA", 190, 185); 
        g.drawString("GEMINI - CO-DEV", 190, 215); 
        g.drawString("GITHUB COPILOT - AI ASSISTANT", 190, 245); 
        g.drawString("CODEX - AI ASSISTANT", 190, 275);
        g.setColor(new Color(255, 100, 100)); g.drawString("Cascade - REVAMP MASTER", 190, 305);
        g.setColor(new Color(100, 255, 100)); g.drawString("shozanthebozan - LEGEND", 190, 335);
        g.setColor(Color.RED); g.setFont(new Font("Arial", Font.BOLD, 18)); g.drawString("ESC / TAP FOR MENU", 205, 385);
    }

    private Color getWeatherSkyColor() {
        Color current = getColorForWeather(currentWeather);
        Color next = getColorForWeather(nextWeather);

        if (weatherTransition <= 0) return current;

        // Smooth interpolation between colors
        float t = 1.0f - (weatherTransition / 30.0f);
        int r = (int) (current.getRed() + (next.getRed() - current.getRed()) * t);
        int g = (int) (current.getGreen() + (next.getGreen() - current.getGreen()) * t);
        int b = (int) (current.getBlue() + (next.getBlue() - current.getBlue()) * t);
        return new Color(r, g, b);
    }

    private Color getColorForWeather(Weather w) {
        return switch(w) {
            case SUNNY -> new Color(148, 219, 255);
            case RAINY -> new Color(100, 140, 180);
            case WINDY -> new Color(160, 200, 255);
            case CLOUDY -> new Color(120, 150, 180);
            case STORMY -> new Color(60, 80, 120);
        };
    }

    private void drawRain(Graphics2D g) {
        long time = System.currentTimeMillis();
        g.setColor(new Color(200, 220, 255, 80));
        for (int i = 0; i < 100; i++) {
            double x = ((time / 20.0 + i * 50) % (WIDTH + 50)) - 25;
            double y = ((time / 10.0 + i * 30) % (HEIGHT + 50)) - 25;
            g.drawLine((int)x, (int)y, (int)(x + 2), (int)(y + 10));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        lastInputTime = System.currentTimeMillis();
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_ESCAPE) { returnToMenu(); return; }
        if (currentState == State.MENU) {
            if (k == KeyEvent.VK_6 || k == KeyEvent.VK_NUMPAD6) advanceSecretUnlock();
            else if (k == KeyEvent.VK_1 || k == KeyEvent.VK_NUMPAD1) openDifficultyMenu();
            else if (k == KeyEvent.VK_2 || k == KeyEvent.VK_NUMPAD2) openCredits();
            else if (k == KeyEvent.VK_3 || k == KeyEvent.VK_NUMPAD3) System.exit(0);
        }
        else if (currentState == State.DIFF_SELECT) {
            if (k >= KeyEvent.VK_1 && k <= KeyEvent.VK_5) chooseDifficulty(k - KeyEvent.VK_0);
            else if (k >= KeyEvent.VK_NUMPAD1 && k <= KeyEvent.VK_NUMPAD5) chooseDifficulty(k - KeyEvent.VK_NUMPAD0);
            else if (k == KeyEvent.VK_6 || k == KeyEvent.VK_NUMPAD6) chooseAutoDifficulty();
        }
        else if (currentState == State.BIRD_SELECT) {
            if (k >= KeyEvent.VK_1 && k <= KeyEvent.VK_7) {
                chooseBird(k - KeyEvent.VK_1);
            } else if ((k == KeyEvent.VK_0 || k == KeyEvent.VK_NUMPAD0) && isSecretUnlocked) chooseSecretBird();
        }
        else if (k == KeyEvent.VK_SPACE) {
            flap();
        }
        else if (k == KeyEvent.VK_ENTER) { if (currentState == State.GAMEOVER || currentState == State.EASTER_EGG_UNLOCKED) returnToMenu(); }
    }

    private void resetGame() {
        birdY = 200;
        birdVel = 0;
        score = 0;
        lastWeatherScore = 0;
        currentWeather = Weather.SUNNY;
        nextWeather = Weather.SUNNY;
        weatherTransition = 0;
        if (autoMode) currentDifficulty = 3;
        pipes.clear();
        particles.clear();
        hasShield = false;
        hasDoubleScore = false;
        shieldTimer = 0;
        doubleScoreTimer = 0;
        powerUpActive = false;
        spawnPipe();
        currentState = State.PLAYING;
    }
    private void runAI() {
        if (pipes.isEmpty()) {
            if (birdY > 250) birdVel = jump;
            return;
        }
        
        // Find the next pipe gap
        Rectangle nextPipe = null;
        for (int i = 0; i < pipes.size(); i += 2) {
            if (pipes.get(i).x > 100) {
                nextPipe = pipes.get(i);
                break;
            }
        }
        
        if (nextPipe == null) {
            // No upcoming pipe, maintain middle height
            if (birdY > 250) birdVel = jump;
            return;
        }
        
        // Calculate target position (center of the gap)
        double gapCenter = nextPipe.height + (pipeGap / 2.0);
        double targetY = gapCenter - 20; // Aim slightly above center
        
        // Smarter AI: flap if below target and falling
        if (birdY > targetY && birdVel > 0) {
            birdVel = jump;
        }
        // Also flap if too close to ground
        if (birdY > 350) {
            birdVel = jump;
        }
    }
    private void spawnPipe() {
        int minPipeHeight = 45;
        int randomRange = GROUND_Y - pipeGap - (minPipeHeight * 2);
        int h = minPipeHeight + rand.nextInt(Math.max(1, randomRange + 1));
        pipes.add(new Rectangle(WIDTH, 0, 70, h)); pipes.add(new Rectangle(WIDTH, h + pipeGap, 70, GROUND_Y - h - pipeGap)); }
    private void setDifficulty(int l) {
        currentDifficulty = Math.max(1, Math.min(5, l));
        gravity = difficultyGravity[currentDifficulty];
        jump = difficultyJump[currentDifficulty];
        maxFallSpeed = difficultyFallSpeed[currentDifficulty];
        pipeSpeed = difficultyPipeSpeed[currentDifficulty];
        pipeGap = difficultyPipeGap[currentDifficulty];
        if(currentState != State.PREVIEW) currentState = State.BIRD_SELECT;
    }
    private void drawPipes(Graphics2D g) {
        for (Rectangle p : pipes) {
            g.setColor(WII_PIPE); g.fillRect(p.x, p.y, p.width, p.height);
            g.setColor(Color.BLACK); g.drawRect(p.x, p.y, p.width, p.height);
            if (p.y == 0) g.fillRect(p.x - 5, p.height - 15, p.width + 10, 15);
            else g.fillRect(p.x - 5, p.y, p.width + 10, 15);
        }
    }
    public void run() {
        while (true) { update(); repaint(); try { Thread.sleep(16); } catch (InterruptedException ignored) {} } }
    public static void main(String[] args) {
        JFrame f = new JFrame("Flappy Bird: Ansh Edition"); FlappyAnshPro game = new FlappyAnshPro(); f.add(game); f.pack(); f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); f.setLocationRelativeTo(null); f.setResizable(true); f.setMinimumSize(new Dimension(400, 350)); f.setExtendedState(JFrame.MAXIMIZED_BOTH); f.setVisible(true);
        game.requestFocus(); new Thread(game).start();
    }
    @Override public void keyTyped(KeyEvent e) {} @Override public void keyReleased(KeyEvent e) {}
}
