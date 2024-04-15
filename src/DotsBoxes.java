/**
 * 缺少的模块(需要自己写的）：
 * 1.判断及显示领地，连续行走模块------------------>秦
 * 2.改变游戏点阵大小----------------------------->秦
 * 3.机器对战模式--------------------------------->王
 * 4.人机对战模式（玩家有先动选择）---------------->王
 * 5.人人对战模式 -------------------------------->王 & 秦
 * 6.游戏结束判定及显示模块----------------------->秦
 * 7.非法输入的handle----------------------------->秦
 * ---------------------------------
 * 8.GUI（15分）--------------------------------->秦
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;

public class DotsBoxes {

    // 数据域
    private ArrayList<Edge> edges = new ArrayList<>(); // 线集
    private ArrayList<Dot> dots = new ArrayList<>(); // 点集
    private ArrayList<DotForCredit> dots_for_credit = new ArrayList<>(); // 显示领地的点集
    private Color currentColor = Color.RED; // 初始颜色为红

    // 画布初始化
    public DotsBoxes(int canvasWidth, int canvasHeight, int size) {
        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 400);
        StdDraw.setYscale(0, 400);
        initialize(size);
    }

    // 点阵初始化
    public void initialize(int size) {

        int x0, y0; // 不必初始化
        if (size == 3)
            x0 = y0 = 110;
        else if (size == 4)
            x0 = y0 = 80;
        else if (size == 5)
            x0 = y0 = 40;
        else {
            System.out.println("Invalid size.");
            return; // 非法输入引起结束，不再执行后续语句
        }

        int big = 80, small = 7; // big在前是横线，big在后是竖线
        int big_correction = 78;

        for (int i = 0, x1 = x0, y1 = y0, x2 = x0, y2 = y0; i < size; i++) { // 添加点
            for (int j = 0; j < size; j++) {
                dots.add(new Dot(x0 + i * big + 3, y0 + j * big + 3, 10));
            }
        }

        // 添加edge
        for (int i = 0, x = x0, y = y0; i < size - 1; i++) { // 添加与计分点下标相同的edge
            for (int j = 0; j < size - 1; j++) { // 由下到上添加横线（n-1个），再换列添加（x += big）
                edges.add(new Edge(x, y + j * big, big_correction, small));
            }
            x += big;
        }
        for (int i = 0, x = x0, y = y0; i < size; i++) {
            for (int j = 0; j < size - 1; j++) { // 由上到下添加竖线
                edges.add(new Edge(x, y + j * big, small, big_correction));
            }
            x += big;
        }
        for (int i = 0, x = x0, y = y0 + (size - 1) * big; i < size - 1; i++) { // 最后的n-1条横线由左到右添加
            edges.add(new Edge(x + i * big, y, big_correction, small));
        }

        /* 添加计分点 */
        for (int i = 0; i < size - 1; i++) {
            for (int j = 0; j < size - 1; j++) { // 计分点先由下到上添加一列，再换列添加（x0 + i*big + big/2）
                dots_for_credit.add(new DotForCredit(x0 + i * big + big / 2 + 3, y0 + j * big + big / 2 + 3, 25));
            }
        }
    }

    // 点阵刷新
    public boolean[] updateForMan(int size) {

        Point mousePoint = new Point((int) StdDraw.mouseX(), (int) StdDraw.mouseY()); // 鼠标位置获取

        boolean isMousePressed = StdDraw.isMousePressed(); // 鼠标动作获取（点击）
        boolean foundEdge = false; // 初始设定为不可见？？？
        boolean[] array = new boolean[2];

        for (Edge edge : edges) {

            if (edge.isFree()) { // 若状态可变？？即可由不可见到可见
                if (!foundEdge && edge.getBounds().contains(mousePoint)) { // 确定鼠标在线内

                    edge.setColor(currentColor); // 挨到就上色
                    edge.setVisible(true); // 暂时可见

                    if (isMousePressed) { // 光标指向线，且进行了点击

                        edge.setFree(false);
                        if (!judgeMatrix(size)) { // 如果不得分
                            array[0] = true;
                            array[1] = false; // 已经上色，但不继续行走
                            currentColor = currentColor == Color.RED ? Color.BLUE : Color.RED; // 若不得分则变色
                        } else { // 若得分，则改变array的值
                            array[0] = true;
                            array[1] = true; // 已经上色，而且继续行走
                        }
                    } else { // 没有点击上色
                        array[0] = false;
                        array[1] = false; // 没有上色，(也不继续行走,没有上色就免谈)
                    }

                    foundEdge = true; // Avoid multiple selections. 显示线
                } else {
                    edge.setVisible(false); // 否则（鼠标不指向线）则不显示
                }
            }
        }
        return array;
    }

    // 机器行走
    public boolean updateForMachine(int size) {

        try {
            Thread.sleep(1100); // 机器暂停，以防太快（先sleep）
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int number_edge = 2 * size * (size - 1);

        boolean is_coninue = false;

        for (;;) { // 一直循环直到改变return为止

            int randomNumber = (int) (Math.random() * number_edge); // 生成随机整数

            for (; randomNumber < edges.size(); randomNumber++) {
                if (edges.get(randomNumber).isFree()) { // 若状态可变？？即可由不可见到可见

                    edges.get(randomNumber).setColor(currentColor);
                    edges.get(randomNumber).setVisible(true); // 可见
                    edges.get(randomNumber).setFree(false); // 线已被显示，不可变化（若可反悔，可再次为真）

                    is_coninue = judgeMatrix(size);
                    if (is_coninue)
                        return true; // 改变后得分返回true,且阻止颜色改变
                    else {
                        currentColor = currentColor == Color.RED ? Color.BLUE : Color.RED;
                        return false; // 改变后不得分返回false
                    }
                }
            }
        }
    }

    // 暂停10毫秒
    public void pauseForMan() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 点阵显示
    public void paint() {
        StdDraw.clear();
        for (Edge edge : edges) {
            edge.paint();
        }
        for (Dot dot : dots) {
            dot.paint();
        }
        for (DotForCredit dotForCredit : dots_for_credit) {
            dotForCredit.paint();
        }
        StdDraw.show();
    }

    // 得分判定
    public boolean judgeMatrix(int size) {

        boolean isContinue = false; // 只能变成true,不能再次变成false
        boolean getCredit; // 是否得分，若得分，isContinue == true，且给计分点上色

        if (size == 3) { // (3-1)^2，即4个计分点，12条边
            for (int i = 0; i < 4; i++) {
                /* i是从0计数 !!!! */
                if ((i + 1) % 2 == 0) { // 从一计数是2的倍数
                    if (i == 1) {
                        getCredit = edges.get(i).isShown() && edges.get(i + 4).isShown() && edges.get(i + 6).isShown()
                                && edges.get(10).isShown();
                        // 都shown即为getCredit真
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    } else { // i == 3
                        getCredit = edges.get(i).isShown() && edges.get(i + 4).isShown() && edges.get(i + 6).isShown()
                                && edges.get(11).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    }
                } else { // i == 0,2
                    getCredit = edges.get(i).isShown() && edges.get(i + 4).isShown() && edges.get(i + 6).isShown()
                            && edges.get(i + 1).isShown();
                    dots_for_credit.get(i).setColor(getCredit, currentColor);
                    isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                }
            }
        } else if (size == 4) { // 9个计分点，24条边

            for (int i = 0; i < 9; i++) {
                /* i是从0计数 !!!! */
                if ((i + 1) % 3 == 0) {
                    if (i == 2) {
                        getCredit = edges.get(i).isShown() && edges.get(i + 9).isShown() && edges.get(i + 12).isShown()
                                && edges.get(21).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    } else if (i == 5) {
                        getCredit = edges.get(i).isShown() && edges.get(i + 9).isShown() && edges.get(i + 12).isShown()
                                && edges.get(22).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    } else { // i == 8
                        getCredit = edges.get(i).isShown() && edges.get(i + 9).isShown() && edges.get(i + 12).isShown()
                                && edges.get(23).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    }
                } else {
                    getCredit = edges.get(i).isShown() && edges.get(i + 9).isShown() && edges.get(i + 12).isShown()
                            && edges.get(i + 1).isShown();
                    dots_for_credit.get(i).setColor(getCredit, currentColor);
                    isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                }
            }
        } else { // size == 5, 16个计分点, 40条边
            /* i是从0计数 !!!! */
            for (int i = 0; i < 16; i++) {
                if ((i + 1) % 4 == 0) {
                    if (i == 3) {
                        getCredit = edges.get(i).isShown() && edges.get(i + 16).isShown() && edges.get(i + 20).isShown()
                                && edges.get(36).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    } else if (i == 7) {
                        getCredit = edges.get(i).isShown() && edges.get(i + 16).isShown() && edges.get(i + 20).isShown()
                                && edges.get(37).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    } else if (i == 11) {
                        getCredit = edges.get(i).isShown() && edges.get(i + 16).isShown() && edges.get(i + 20).isShown()
                                && edges.get(38).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    } else { // i == 15
                        getCredit = edges.get(i).isShown() && edges.get(i + 16).isShown() && edges.get(i + 20).isShown()
                                && edges.get(39).isShown();
                        dots_for_credit.get(i).setColor(getCredit, currentColor);
                        isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                    }
                } else {
                    getCredit = edges.get(i).isShown() && edges.get(i + 16).isShown() && edges.get(i + 20).isShown()
                            && edges.get(i + 1).isShown();
                    dots_for_credit.get(i).setColor(getCredit, currentColor);
                    isContinue = setIsContinue(dots_for_credit.get(i), getCredit, isContinue);
                }
            }
        }
        return isContinue;
    }

    // 是否连续行走
    public boolean setIsContinue(DotForCredit dotForCredit, boolean getCredit, boolean isContinue) { // 每个技术点都只能对颜色的改变work1次，然后就作废

        if (dotForCredit.isWork()) { // 如果此计数点work,即第一次被上色和显示
            if (getCredit) { // 如果得分
                isContinue = true;
                dotForCredit.setWork(); // 关闭work
            }
        }
        return isContinue; // 两者都改变赋值为true,否则返回原值
    }

    // 游戏结束判定
    public boolean isOver() {

        boolean game_over = true; // 先假设游戏结束，只能变成false，不能再变成true
        for (Edge edge : this.edges) {
            if (!edge.isShown())
                game_over = false; // 只要有一个edge没有shown，游戏就不会结束
        }
        return game_over;
    }

    // 结束后分数统计
    public int getCredit(Color color) {

        int credit = 0;
        for (DotForCredit a : this.dots_for_credit) {
            if (a.getColor() == color)
                credit++;
        }
        return credit;
    }

    // 游戏结束显示
    private static void gameOver(DotsBoxes game, String player1, String player2) {

        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("Game over");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 添加 "Hello World" 标签
        String display1;
        if (game.getCredit(Color.BLUE) == game.getCredit(Color.RED))
            display1 = String.format("Game over, no winner");
        else {
            String winner = game.getCredit(Color.RED) > game.getCredit(Color.BLUE) ? player1 : player2;
            display1 = String.format("Game over! %s gets %d credits. %s gets %d credits. Winner is %s.", player1,
                    game.getCredit(Color.RED), player2, game.getCredit(Color.BLUE), winner);
        }
        JLabel label1 = new JLabel(display1);
        label1.setFont(new Font("宋体", Font.BOLD, 25)); // 设置字体
        frame.getContentPane().add(label1);

        // 显示窗口
        frame.pack();
        frame.setVisible(true);
    }

    // PVP
    public void PVP(int size, DotsBoxes game) {

        for (; !game.isOver();) {
            game.updateForMan(size);
            game.paint();
            game.pauseForMan();
        }
    }

    // PVM2:人先手
    public void PVM1(int size, DotsBoxes game) { // 人先走

        boolean[] judge = { false, false }; // 一开始要进入循环
        boolean machineContinue = true; // 进入循环

        for (;;) { // 大循环

            // 人循环,一次上色循环
            for (; !judge[0];) { // 没有点击上色就循环
                judge = game.updateForMan(size);
                game.paint();
                game.pauseForMan();
                if (game.isOver())
                    return; // over直接返回主方法，不over无操作
            }

            // 电脑循环
            if (!judge[1]) { // 如果人继续行走就跳过机器循环
                for (; machineContinue;) {
                    machineContinue = game.updateForMachine(size);
                    game.paint();
                    if (game.isOver())
                        return;
                }
            }

            judge[0] = false; // 设鼠标未点击上色(使人可以走下一步)
            machineContinue = true; // 设机器可走(下一步时)
        }
    }

    // PVM2:电脑先手
    public void PVM2(int size, DotsBoxes game) { // 电脑先走

        boolean[] judge = { false, false }; // 一开始要进入循环
        boolean machineContinue = true; // 进入循环

        for (;;) { // 大循环

            // 电脑循环
            if (!judge[1]) { // 如果人继续行走就跳过机器循环
                for (; machineContinue;) {
                    machineContinue = game.updateForMachine(size);
                    game.paint();
                    if (game.isOver())
                        return;
                }
            }
            machineContinue = true; // 设机器可走(下一步时)

            // 人循环,一次上色循环
            for (; !judge[0];) { // 没有点击上色就进入循环
                judge = game.updateForMan(size);
                game.paint();
                game.pauseForMan();
                if (game.isOver())
                    return; // over直接返回主方法，不over无操作
            }
            judge[0] = false; // 设鼠标未点击上色(使机器可以走下一步)
        }
    }

    // MVM
    public void MVM(int size, DotsBoxes game) {

        for (; !game.isOver();) {
            game.updateForMachine(size);
            game.paint();
        }
    }

    // 主方法
    public static void main(String[] args) {

        Scanner reader = new Scanner(System.in);

        System.out.print("please enter the size of the game(3, 4 or 5):");
        String enter = reader.nextLine().trim();
        if (!enter.matches("[345]")) {
            System.out.println("Invalid size!");
            return;
        }
        int size = Integer.valueOf(enter);

        System.out.println("A Player VS Player");
        System.out.println("B Player VS Machine");
        System.out.println("C Machine VS Machine");
        System.out.print("Please choose mode of the game:");
        String mode = reader.nextLine(); // 得到格式
        String player1, player2;

        if (mode.equalsIgnoreCase("a")) {
            System.out.print("Please enter the name for player 1:"); // 红方
            player1 = reader.nextLine().trim();
            System.out.print("Please enter the name for player 2:");
            player2 = reader.nextLine().trim();
            if (player1.equals(player2)) {
                System.out.println("Two players can't have the same name!");
                return;
            }
            // game.PVP(size, game);
        } else if (mode.equalsIgnoreCase("b")) {
            System.out.print("Do you want play first(yes or no):");
            String choice = reader.nextLine().trim();
            if (choice.equals("yes")) {
                System.out.print("Please enter your name:");
                player1 = reader.nextLine().trim(); // 红方总是player1
                player2 = "Machine";

                mode = "b1";
                // game.PVM1(size, game);
            } else if (choice.equals("no")) {
                System.out.print("Please enter your name:");
                player2 = reader.nextLine().trim();
                player1 = "Machine"; // 红方总是player1

                mode = "b2";
                // game.PVM2(size, game);
            } else {
                System.out.println("please input yes or no!");
                return;
            }
        } else if (mode.equalsIgnoreCase("c")) {
            player1 = "Machine1(red)"; // 红方总是player1
            player2 = "Machine2(blue)";
            // game.MVM(size, game);
        } else {
            System.out.println("Invalid mode!");
            return;
        }

        DotsBoxes game = new DotsBoxes(500, 500, size); // 新建画布

        if (mode.equalsIgnoreCase("a"))
            game.PVP(size, game);
        else if (mode.equalsIgnoreCase("c"))
            game.MVM(size, game);
        else if (mode.equalsIgnoreCase("b1"))
            game.PVM1(size, game);
        else if (mode.equalsIgnoreCase("b2"))
            game.PVM2(size, game);

        gameOver(game, player1, player2);
    }
}
