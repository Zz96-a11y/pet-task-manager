package com.desktoppet.view;

import com.desktoppet.model.Pet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InteractWindow extends JFrame {
    private Pet pet;
    private JLabel levelLabel;
    private JProgressBar intimacyBar;
    private JTextArea recordArea;
    private JButton feedBtn, petBtn, playBtn, closeBtn;
    private Timer cooldownTimer;
    private int cooldownRemaining;

    public InteractWindow(Pet pet) {
        this.pet = pet;
        initUI();
    }

    private void initUI() {
        setTitle("互动面板");
        setSize(300, 420);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);

        // 顶部：等级 + 进度条
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        topPanel.setBackground(new Color(250, 248, 252));

        levelLabel = new JLabel("LV" + pet.getLevel());
        levelLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        levelLabel.setForeground(new Color(80, 80, 120));
        topPanel.add(levelLabel, BorderLayout.WEST);

        intimacyBar = new JProgressBar();
        intimacyBar.setPreferredSize(new Dimension(180, 22));
        intimacyBar.setMaximum(pet.getMaxIntimacy());
        intimacyBar.setValue(pet.getIntimacy());
        intimacyBar.setString(pet.getIntimacy() + "/" + pet.getMaxIntimacy());
        intimacyBar.setStringPainted(true);
        intimacyBar.setForeground(new Color(80, 140, 220));
        topPanel.add(intimacyBar, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 中部：互动记录
        recordArea = new JTextArea();
        recordArea.setEditable(false);
        recordArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        recordArea.setBackground(new Color(255, 255, 255));
        recordArea.setLineWrap(true);
        recordArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(recordArea);
        scrollPane.setBorder(new EmptyBorder(5, 15, 5, 15));
        add(scrollPane, BorderLayout.CENTER);

        // 底部：三个互动按钮 + 关闭
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        bottomPanel.setBackground(new Color(250, 248, 252));

        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        feedBtn = new JButton("喂食");
        feedBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        feedBtn.setBackground(new Color(255, 200, 100));
        feedBtn.setFocusPainted(false);

        petBtn = new JButton("抚摸");
        petBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        petBtn.setBackground(new Color(255, 150, 180));
        petBtn.setFocusPainted(false);

        playBtn = new JButton("玩耍");
        playBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        playBtn.setBackground(new Color(150, 200, 255));
        playBtn.setFocusPainted(false);

        btnPanel.add(feedBtn);
        btnPanel.add(petBtn);
        btnPanel.add(playBtn);
        bottomPanel.add(btnPanel);

        closeBtn = new JButton("关闭");
        closeBtn.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        closeBtn.setFocusPainted(false);
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // 窗口居中
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screen.width / 2 - 150, screen.height / 2 - 210);
    }

    public void updateIntimacy() {
        levelLabel.setText("LV" + pet.getLevel());
        intimacyBar.setMaximum(pet.getMaxIntimacy());
        intimacyBar.setValue(pet.getIntimacy());
        intimacyBar.setString(pet.getIntimacy() + "/" + pet.getMaxIntimacy());
    }

    public void refreshIntimacy() {
        updateIntimacy();
    }

    public void addRecord(String text) {
        recordArea.append(text + "\n");
        recordArea.setCaretPosition(recordArea.getDocument().getLength());
    }

    public void startPlayCooldown(int seconds) {
        cooldownRemaining = seconds;
        playBtn.setEnabled(false);
        playBtn.setText("玩耍(" + cooldownRemaining + "s)");
        if (cooldownTimer != null && cooldownTimer.isRunning()) cooldownTimer.stop();
        cooldownTimer = new Timer(1000, e -> {
            cooldownRemaining--;
            if (cooldownRemaining <= 0) {
                ((Timer) e.getSource()).stop();
                playBtn.setEnabled(true);
                playBtn.setText("玩耍");
            } else {
                playBtn.setText("玩耍(" + cooldownRemaining + "s)");
            }
        });
        cooldownTimer.start();
    }

    public JButton getFeedBtn() { return feedBtn; }
    public JButton getPetBtn() { return petBtn; }
    public JButton getPlayBtn() { return playBtn; }
    public JButton getCloseBtn() { return closeBtn; }
    public Pet getPet() { return pet; }
}
