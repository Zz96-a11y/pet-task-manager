package com.desktoppet.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Random;

/**
 * 自习室窗口
 * 功能：
 *     展示自习室场景背景
 *     展示鸡汤语录（黄色字体）
 *     底部展示退出按钮
 */
public class StudyRoomWindow extends JFrame {

    //--------------组件设计-------------------
    private JLabel backgroundLabel;        // 全屏背景图片标签
    private JTextArea quoteArea;           // 鸡汤语录显示区域
    private JButton exitBtn;               // 退出自习室按钮
    private JLabel welcomeLabel;           // 左上角欢迎语标签

    // 业务数据
    private String[] wallpapers;           // 壁纸资源路径数组
    private int currentWallpaperIndex;     // 当前壁纸索引
    private String[] quotes;               // 鸡汤语录数组
    private int currentQuoteIndex;         // 当前语录索引
    private Random random;                 // 随机数生成器

    // 定时器
    private Timer wallpaperTimer;          // 壁纸定时切换器
    private Timer quoteTimer;              // 语录定时切换器

    //--------------service-------------------
    // service:初始化自习室窗口
    public StudyRoomWindow() {
        super("沉浸式自习室");

        // 1. 初始化业务数据
        this.random = new Random();
        initBusinessData(); // 初始化壁纸、语录

        // 2. 设置窗口属性
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null); // 居中显示
        this.setUndecorated(true); // 隐藏标题栏，实现沉浸式
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 禁止点X直接关闭

        // 3. 初始化界面和事件
        initUI();// 初始化界面组件
        initListeners();// 初始化事件监听
        initBusinessData();// 初始化语录
        startTimers(); // 启动定时器
    }


    //--------------被调用的初始化方法-------------------

    // 1.初始化界面组件
    private void initUI() {
        //（1） 创建背景标签
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, 1000, 700);
        this.setContentPane(backgroundLabel);
        this.setLayout(null); // 使用绝对布局

        //（2）创建半透明的语录区域
        quoteArea = new JTextArea();
        quoteArea.setFont(new Font("楷体", Font.BOLD, 30));
        // 文字颜色改为黄色
        quoteArea.setForeground(Color.YELLOW);
        quoteArea.setOpaque(false); // 背景透明
        quoteArea.setEditable(false);
        quoteArea.setBounds(150, 300, 700, 150);
        // 自动换行
        quoteArea.setLineWrap(true);           // 允许换行
        quoteArea.setWrapStyleWord(true);      // 按语义断行

        this.add(quoteArea);
       //（3）欢迎语句
        welcomeLabel = new JLabel();
        welcomeLabel.setBounds(30, 30, 400, 80);
        String welcomeText = "<html><font color='white' size='5'>" +
                "欢迎来到沉浸式自习室<br>" +
                "你的累计专注时长为：00:00:00" +
                "</font></html>";
        welcomeLabel.setText(welcomeText);

        this.add(welcomeLabel);

        //（4）创建退出按钮键
        exitBtn = new JButton("×"); // 使用乘号符号作为图标
        exitBtn.setFont(new Font("Arial", Font.BOLD, 24)); // 设置大一点的字体
        exitBtn.setForeground(new Color(255, 255, 255, 180)); // 初始颜色：半透明白色
        exitBtn.setContentAreaFilled(false); // 去掉按钮默认的灰色背景
        exitBtn.setBorderPainted(false);     // 去掉边框
        exitBtn.setFocusPainted(false);      // 去掉点击时的焦点框
        exitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 鼠标放上去变手型
        exitBtn.setBounds(960, 20, 30, 30);  // 调整位置到右上角，尺寸改小

        //（5）添加鼠标悬停变色效果（交互反馈）
        exitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitBtn.setForeground(Color.RED); // 悬停变红
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitBtn.setForeground(new Color(255, 255, 255, 180)); // 离开恢复白色
            }
        });

        this.add(exitBtn);

        //（6） 显示初始壁纸和语录
        updateBackground(wallpapers[0]);
        quoteArea.setText(quotes[0]);
    }

    // 2.绑定事件监听
    private void initListeners() {
        // 退出按钮点击
        exitBtn.addActionListener(e -> exitStudyRoom());
    }

    // 3.初始化壁纸和语录数据
    private void initBusinessData() {
        this.wallpapers = new String[]{
                "assets/study/bg_study_001.jpg",
                "assets/study/bg_study_002.jpg",
                "assets/study/bg_study_003.jpg",
                "assets/study/bg_study_004.jpg",
        };
        this.currentWallpaperIndex = 0;

        this.quotes = new String[]{
                "星光不问赶路人，时光不负有心人。",
                "种一棵树最好的时间是十年前，其次是现在。",
                "每一个优秀的人，都有一段沉默的时光。",
                "既然选择了远方，便只顾风雨兼程。",
                "看似不起眼的日复一日，会在将来的某一天，突然让你看到坚持的意义。",
                "你现在的努力，藏着你十年后的样子。",
                "别让你的野心，配不上你的才华；别让你的苦难，辜负了你的经历。",
                "生活不是等着暴风雨过去，而是学会在风雨中跳舞。",
                "每一个优秀的人，都有一段沉默的时光。",
                "你的负担将变成礼物，你受的苦将照亮你的路。"
        };
        this.currentQuoteIndex = 0;
    }


    // 4.启动定时器，更换壁纸和语录
    private void startTimers() {
        // （1）壁纸定时器：每 10 秒切换一次
        wallpaperTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newBg = getNextWallpaper();
                updateBackground(newBg);
            }
        });
        wallpaperTimer.start();

        // （2）语录定时器：每 6 秒切换一次
        quoteTimer = new Timer(6000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newQuote = getNextQuote();
                updateQuote(newQuote);
            }
        });
        quoteTimer.start();
    }

    //--------------业务逻辑-------------------

    // 1.获取下一张随机壁纸
    private String getNextWallpaper() {
        if (wallpapers.length <= 1) return wallpapers[0];
        int newIndex;
        do {
            newIndex = random.nextInt(wallpapers.length);
        } while (newIndex == currentWallpaperIndex);
        currentWallpaperIndex = newIndex;
        return wallpapers[currentWallpaperIndex];
    }

    // 2.获取下一条鸡汤语录
    private String getNextQuote() {
        currentQuoteIndex = (currentQuoteIndex + 1) % quotes.length;
        return quotes[currentQuoteIndex];
    }

    // 3.更新欢迎语中的累计专注时长
    public void updateWelcomeFocusTime(int totalSeconds) {
        String timeStr;
        if (totalSeconds > 0) {
            int h = totalSeconds / 3600;
            int m = (totalSeconds % 3600) / 60;
            int s = totalSeconds % 60;
            timeStr = String.format("%02d:%02d:%02d", h, m, s);
        } else {
            timeStr = "你今日还没有累计专注时长";
        }

        String welcomeText = "<html><font color='white' size='5'>" +
                "欢迎来到沉浸式自习室<br>" +
                "你今日的累计专注时长为：" + timeStr +
                "</font></html>";
        welcomeLabel.setText(welcomeText);
    }
    //--------------界面显示-------------------

    // 显示背景壁纸的切换
    public void updateBackground(String wallpaperPath) {
        try {
            // 【关键修改】直接使用 File 对象加载，它能识别平级目录 assets
            File imageFile = new File(wallpaperPath);

            if (imageFile.exists()) {
                ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
                // 缩放图片以适应窗口 (1000x700)
                Image img = icon.getImage().getScaledInstance(1000, 700, Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(img));
            } else {
                System.err.println("找不到壁纸文件：" + imageFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("壁纸加载异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 显示更新的语录
    public void updateQuote(String newQuote) {
        // 简单的淡入效果
        quoteArea.setForeground(new Color(255, 255, 0, 0)); // 透明黄
        Timer fadeTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quoteArea.setText(newQuote);
                quoteArea.setForeground(Color.YELLOW); // 变回黄色
                ((Timer) e.getSource()).stop();
            }
        });
        fadeTimer.setRepeats(false);
        fadeTimer.start();
    }

    //service:退出自习室
    public void exitStudyRoom() {
        // 1. 停止定时器
        if (wallpaperTimer != null) wallpaperTimer.stop();
        if (quoteTimer != null) quoteTimer.stop();

        // 2. 关闭窗口
        this.dispose();
        // 如果 TaskSystem 还没实例化，这里可能会报错，请确保调用前已赋值或处理好空指针
        // taskSystem.openTaskWindow();
    }
}
