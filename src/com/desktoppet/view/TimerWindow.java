package com.desktoppet.view;

import com.desktoppet.model.Task;

import com.desktoppet.system.TaskSystem;
import com.desktoppet.system.TimerSystem;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;


/**
 * 计时器窗口
 * 负责人：
 * 功能：
 *     从任务界面点击开始按钮打开
 *     顶部灰色标题栏（计时器）
 *     任务名字圆角输入框/标签
 *     中央大字号计时时间（24:00:00）
 *     三个操作按钮：开始、暂停、结束
 *     底部本次专注 + 累计专注双栏显示
 *     结束后可选择是否生成自习室（功能待定）
 */
public class TimerWindow extends JFrame {

    //--------------组件设计-------------------
    public static  int globalSeconds=0;
    private Task task;                   //当前计时的任务
    private TimerSystem timerSystem;     //(新增)定义业务层对象
    private TaskSystem taskSystem;       //(新增)定义业务层对象
    private JLabel taskNameLabel;        //任务名字标签
    private JLabel timeLabel;            //计时时间标签
    private JButton startBtn;            //开始按钮
    private JButton pauseBtn;            //暂停按钮
    private JButton stopBtn;             //结束按钮
    private JLabel currentFocusLabel;    //本次专注时长标签
    private JLabel totalFocusLabel;      //累计专注时长标签
    private JLabel currentFocusTextLabel;//(新增)本次专注标签
    private JLabel totalFocusTextLabel;  //(新增)累计专注标签
    //--------------组件设计-------------------


    //--------------service-------------------
    //service:初始化计时器窗口
    /*
    负责人：
    功能：
        设置窗口大小、位置、标题
        初始化灰色标题栏
        初始化任务名字圆角显示框
        初始化大字号计时时间显示
        初始化开始、暂停、结束三个按钮
        初始化本次专注和累计专注双栏显示
        为按钮添加事件监听
    参数：task 要计时的任务
    返回值：void
     */

    //初始化里我新传了两个参数
    public TimerWindow(Task task,TimerSystem timerSystem, TaskSystem taskSystem){
        super("计时器");
        this.task = task;
        this.timerSystem = timerSystem;
        this.taskSystem = taskSystem;
        setSize(450,600);  //窗口大小
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);  //位置居中
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // --- 1. 初始化顶部灰色标题栏 ---
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(220, 220, 220)); // 灰色背景
        titlePanel.setPreferredSize(new Dimension(0, 60));
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JLabel titleLabel = new JLabel("计时器");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 40));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);
        this.add(titlePanel, BorderLayout.NORTH); // 放在窗口最上方

        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setBackground(Color.WHITE);
        centerContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // --- 2. 初始化任务名字圆角显示框 ---
        taskNameLabel = new JLabel(task != null ? task.getName() : "任务名字");
        taskNameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 28)); //字体
        taskNameLabel.setHorizontalAlignment(SwingConstants.CENTER); //居中
        taskNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        taskNameLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        // 设置圆角边框

        taskNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Border roundedBorder = BorderFactory.createLineBorder(new Color(180, 180, 180), 1); //圆角外框
        javax.swing.border.EmptyBorder padding = new javax.swing.border.EmptyBorder(8, 10, 8, 10); //内边距
        taskNameLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true), // true = 圆角
                padding
        ));
        centerContent.add(taskNameLabel);
        centerContent.add(Box.createVerticalStrut(40));


        // --- 3. 初始化中央大字号计时时间显示 ---
        timeLabel = new JLabel("00:00:00");
        timeLabel.setFont(new Font("Arial", Font.BOLD, 70)); // 大字号加粗
        timeLabel.setForeground(Color.BLACK);
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerContent.add(timeLabel);
        centerContent.add(Box.createVerticalStrut(40)); // 与下方按钮的间距


        // --- 4. 初始化开始、暂停、结束三个按钮 ---
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); // 居中，间距20
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        startBtn=createFlatButton("开始", new Color(144, 199, 87));
        pauseBtn=createFlatButton("暂停", new Color(237, 177, 61));
        stopBtn=createFlatButton("结束", new Color(217, 83, 98));

        Dimension btnSize = new Dimension(90, 40);
        startBtn.setPreferredSize(btnSize);
        pauseBtn.setPreferredSize(btnSize);
        stopBtn.setPreferredSize(btnSize);
        // 添加事件监听
        startBtn.addActionListener(e-> startTimer());
        pauseBtn.addActionListener(e -> togglePause());
        stopBtn.addActionListener(e -> stopTimer());
        btnPanel.add(startBtn);
        btnPanel.add(pauseBtn);
        btnPanel.add(stopBtn);

        centerContent.add(btnPanel);
        centerContent.add(Box.createVerticalStrut(40));

        // --- 5. 初始化本次专注和累计专注双栏显示 ---
        //创建外层容器,1行2列
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 1, 1));
        statsPanel.setBackground(Color.WHITE);

        // 蓝色边框
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color(66, 133, 244), 2));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        statsPanel.setPreferredSize(new Dimension(380, 80));
        //字体
        Font descFont = new Font("微软雅黑", Font.ITALIC, 14); // 文字：12号斜体
        Font timeFont = new Font("Arial", Font.BOLD, 18);     // 时间：14号常规
        //创建左侧面板（本次专注）
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // 垂直布局
        leftPanel.setBackground(Color.WHITE);

        currentFocusLabel = new JLabel("00:00"); //本次计时时间
        currentFocusLabel.setFont(timeFont);
        currentFocusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        currentFocusTextLabel = new JLabel("本次专注"); //本次专注
        currentFocusTextLabel.setFont(descFont);
        currentFocusTextLabel.setForeground(Color.DARK_GRAY);
        currentFocusTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(currentFocusLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(currentFocusTextLabel);
        leftPanel.add(Box.createVerticalGlue());

        //创建右侧面板（累计专注）
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));//垂直布局
        rightPanel.setBackground(Color.WHITE);

        totalFocusLabel = new JLabel("00:00");//初始化累计时间
        totalFocusLabel.setFont(timeFont);
        totalFocusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalFocusTextLabel = new JLabel("累计专注");//初始化累计专注
        totalFocusTextLabel.setFont(descFont);
        totalFocusTextLabel.setForeground(Color.DARK_GRAY);
        totalFocusTextLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(totalFocusLabel);
        rightPanel.add(Box.createVerticalStrut(5));
        rightPanel.add(totalFocusTextLabel);
        rightPanel.add(Box.createVerticalGlue());

        //组装并添加到主窗口
        statsPanel.add(leftPanel);
        statsPanel.add(rightPanel);
        centerContent.add(statsPanel);
        this.add(centerContent, BorderLayout.CENTER);

        refreshTotalFocus();
        setVisible(true);

    }
    private JButton createFlatButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);       // 去掉焦点框
        btn.setBorderPainted(false);      // 去掉边框线
        btn.setContentAreaFilled(false);  // 去掉默认填充
        btn.setOpaque(true);              // 设为不透明以显示背景色
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);   // 白色文字
        btn.setFont(new Font("微软雅黑", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 鼠标变手型

        // 鼠标悬停效果：颜色变深一点
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }


    //service:开始计时
    /*
    负责人：W
    功能：
        启动定时器，每秒currentSeconds加1
        更新计时时间显示
        开始按钮置灰，暂停按钮可用
    参数：void
    返回值：void
     */
    public void startTimer(){
        //调用system层中的startTiming()，启动定时器
        if (timerSystem != null) {
            timerSystem.startTiming();
        }

        // 更新按钮状态
        startBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
        pauseBtn.setText("暂停");
        stopBtn.setEnabled(true);
    }

    //service:暂停/继续计时
    /*
    负责人：
    功能：
        暂停时停止定时器更新，按钮文字变为"继续"
        继续时恢复定时器，按钮文字变回"暂停"
    参数：void
    返回值：void
     */
    public void togglePause() {
        if (pauseBtn == null) return;
        // 获取当前按钮上的文字
        String currentText = pauseBtn.getText();
        if ("暂停".equals(currentText)) {
            // --- 用户点击了“暂停” ---
            timerSystem.pauseTiming();//调用system层中的pauseTiming()，暂停定时器
            pauseBtn.setText("继续");//更新按钮名字

        } else {
            // --- 情况 B：用户点击了“继续” ---
            timerSystem.resumeTiming();//调用system层中的resumeTiming()，恢复定时器

            pauseBtn.setText("暂停");//更新按钮名字
        }

    }

    //service:结束计时
    /*
    负责人：
    功能：
        停止定时器
        将本次计时秒数累加到任务总专注时长
        弹出对话框显示本次专注时长，询问是否生成自习室
        用户选择是则进入自习室界面，否则返回任务清单
        重置计时状态
    参数：void
    返回值：void
     */
    public void stopTimer(){
        //停止定时器
        timerSystem.stopTiming();
        //将本次计时秒数累加到任务总专注时长
        task.addFocusSeconds(timerSystem.getCurrentSeconds());
        taskSystem.saveTasks();//写回JSON，重启后专注时长不丢失
        int totalSecs = timerSystem.getCurrentSeconds();
        //设置本次专注时长格式（00：00：00）
        String msg = String.format("%02d:%02d:%02d", totalSecs / 3600,
                (totalSecs % 3600) / 60, totalSecs % 60);
        //拼接对话框内容
        String message = "<html><center>" +
                "本次专注时长：" + msg + "<br><br>" +
                "是否进入自习室？" +
                "</center></html>";

        //弹出对话框
        int choice = JOptionPane.showConfirmDialog(
                this,       // 父窗口
                message,
                "专注完成",
                JOptionPane.YES_NO_OPTION // 自动生成“是”和“否”按钮
        );
        // 处理结果
        if (choice == JOptionPane.YES_OPTION) {
            //调用system层中的openStudyRoom()，进入自习室
            timerSystem.openStudyRoom(task);
        } else {
            //调用system层中的openTaskWindow()，返回任务清单
            taskSystem.openTaskWindow();
        }
        dispose();//关闭计时器窗口
    }

    //service:更新计时显示
    /*
    负责人：
    功能：
        将秒数格式化为时:分:秒
        更新中央大字号时间显示
        更新本次专注时长显示
        每秒调用一次
    参数：seconds 当前计时秒数
    返回值：void
     */
    public void updateTimeDisplay(int seconds){
        // 1. 数学计算：将总秒数拆解为 时、分、秒
        int h = seconds / 3600;
        int m = (seconds % 3600) / 60;
        int s = seconds % 60;

        // 2. 格式化字符串：使用 %02d 保证不足两位时前面补0 (例如 01:05:09)
        String timeString = String.format("%02d:%02d:%02d", h, m, s);

        // 3. 更新中央大字号时间显示
        if (timeLabel != null) {
            timeLabel.setText(timeString);
        }

        // 4. 更新底部“本次专注”标签
        if (currentFocusLabel != null) {
            currentFocusLabel.setText(timeString);
        }
        refreshTotalFocus();
    }

    //service:刷新累计专注显示
    /*
    负责人：
    功能：
        获取任务的累计专注时长
        更新累计专注标签显示
    参数：void
    返回值：void
     */
    public void refreshTotalFocus(){
        if (task == null || totalFocusLabel == null) return;

        // 从任务对象获取累计秒数
        //调用 Task 类里的 getTotalFocusTime() 方法返回 int 类型的秒数
        int totalSeconds = task.getTotalFocusSeconds();
        //将数据存到公共变量中
        TimerWindow.globalSeconds=totalSeconds;

        // 2. 将总秒数转换为 HH:mm:ss 格式
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        String totalTimeStr = String.format("%02d:%02d:%02d", h, m, s);

        // 3. 更新界面标签
        totalFocusLabel.setText(totalTimeStr);
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public Task getTask(){ return task; }
    public JButton getStartBtn(){ return startBtn; }
    public JButton getPauseBtn(){ return pauseBtn; }
    public JButton getStopBtn(){ return stopBtn; }
    //--------------get/set-------------------
}
