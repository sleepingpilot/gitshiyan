package lifeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener {
    private static GUI frame;
    private Cell cell;
    private int maxLength, maxWidth; //长和宽
    private JButton[][] nGrid; //一个按钮表示一个细胞
    private boolean[][] isSelected;  //按钮（即细胞）是否被选中
    private JButton ok, jbNowGeneration, randomInit, clearGeneration; //确定，当前代数，代数清零
    private JButton clearCell, nextGeneration, start, stop, exit; //下一代，开始繁衍，暂停，退出
    private JComboBox lengthList, widthList;//长款选择
    private boolean isRunning;
    private Thread thread;
    private boolean isDead;
    private int [][] UpdateCell;

    public static void main(String arg[]) {
        frame = new GUI("生命游戏");
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void initGUI() {
        if (maxWidth == 0) {
            maxWidth = 20;
        }
        if (maxLength == 0) {
            maxLength = 35;
        }

        cell = new Cell(maxWidth, maxLength);

        JPanel backPanel, centerPanel, bottomPanel;
        JLabel jWidth, jLength, jNowGeneration;
        backPanel = new JPanel(new BorderLayout());
        centerPanel = new JPanel(new GridLayout(maxWidth, maxLength));
        bottomPanel = new JPanel();
        this.setContentPane(backPanel);
        backPanel.add(centerPanel, "Center");
        backPanel.add(bottomPanel, "South");

        nGrid = new JButton[maxWidth][maxLength];
        isSelected = new boolean[maxWidth][maxLength];
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxLength; j++) {
                nGrid[i][j] = new JButton(""); //按钮内容置空以表示细胞
                nGrid[i][j].setBackground(Color.WHITE); //初始时所有细胞均为死
                centerPanel.add(nGrid[i][j]);
            }
        }

        jLength = new JLabel("长度：");
        lengthList = new JComboBox();
        for (int i = 3; i <= 100; i++)
            lengthList.addItem(String.valueOf(i));
        lengthList.setSelectedIndex(maxLength - 3); //设置的初始最小值为3，故减去3，下同

        jWidth = new JLabel("宽度：");
        widthList = new JComboBox();
        for (int i = 3; i <= 100; i++)
            widthList.addItem(String.valueOf(i));
        widthList.setSelectedIndex(maxWidth - 3);

        ok = new JButton("确定");
        jNowGeneration = new JLabel(" 当前代数：");
        jbNowGeneration = new JButton(""+cell.getNowGeneration());//Buttom不能直接添加int，故采用此方式
        jbNowGeneration.setEnabled(false);
        clearGeneration = new JButton("代数清零");
        randomInit = new JButton("随机初始化");
        clearCell = new JButton("细胞清零");
        start = new JButton("开始繁衍");
        nextGeneration = new JButton("下一代");
        stop = new JButton("暂停");
        exit = new JButton("退出");

        bottomPanel.add(jLength);
        bottomPanel.add(lengthList);
        bottomPanel.add(jWidth);
        bottomPanel.add(widthList);
        bottomPanel.add(ok);
        bottomPanel.add(jNowGeneration);
        bottomPanel.add(jbNowGeneration);
        bottomPanel.add(clearGeneration);
        bottomPanel.add(randomInit);
        bottomPanel.add(clearCell);
        bottomPanel.add(start);
        bottomPanel.add(nextGeneration);
        bottomPanel.add(stop);
        bottomPanel.add(exit);


        // 设置窗口
        this.setSize(1000, 650);
        this.setResizable(true);
        this.setLocationRelativeTo(null); // 让窗口在屏幕居中
        this.setVisible(true);// 将窗口设置为可见的

        // 注册监听器
        this.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ok.addActionListener(this);
        clearGeneration.addActionListener(this);
        randomInit.addActionListener(this);
        clearCell.addActionListener(this);
        nextGeneration.addActionListener(this);
        start.addActionListener(this);
        stop.addActionListener(this);
        exit.addActionListener(this);
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxLength; j++) {
                nGrid[i][j].addActionListener(this);
            }
        }
    }

    public GUI(String name) {
        super(name);
        initGUI();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == ok) { //确定
            frame.setMaxLength(lengthList.getSelectedIndex() + 3);//设置的初始最小值为3，故加上3，下同
            frame.setMaxWidth(widthList.getSelectedIndex() + 3);
            initGUI();

            cell = new Cell(getMaxWidth(), getMaxLength());

        } else if(e.getSource() == clearGeneration){ //代数清零
            cell.setNowGeneration(0);
            jbNowGeneration.setText(""+cell.getNowGeneration());//刷新当前代数
            isRunning = false;
            thread = null;
        } else if(e.getSource() == randomInit){ //随机初始化
            cell.randomCell();
            showCell();
            isRunning = false;
            thread = null;
        } else if(e.getSource() == clearCell){ //细胞清零
            cell.deleteAllCell();
            showCell();
            isRunning = false;
            thread = null;
        } else if (e.getSource() == start) { //开始
            isRunning = true;
            thread = new Thread(new Runnable() {
                public void run() {
                    while (isRunning) {
                        makeNextGeneration();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        isDead = true;
                        for(int row = 1; row <= maxWidth; row++) {
                            for (int col = 1; col <= maxLength; col++) {
                                if (cell.getGrid()[row][col] != 0) {
                                    isDead = false;
                                    break;
                                }
                            }
                            if (!isDead) {
                                break;
                            }
                        }
                        if (isDead) {
                            JOptionPane.showMessageDialog(null, "所有细胞已死亡");
                            isRunning = false;
                            thread = null;
                        }
                    }
                }
            });
            thread.start();
        } else if (e.getSource() == nextGeneration) { //下一代
            makeNextGeneration();
            isRunning = false;
            thread = null;
        } else if (e.getSource() == stop) { //暂停
            isRunning = false;
            thread = null;
        } else if (e.getSource() == exit) { //退出
            frame.dispose();
            System.exit(0);
        } else {
            int[][] grid = cell.getGrid();
            for (int i = 0; i < maxWidth; i++) {
                for (int j = 0; j < maxLength; j++) {
                    if (e.getSource() == nGrid[i][j]) {
                        isSelected[i][j] = !isSelected[i][j];
                        if (isSelected[i][j]) {
                            nGrid[i][j].setBackground(Color.BLACK);
                            grid[i + 1][j + 1] = 1;
                        } else {
                            nGrid[i][j].setBackground(Color.WHITE);
                            grid[i + 1][j + 1] = 0;
                        }
                        break;
                    }
                }
            }
            cell.setGrid(grid);
        }
    }

    private void makeNextGeneration() {
    	//UpdateCell = cell.getGrid().clone();
    //	for (int i = 0;i<UpdateCell.length;i++)
    	//{
    		//UpdateCell[i] = cell.getGrid()[i].clone();
    	//}
        cell.update();
        showCell();
        jbNowGeneration.setText(""+cell.getNowGeneration());//刷新当前代数
    }

    public void showCell(){
        int[][] grid = cell.getGrid();
        for (int i = 0; i < maxWidth; i++) {
            for (int j = 0; j < maxLength; j++) {
            	//if (grid[i+1][j+1] != UpdateCell[i+1][j+1]) {
                if (grid[i + 1][j + 1] == 1) {
                    nGrid[i][j].setBackground(Color.BLACK); //活显示黑色
                } else {
                    nGrid[i][j].setBackground(Color.WHITE); //死则显示白色
              //  }
            	}
            }
        }
    }
    
}
