package com.desktoppet.view;

import com.desktoppet.model.Task;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

/**
 * 任务界面窗口
 * 负责人：gyjw
 * 功能：
 *     从桌宠右键菜单点击"任务"打开
 *     顶部窗口栏（红黄绿圆点 + 标题"任务清单"）
 *     日期导航栏（左箭头 | 日期+周几 | 今天按钮 | 右箭头）
 *     任务列表（每条任务：复选框 + 任务名称 + 预计/状态/专注信息 + 开始/编辑/删除按钮）
 *     未完成任务名称标红，已完成任务灰色加删除线
 *     底部虚线边框的"+ 新增任务"按钮
 *     点击开始按钮打开计时器窗口
 *     点击编辑按钮打开任务编辑弹窗
 *     点击删除按钮删除任务
 *     再次左键任务或点击关闭按钮，返回桌宠主界面
 */
public class TaskWindow extends JFrame {

    //--------------组件设计-------------------
    private JPanel macBar;              //窗口栏
    private JLabel titleLabel;          //标题"任务清单"
    private JButton prevBtn;            //上一天按钮（左箭头）
    private JLabel dateLabel;           //当前日期标签（2026-08-18 周二）
    private JButton todayBtn;           //今天按钮
    private JButton nextBtn;            //下一天按钮（右箭头）
    private JPanel taskListPanel;       //任务列表面板
    private JScrollPane scrollPane;     //滚动面板
    private JButton addTaskBtn;         //新增任务按钮（虚线边框）
    private JButton closeBtn;           //关闭按钮
    private ArrayList<TaskItemPanel> taskItems; //任务项组件列表
    //--------------组件设计-------------------

    //--------------配色设计-------------------
    private static final Color MAC_BAR_BG  = new Color(0xF5F5F7);   //窗口栏背景
    private static final Color LINE_COLOR  = new Color(0xE0E0E0);   //分隔线
    private static final Color DOT_RED     = new Color(0xFF5F57);   //mac红点
    private static final Color DOT_YELLOW  = new Color(0xFEBC2E);   //mac黄点
    private static final Color DOT_GREEN   = new Color(0x28C840);   //mac绿点
    private static final Color TEXT_GRAY   = new Color(0x666666);   //正文灰
    private static final Color BTN_BORDER  = new Color(0xDDDDDD);   //按钮边框
    private static final Color EMPTY_GRAY  = new Color(0xAAAAAA);   //空状态灰
    //--------------配色设计-------------------


    //--------------service-------------------
    //service:初始化任务界面窗口
    /*
    负责人：gyjw
    功能：
        设置窗口大小、位置、无边框、不可缩放
        初始化mac风格窗口栏（红黄绿圆点+标题+关闭按钮，可拖拽）
        初始化日期导航栏（上一天/今天/下一天按钮+日期显示）
        初始化任务列表滚动面板
        初始化底部虚线边框新增任务按钮
        按钮业务监听由TaskSystem/Main通过getter绑定
    参数：void
    返回值：void
     */
    public TaskWindow(){
        super("任务清单");
        this.taskItems = new ArrayList<>();
        setLayout(new BorderLayout());
        setUndecorated(true);
        setResizable(false);
        setSize(520, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        //顶部：窗口栏 + 日期导航栏
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        initMacBar();
        topPanel.add(macBar);
        topPanel.add(initDateNav());
        add(topPanel, BorderLayout.NORTH);

        initTaskList();
        initAddTaskBtn();
    }

    //service:初始化顶部mac窗口栏
    /*
    负责人：gyjw
    功能：
        构建浅灰窗口栏：左侧红黄绿圆点、中间标题、右侧关闭按钮
        为窗口栏绑定鼠标拖拽，实现无边框窗口移动
    参数：void
    返回值：void
     */
    private void initMacBar(){
        macBar = new JPanel();
        macBar.setBackground(MAC_BAR_BG);
        macBar.setPreferredSize(new Dimension(0, 36));
        macBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
        macBar.setLayout(new BorderLayout());

        //左侧红黄绿圆点
        JPanel dotsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 11));
        dotsPanel.setOpaque(false);
        dotsPanel.add(new RoundDot(DOT_RED));
        dotsPanel.add(new RoundDot(DOT_YELLOW));
        dotsPanel.add(new RoundDot(DOT_GREEN));
        macBar.add(dotsPanel, BorderLayout.WEST);

        //中间标题
        titleLabel = new JLabel("任务清单", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        macBar.add(titleLabel, BorderLayout.CENTER);

        //右侧关闭按钮
        closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        closeBtn.setForeground(TEXT_GRAY);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.setPreferredSize(new Dimension(30, 30));
        macBar.add(closeBtn, BorderLayout.EAST);

        //窗口栏可拖拽
        attachDrag(this, macBar);
    }

    //service:初始化日期导航栏
    /*
    负责人：gyjw
    功能：
        构建白色日期导航栏：左侧上一天按钮、中间日期标签、右侧今天/下一天按钮
    参数：void
    返回值：JPanel 日期导航栏面板
     */
    private JPanel initDateNav(){
        JPanel dateNav = new JPanel();
        dateNav.setBackground(Color.WHITE);
        dateNav.setLayout(new BorderLayout());
        dateNav.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE_COLOR));
        dateNav.setPreferredSize(new Dimension(0, 40));

        prevBtn = createNavBtn("◀");
        todayBtn = createNavBtn("今天");
        nextBtn = createNavBtn("▶");

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        left.setOpaque(false);
        left.add(prevBtn);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        right.setOpaque(false);
        right.add(todayBtn);
        right.add(nextBtn);

        dateNav.add(left, BorderLayout.WEST);
        dateNav.add(dateLabel, BorderLayout.CENTER);
        dateNav.add(right, BorderLayout.EAST);
        return dateNav;
    }

    //service:初始化任务列表滚动面板
    /*
    负责人：gyjw
    功能：
        构建居中滚动区域，内部为纵向任务列表容器
    参数：void
    返回值：void
     */
    private void initTaskList(){
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    //service:初始化底部新增任务按钮
    /*
    负责人：gyjw
    功能：
        构建底部虚线边框"+ 新增任务"按钮
    参数：void
    返回值：void
     */
    private void initAddTaskBtn(){
        addTaskBtn = new JButton("＋ 新增任务");
        addTaskBtn.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        addTaskBtn.setForeground(TEXT_GRAY);
        addTaskBtn.setFocusPainted(false);
        addTaskBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addTaskBtn.setBackground(Color.WHITE);
        addTaskBtn.setBorder(new DashedBorder(new Color(0x999999), 1, 6, 4));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        bottom.add(addTaskBtn, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    //service:创建导航按钮
    private JButton createNavBtn(String text){
        JButton btn = new JButton(text);
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        btn.setForeground(TEXT_GRAY);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(BTN_BORDER));
        btn.setBackground(Color.WHITE);
        return btn;
    }

    //service:渲染任务列表
    /*
    负责人：gyjw
    功能：
        清空任务列表面板
        遍历当前日期的任务列表，为每个任务创建TaskItemPanel组件
        任务列表为空时显示"暂无任务"提示
    参数：tasks 任务列表
    返回值：void
     */
    public void renderTaskList(ArrayList<Task> tasks){
        taskListPanel.removeAll();
        taskItems.clear();
        if (tasks == null || tasks.isEmpty()) {
            JLabel emptyLabel = new JLabel("暂无任务", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
            emptyLabel.setForeground(EMPTY_GRAY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
            taskListPanel.add(emptyLabel);
        } else {
            for (Task task : tasks) {
                TaskItemPanel item = new TaskItemPanel(task);
                taskListPanel.add(item);
                taskItems.add(item);
            }
        }
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    //service:更新日期显示
    /*
    负责人：gyjw
    功能：
        更新日期标签显示（如"2026-08-18 周二"）
    参数：date 日期字符串, week 星期
    返回值：void
     */
    public void updateDateLabel(String date, String week){
        dateLabel.setText(date + "  " + week);
    }

    //service:窗口拖拽
    /*
    负责人：gyjw
    功能：
        给指定区域绑定鼠标拖拽，实现无边框窗口的整体移动
    参数：w 要移动的窗口, area 拖拽区域
    返回值：void
     */
    private static void attachDrag(Window w, JComponent area){
        final Point[] offset = {new Point()};
        area.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){ offset[0] = e.getPoint(); }
        });
        area.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
                Point p = e.getLocationOnScreen();
                w.setLocation(p.x - offset[0].x, p.y - offset[0].y);
            }
        });
    }

    //--------------------service---------------------------


    //--------------get/set-------------------
    public JButton getPrevBtn(){ return prevBtn; }
    public JButton getNextBtn(){ return nextBtn; }
    public JButton getTodayBtn(){ return todayBtn; }
    public JButton getAddTaskBtn(){ return addTaskBtn; }
    public JButton getCloseBtn(){ return closeBtn; }
    public ArrayList<TaskItemPanel> getTaskItems(){ return taskItems; }
    //--------------get/set-------------------

    //--------------内部类：mac圆点-------------------
    /*
    负责人：gyjw
    功能：
        自绘圆形装饰点（红/黄/绿）
     */
    private static class RoundDot extends JComponent {
        private final Color color;
        RoundDot(Color color){ this.color = color; setPreferredSize(new Dimension(12, 12)); }
        @Override
        protected void paintComponent(Graphics g){
            g.setColor(color);
            g.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }
    //--------------内部类：mac圆点-------------------

    //--------------内部类：虚线边框-------------------
    /*
    负责人：gyjw
    功能：
        自绘圆角虚线边框，用于"+ 新增任务"按钮
     */
    private static class DashedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int dashLength;
        private final int gapLength;
        DashedBorder(Color color, int thickness, int dashLength, int gapLength){
            this.color = color;
            this.thickness = thickness;
            this.dashLength = dashLength;
            this.gapLength = gapLength;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0,
                    new float[]{dashLength, gapLength}, 0));
            g2.drawRoundRect(x + 3, y + 3, width - 7, height - 7, 10, 10);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c){ return new Insets(6, 8, 6, 8); }
    }
    //--------------内部类：虚线边框-------------------
}
