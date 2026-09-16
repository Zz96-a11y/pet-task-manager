package com.desktoppet.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.io.File;
/**
 * 团队介绍窗口
 * 负责人：
 * 功能：
 *     从设置界面点击"团队介绍"打开
 *     展示团队成员信息（姓名、角色、职责）
 *     每个成员卡片包含头像、姓名、角色、职责描述
 *     底部返回设置按钮
 *     点击"x"或返回按钮关闭，返回设置界面
 */
public class TeamWindow extends JFrame {

    //--------------组件设计-------------------
    private JLabel titleLabel;          //标题标签（团队介绍）
    private JPanel memberListPanel;     //成员列表面板
    private JButton backBtn;            //返回设置按钮
    private JButton closeBtn;           //关闭按钮（x）
    //--------------组件设计-------------------

    // 团队成员数据 [姓名, 角色, 职责]
    private static final String[][] TEAM_MEMBERS = {
            {"岁甘",     "监督官",      "负责 Main.java，程序启动入口、全局初始化",
                    "assets/Profile Picture/岁甘.jpg"},
            {"周",       "信息官",      "负责 Pet + PetSystem，桌宠实体、亲密度系统",
                    "assets/Profile Picture/周.jpg"},
            {"小马",     "产品经理",      "负责 Task + TaskSystem，任务数据模型、增删改查逻辑",
                    "assets/Profile Picture/小马.jpg"},
            {"无效",     "技术官",    "负责 Food + Toy + TimerSystem，食物道具、玩具、计时系统",
                    "assets/Profile Picture/无效.jpg"},
            {"Continue!",  "技术官",   "负责 PetWindow + InteractWindow，桌宠主界面、互动窗口",
                    "assets/Profile Picture/Continue.jpg"},
            {"古意今闻", "技术官",      "负责 TaskWindow + TaskItemPanel + TaskEditDialog，任务清单、任务卡片、编辑弹窗",
                    "assets/Profile Picture/古意今闻.jpg"},
            {"W",        "组长","负责 TimerWindow + StudyRoomWindow，计时器界面、自习室界面",
                    "assets/Profile Picture/W.jpg"},
            {"喜洋洋",   "产品经理",    "负责 SettingWindow + TeamWindow，设置界面、团队介绍窗口",
                    "assets/Profile Picture/喜洋洋.jpg"}
    };
    // 头像配色（按成员索引循环使用）
    private static final Color[] AVATAR_COLORS = {
            new Color(52, 152, 219),   // 蓝
            new Color(46, 204, 113),   // 绿
            new Color(230, 126, 34),   // 橙
            new Color(155, 89, 182),   // 紫
            new Color(231, 76, 60)     // 红
    };


    //--------------service-------------------
    //service:初始化团队介绍窗口
    /*
    负责人：
    功能：
        设置窗口大小、位置、标题
        初始化标题栏
        初始化成员列表面板
        遍历团队成员数据，为每个成员创建成员卡片
        成员卡片包含圆形头像、姓名、角色、职责描述
        初始化返回设置按钮和关闭按钮
    参数：void
    返回值：void
     */
    public TeamWindow(){
        super("团队介绍");
        //.......
        // ===== 窗口基础设置 =====
        this.setSize(720, 720);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== 顶部标题栏 =====
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(155, 89, 182));  // 紫色，与设置界面团队介绍按钮呼应
        titlePanel.setPreferredSize(new Dimension(720, 50));

        titleLabel = new JLabel("团队介绍", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 关闭按钮（x）
        closeBtn = new JButton("×");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setBackground(new Color(155, 89, 182));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setPreferredSize(new Dimension(50, 50));
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titlePanel.add(closeBtn, BorderLayout.EAST);

        this.add(titlePanel, BorderLayout.NORTH);

        // ===== 成员列表面板（垂直排列，支持滚动）=====
        memberListPanel = new JPanel();
        memberListPanel.setLayout(new BoxLayout(memberListPanel, BoxLayout.Y_AXIS));
        memberListPanel.setBackground(new Color(245, 246, 250));
        memberListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 遍历成员数据，创建卡片
        for (int i = 0; i < TEAM_MEMBERS.length; i++) {
            JPanel card = createMemberCard(TEAM_MEMBERS[i]);
            // 卡片之间加间距
            memberListPanel.add(card);
            if (i < TEAM_MEMBERS.length - 1) {
                memberListPanel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scrollPane = new JScrollPane(memberListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPane, BorderLayout.CENTER);

        // ===== 底部返回按钮 =====
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(new Color(245, 246, 250));
        bottomPanel.setBorder(new EmptyBorder(10, 15, 15, 15));

        backBtn = new JButton("返回设置");
        backBtn.setPreferredSize(new Dimension(200, 40));
        backBtn.setBackground(new Color(52, 152, 219));  // 蓝色
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("微软雅黑", Font.BOLD, 15));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bottomPanel.add(backBtn);

        this.add(bottomPanel, BorderLayout.SOUTH);

        // ===== 事件监听 =====
        // 返回设置按钮：关闭窗口返回设置界面
        backBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // 关闭按钮（x）：关闭窗口返回设置界面
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    //service:创建成员卡片
    /*
    负责人：
    功能：
        创建一个成员卡片面板
        包含圆形头像（显示姓名最后一个字）
        包含姓名标签
        包含角色标签
        包含职责描述标签
    参数：member 成员数据 [姓名, 角色, 职责]
    返回值：JPanel 成员卡片面板
     */
    private JPanel createMemberCard(String[] member){
        //.......
        String name = member[0];
        String role = member[1];
        String duty = member[2];

        // 卡片外层
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setPreferredSize(new Dimension(440, 90));

        // 圆形头像（取姓名最后一个字做兜底，传入图片路径）
        String avatarText = name.substring(name.length() - 1);
        Color avatarColor = AVATAR_COLORS[Math.abs(name.hashCode()) % AVATAR_COLORS.length];
        String avatarPath = member.length > 3 ? member[3] : "";
        CircleAvatar avatar = new CircleAvatar(avatarText, avatarColor, avatarPath);
        avatar.setBorder(new EmptyBorder(0, 10, 0, 0));
        card.add(avatar, BorderLayout.WEST);


        // 信息区域
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 0, 10, 10));

        // 姓名 + 角色 同一行
        JPanel nameRolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        nameRolePanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        nameLabel.setForeground(new Color(44, 62, 80));
        nameRolePanel.add(nameLabel);

        JLabel roleLabel = new JLabel(role);
        roleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        roleLabel.setForeground(avatarColor);
        roleLabel.setBorder(new EmptyBorder(2, 8, 2, 8));
        roleLabel.setOpaque(true);
        roleLabel.setBackground(new Color(avatarColor.getRed(), avatarColor.getGreen(), avatarColor.getBlue(), 30));
        nameRolePanel.add(roleLabel);

        infoPanel.add(nameRolePanel);
        infoPanel.add(Box.createVerticalStrut(6));

        // 职责描述
        JLabel dutyLabel = new JLabel("职责：" + duty);
        dutyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        dutyLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(dutyLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    //--------------------service---------------------------

    //--------------内部类：圆形头像--------------
    /**
     * 圆形头像组件，支持本地图片，加载失败自动回退为文字头像
     */
    private class CircleAvatar extends JPanel {
        private String text;        // 文字头像（图片加载失败时兜底）
        private Color bgColor;
        private Image avatarImage;  // 头像图片

        public CircleAvatar(String text, Color bgColor, String imagePath) {
            this.text = text;
            this.bgColor = bgColor;
            setPreferredSize(new Dimension(60, 60));
            setMaximumSize(new Dimension(60, 60));
            setMinimumSize(new Dimension(60, 60));
            setOpaque(false);

            // 加载本地图片
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        this.avatarImage = ImageIO.read(imgFile);
                    } else {
                        System.out.println("头像文件不存在: " + imagePath + "，使用文字头像");
                        this.avatarImage = null;
                    }
                } catch (Exception e) {
                    System.out.println("头像加载失败: " + imagePath + "，错误: " + e.getMessage());
                    this.avatarImage = null;
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // 取宽高较小值作为直径，保证始终是正圆
            int size = Math.min(getWidth(), getHeight());
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if (avatarImage != null) {
                // ===== 绘制圆形图片 =====
                Image scaled = centerCropImage(avatarImage, size, size);
                // 创建圆形遮罩合成
                BufferedImage mask = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D mg = mask.createGraphics();
                mg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                mg.fillOval(0, 0, size - 1, size - 1);
                mg.setComposite(AlphaComposite.SrcIn);
                mg.drawImage(scaled, 0, 0, null);
                mg.dispose();
                g2d.drawImage(mask, x, y, null);
            } else {
                // ===== 兜底：文字头像 =====
                g2d.setColor(bgColor);
                g2d.fillOval(x, y, size - 1, size - 1);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("微软雅黑", Font.BOLD, 22));
                FontMetrics fm = g2d.getFontMetrics();
                int tx = x + (size - fm.stringWidth(text)) / 2;
                int ty = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, tx, ty);
            }
        }

        /**
         * 中心裁剪缩放：保持宽高比，裁剪图片中心区域，缩放到目标尺寸
         */
        private Image centerCropImage(Image src, int targetW, int targetH) {
            int srcW = src.getWidth(null);
            int srcH = src.getHeight(null);
            if (srcW <= 0 || srcH <= 0) return src;

            double scale = Math.max((double) targetW / srcW, (double) targetH / srcH);
            int cropW = (int) (targetW / scale);
            int cropH = (int) (targetH / scale);
            int cropX = (srcW - cropW) / 2;
            int cropY = (srcH - cropH) / 2;

            BufferedImage cropped = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, targetW, targetH, cropX, cropY, cropX + cropW, cropY + cropH, null);
            g.dispose();
            return cropped;
        }
    }
    //--------------内部类：圆形头像--------------



    //--------------get/set-------------------
    public JButton getBackBtn(){ return backBtn; }
    public JButton getCloseBtn(){ return closeBtn; }
    //--------------get/set-------------------
}
