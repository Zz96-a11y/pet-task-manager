package com.desktoppet.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import com.desktoppet.system.MusicManager;


/**
 * 设置界面窗口
 * 负责人：
 * 功能：
 *     从桌宠右键菜单点击"设置"打开
 *     顶部绿色标题栏（设置页面）
 *     退出程序按钮
 *     返回按钮
 *     团队介绍按钮
 *     点击团队介绍打开团队介绍窗口
 *     点击返回关闭设置窗口，返回桌宠主界面
 *     再次左键设置或点击关闭按钮，返回桌宠主界面
 */
public class SettingWindow extends JFrame {

    //--------------组件设计-------------------
    private JLabel titleLabel;          //标题标签（设置页面）
    private JButton exitBtn;            //退出程序按钮
    private JButton backBtn;            //返回按钮
    private JButton teamBtn;            //团队介绍按钮
    private JButton musicBtn;           //音乐开关按钮
    //--------------组件设计-------------------


    //--------------service-------------------
    //service:初始化设置界面窗口
    /*
    负责人：
    功能：
        设置窗口大小、位置、标题
        初始化绿色标题栏
        初始化退出程序按钮（红色大按钮）
        初始化返回按钮（蓝色大按钮）
        初始化团队介绍按钮（紫色大按钮）
        为按钮添加事件监听
    参数：void
    返回值：void
     */
    public SettingWindow() {
        super("设置");
        //.......
        this.setSize(400, 450);
        this.setLocationRelativeTo(null);          // 居中显示
        this.setLayout(null);                       // 绝对布局
        this.setResizable(false);                   // 禁止缩放
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== 顶部绿色标题栏 =====
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(46, 204, 113));  // 绿色
        titlePanel.setBounds(0, 0, 400, 50);
        titlePanel.setLayout(null);

        titleLabel = new JLabel("设置页面", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setBounds(0, 0, 400, 50);
        titlePanel.add(titleLabel);
        this.add(titlePanel);

        // ===== 按钮区域 =====
        int btnWidth = 220;
        int btnHeight = 45;
        int btnX = (400 - btnWidth) / 2;
        int startY = 90;
        int gap = 30;

        // 团队介绍按钮（紫色）
        teamBtn = new JButton("团队介绍");
        teamBtn.setBounds(btnX, startY, btnWidth, btnHeight);
        teamBtn.setBackground(new Color(155, 89, 182));   // 紫色
        teamBtn.setForeground(Color.WHITE);
        teamBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        teamBtn.setFocusPainted(false);
        teamBtn.setBorderPainted(false);
        this.add(teamBtn);

        // 返回按钮（蓝色）
        backBtn = new JButton("返回");
        backBtn.setBounds(btnX, startY + (btnHeight + gap), btnWidth, btnHeight);
        backBtn.setBackground(new Color(52, 152, 219));    // 蓝色
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        this.add(backBtn);
        // 退出程序按钮（红色）
        exitBtn = new JButton("退出程序");
        exitBtn.setBounds(btnX, startY + 2 * (btnHeight + gap), btnWidth, btnHeight);
        exitBtn.setBackground(new Color(231, 76, 60));     // 红色
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        exitBtn.setFocusPainted(false);
        exitBtn.setBorderPainted(false);
        this.add(exitBtn);
        // ===== 新增：音乐开关按钮（橙色）=====
        musicBtn = new JButton("音乐：开");
        musicBtn.setBounds(btnX, startY + 3 * (btnHeight + gap), btnWidth, btnHeight);
        musicBtn.setBackground(new Color(243, 156, 18));   // 橙色
        musicBtn.setForeground(Color.WHITE);
        musicBtn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        musicBtn.setFocusPainted(false);
        musicBtn.setBorderPainted(false);
        this.add(musicBtn);

        // ===== 事件监听 =====
        // 团队介绍按钮：由Main统一绑定显示共享窗口，避免重复打开
        // 返回按钮：关闭设置窗口，返回桌宠主界面
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 退出程序按钮
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitProgram();
            }
        });

        // ===== 新增：音乐开关按钮 =====
        musicBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MusicManager.getInstance().toggle();
                if (MusicManager.getInstance().isPlaying()) {
                    musicBtn.setText("音乐：开");
                    musicBtn.setBackground(new Color(243, 156, 18));  // 橙色=开
                } else {
                    musicBtn.setText("音乐：关");
                    musicBtn.setBackground(new Color(149, 165, 166)); // 灰色=关
                }
            }
        });

        // 窗口关闭按钮：关闭设置窗口返回桌宠主界面
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    //service:退出程序
    /*
    负责人：
    功能：
        弹出确认对话框"确定要退出程序吗？"
        用户确认后调用System.exit(0)退出程序
    参数：void
    返回值：void
     */
    public void exitProgram(){
        //.......
        int choice = JOptionPane.showConfirmDialog(
                this,
                "确定要退出程序吗？",
                "退出确认",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public JButton getExitBtn(){ return exitBtn; }
    public JButton getBackBtn(){ return backBtn; }
    public JButton getTeamBtn(){ return teamBtn; }
    public JButton getMusicBtn(){ return musicBtn; }
    //--------------get/set-------------------


}

