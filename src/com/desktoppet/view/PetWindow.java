package com.desktoppet.view;

import com.desktoppet.model.Pet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class PetWindow extends JFrame {
    private Pet pet;
    private JLabel levelLabel;
    private JProgressBar intimacyBar;
    private JLabel petLabel;
    private JLabel floatLabel;
    private JPopupMenu popupMenu;
    private JMenuItem interactMenuItem, taskMenuItem, settingMenuItem;

    private ArrayList<ImageIcon> currentFrames;
    private int currentFrameIndex;
    private Timer animTimer;
    private Timer idleTimer;
    private Timer actionTimer;        //当前动作/循环动画定时器（连点时先停旧Timer，避免叠加导致动画加速）
    private Timer upgradeStopTimer;   //升级动画的结束定时器
    private Random random;
    private boolean isPlayingAction = false;

    private ArrayList<ArrayList<ImageIcon>> idleAnimations;
    private ArrayList<ImageIcon> happyAnim;
    private ArrayList<ImageIcon> feedAnim;
    private ArrayList<ImageIcon> playAnim;
    private ArrayList<ArrayList<ImageIcon>> upgradeAnimations;

    private Point dragStartPoint;

    public PetWindow(Pet pet) {
        this.pet = pet;
        this.random = new Random();
        initUI();
        loadAllAnimations();
        startAnimationSystem();
    }

    private void initUI() {
        setTitle("桌宠");
        setSize(200, 210);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setAlwaysOnTop(true);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        levelLabel = new JLabel("LV" + pet.getLevel());
        levelLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        levelLabel.setForeground(Color.WHITE);
        levelLabel.setBounds(10, 5, 50, 20);
        add(levelLabel);

        intimacyBar = new JProgressBar();
        intimacyBar.setBounds(60, 5, 130, 18);
        intimacyBar.setMaximum(pet.getMaxIntimacy());
        intimacyBar.setValue(pet.getIntimacy());
        intimacyBar.setString(pet.getIntimacy() + "/" + pet.getMaxIntimacy());
        intimacyBar.setStringPainted(true);
        add(intimacyBar);

        petLabel = new JLabel();
        petLabel.setBounds(20, 30, 160, 160);
        petLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(petLabel);

        floatLabel = new JLabel();
        floatLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        floatLabel.setForeground(new Color(255, 100, 100));
        floatLabel.setHorizontalAlignment(SwingConstants.CENTER);
        floatLabel.setBounds(0, 80, 200, 30);
        floatLabel.setVisible(false);
        add(floatLabel);

        popupMenu = new JPopupMenu();
        interactMenuItem = new JMenuItem("互动");
        taskMenuItem = new JMenuItem("任务");
        settingMenuItem = new JMenuItem("设置");
        JMenuItem studyMenuItem = new JMenuItem("自习室");
        popupMenu.add(interactMenuItem);
        popupMenu.add(taskMenuItem);
        popupMenu.add(settingMenuItem);
        popupMenu.add(studyMenuItem);
        studyMenuItem.addActionListener(e -> {
            // 1. 创建一个新的自习室窗口
            StudyRoomWindow room = new StudyRoomWindow();

            // 2. 设置关闭模式为 DISPOSE_ON_CLOSE
            room.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            //3.读取最新累计专注秒数
            int seconds = TimerWindow.globalSeconds;

            //4. 刷新累计专注时长
            room.updateWelcomeFocusTime(seconds);

            // 5. 显示窗口
            room.setVisible(true);
        });

        petLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        });

        MouseAdapter dragAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { dragStartPoint = e.getPoint(); }
        };
        MouseMotionAdapter dragMotion = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = getLocation();
                setLocation(p.x + e.getX() - dragStartPoint.x, p.y + e.getY() - dragStartPoint.y);
            }
        };
        addMouseListener(dragAdapter);
        addMouseMotionListener(dragMotion);
        petLabel.addMouseListener(dragAdapter);
        petLabel.addMouseMotionListener(dragMotion);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width - 250, screen.height - 350);
    }

    private void loadAllAnimations() {
        String base = "assets";
        idleAnimations = new ArrayList<>();
        String[] idleFolders = {"breath","cry1","cry2","cry3","scared","poke1","poke2","hit","tease"};
        for (String f : idleFolders) {
            ArrayList<ImageIcon> frames = loadFrames(base + "/idle/" + f);
            if (!frames.isEmpty()) idleAnimations.add(frames);
        }
        happyAnim = loadFrames(base + "/interact/happy");
        feedAnim = loadFrames(base + "/upgrade/shy");
        playAnim = loadFrames(base + "/interact/dance");
        upgradeAnimations = new ArrayList<>();
        String[] upgradeFolders = {"shy","wakeup","angry","sigh","tease2"};
        for (String f : upgradeFolders) {
            ArrayList<ImageIcon> frames = loadFrames(base + "/upgrade/" + f);
            if (!frames.isEmpty()) upgradeAnimations.add(frames);
        }
    }

    private ArrayList<ImageIcon> loadFrames(String folderPath) {
        ArrayList<ImageIcon> frames = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) return frames;
        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (files == null) return frames;
        Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
        for (File f : files) {
            ImageIcon original = new ImageIcon(f.getAbsolutePath());
            Image scaled = original.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            frames.add(new ImageIcon(scaled));
        }
        return frames;
    }

    private void startAnimationSystem() {
        animTimer = new Timer(100, e -> {
            if (currentFrames != null && !currentFrames.isEmpty()) {
                currentFrameIndex = (currentFrameIndex + 1) % currentFrames.size();
                petLabel.setIcon(currentFrames.get(currentFrameIndex));
            }
        });
        animTimer.start();

        idleTimer = new Timer(0, e -> {
            if (!isPlayingAction) playRandomIdle();
            int delay = 3000 + random.nextInt(2000);
            ((Timer) e.getSource()).setInitialDelay(delay);
            ((Timer) e.getSource()).restart();
        });
        idleTimer.setInitialDelay(0);
        idleTimer.start();
    }

    private void playRandomIdle() {
        if (idleAnimations.isEmpty()) return;
        currentFrames = idleAnimations.get(random.nextInt(idleAnimations.size()));
        currentFrameIndex = 0;
    }

    private void playActionOnce(ArrayList<ImageIcon> frames, Runnable onComplete) {
        if (frames == null || frames.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        stopActionTimer();//快速连点时，先停掉上一个动画，避免多个定时器叠加导致加速
        isPlayingAction = true;
        animTimer.stop();
        currentFrames = frames;
        currentFrameIndex = 0;
        petLabel.setIcon(currentFrames.get(0));
        actionTimer = new Timer(100, e -> {
            currentFrameIndex++;
            if (currentFrameIndex >= currentFrames.size()) {
                actionTimer.stop();
                actionTimer = null;
                isPlayingAction = false;
                animTimer.start();
                if (onComplete != null) onComplete.run();
            } else {
                petLabel.setIcon(currentFrames.get(currentFrameIndex));
            }
        });
        actionTimer.start();
    }

    private void playUpgradeAnimation(ArrayList<ImageIcon> frames) {
        if (frames == null || frames.isEmpty()) {
            playRandomIdle();
            return;
        }
        stopActionTimer();//升级动画开始前，确保没有残留的旧动画定时器
        isPlayingAction = true;
        animTimer.stop();
        currentFrames = frames;
        currentFrameIndex = 0;
        petLabel.setIcon(currentFrames.get(0));

        actionTimer = new Timer(100, e -> {
            currentFrameIndex = (currentFrameIndex + 1) % currentFrames.size();
            petLabel.setIcon(currentFrames.get(currentFrameIndex));
        });
        actionTimer.start();

        upgradeStopTimer = new Timer(3000, e -> {
            stopActionTimer();
            isPlayingAction = false;
            animTimer.start();
            playRandomIdle();
        });
        upgradeStopTimer.setRepeats(false);
        upgradeStopTimer.start();
    }

    private void playLoopTimes(ArrayList<ImageIcon> frames, int times, Runnable onComplete) {
        if (frames == null || frames.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        stopActionTimer();//快速连点时，先停掉上一个动画
        isPlayingAction = true;
        animTimer.stop();
        currentFrames = frames;
        currentFrameIndex = 0;
        final int[] loopCount = {0};
        petLabel.setIcon(currentFrames.get(0));

        actionTimer = new Timer(100, e -> {
            currentFrameIndex++;
            if (currentFrameIndex >= currentFrames.size()) {
                currentFrameIndex = 0;
                loopCount[0]++;
                if (loopCount[0] >= times) {
                    actionTimer.stop();
                    actionTimer = null;
                    isPlayingAction = false;
                    animTimer.start();
                    if (onComplete != null) onComplete.run();
                    return;
                }
            }
            petLabel.setIcon(currentFrames.get(currentFrameIndex));
        });
        actionTimer.start();
    }

    //停掉当前动作/循环/升级动画的定时器，防止多个定时器叠加
    private void stopActionTimer() {
        if (actionTimer != null) {
            actionTimer.stop();
            actionTimer = null;
        }
        if (upgradeStopTimer != null) {
            upgradeStopTimer.stop();
            upgradeStopTimer = null;
        }
    }

    public void playHappy(int levelBefore) {
        showFloatText("+1");
        updateLevelBar();
        playActionOnce(happyAnim, () -> finishAction(levelBefore));
    }

    public void playFeed(int levelBefore) {
        showFloatText("+3");
        updateLevelBar();
        playLoopTimes(feedAnim, 3, () -> finishAction(levelBefore));
    }

    public void playPlay(int levelBefore) {
        showFloatText("+5");
        updateLevelBar();
        playLoopTimes(playAnim, 3, () -> finishAction(levelBefore));
    }

    //互动动画播完后的收尾：等级提升则播升级动画，否则回到待机
    private void finishAction(int levelBefore) {
        if (pet.getLevel() > levelBefore) {
            playUpgrade();
        } else {
            playRandomIdle();
        }
    }

    //播放下一次升级动画（等级与亲密度由Pet模型统一管理，这里只负责展示）
    public void playUpgrade() {
        int upgradeIdx = pet.getLevel() - 2;
        if (upgradeIdx >= 0 && upgradeIdx < upgradeAnimations.size()) {
            playUpgradeAnimation(upgradeAnimations.get(upgradeIdx));
        } else {
            playRandomIdle();
        }
    }

    private void updateLevelBar() {
        levelLabel.setText("LV" + pet.getLevel());
        intimacyBar.setMaximum(pet.getMaxIntimacy());
        intimacyBar.setValue(pet.getIntimacy());
        intimacyBar.setString(pet.getIntimacy() + "/" + pet.getMaxIntimacy());
    }

    private void showFloatText(String text) {
        floatLabel.setText(text);
        floatLabel.setVisible(true);
        final int[] y = {80};
        Timer floatTimer = new Timer(30, null);
        floatTimer.addActionListener(e -> {
            y[0] -= 2;
            floatLabel.setLocation(0, y[0]);
            if (y[0] < 50) {
                floatTimer.stop();
                floatLabel.setVisible(false);
                floatLabel.setLocation(0, 80);
            }
        });
        floatTimer.start();
    }

    public void showEffect(String text) {
        showFloatText(text);
    }

    public void refreshIntimacy(int level, int current, int max) {
        updateLevelBar();
    }

    public void playIdleAnimation() {
        playRandomIdle();
    }

    public JMenuItem getInteractMenuItem() { return interactMenuItem; }
    public JMenuItem getTaskMenuItem() { return taskMenuItem; }
    public JMenuItem getSettingMenuItem() { return settingMenuItem; }
    public Pet getPet() { return pet; }
}
