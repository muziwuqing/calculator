package calculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.util.LinkedList;

public class calculator {
    public static void main(String[] args) {
        new CalculatorFrame("计算器");
    }
}
class CalculatorFrame extends Frame {
    TextArea display,record;//显示屏与历史记录
    String nullMessage = "尚无历史记录...";

    //组件value
    String[] value = {"%", "(", ")", "CE", "!", "sin", "cos", "<-", "1/x", "^", "√", "/",
            "7", "8", "9", "*", "4", "5", "6", "-", "1", "2", "3", "+", "-/+", "0", ".", "="};
    //构造函数，计算器入口
    public CalculatorFrame(String title){
        super(title);//计算器名称
        setMainFrame();//窗口基本设置
        setButton();//设置按钮
    }

    private void setMainFrame(){
        this.setLayout(null);//设置窗口布局为空
        this.setBounds(400,200,720,520);//设置窗口大小
        this.setResizable(false);//窗口是否可调整大小
        this.setVisible(true);//窗口可视
        //关闭窗口监听事件
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
        //设置显示屏
        display = new TextArea("0",8,52,3);//文本区域
        display.setBounds(20, 40, 490, 120);
        display.setEditable(false);
        display.setBackground(new Color(230, 230, 230));
        display.setFont(new Font("幼圆", Font.BOLD, 15));
        this.add(display);
        //设置历史记录
        record = new TextArea(nullMessage, 52, 52, 3);
        record.setBounds(510, 40, 190, 460);
        record.setEditable(false);
        record.setBackground(new Color(230,230,230));
        record.setFont(new Font("幼圆", Font.BOLD, 10));
        //清空历史记录的按钮
        JButton deleteButton = new JButton();
        deleteButton.setBounds(660, 460, 20, 20);
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            //鼠标点击，清空历史记录
            public void mousePressed(MouseEvent e){
                record.setText(nullMessage);
            }
        });
        this.add(deleteButton);
        this.add(record);
    }
    private void setButton(){
        for(int i=0; i<value.length;i++){
            addButton(value[i],20+122*(i%4), 165+48*(int)(Math.floor((i/4))));
        }
    }

    private void addButton(String val, int x, int y){
        Button button = new Button(val);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBounds(x,y,117,43);
        button.setFont(new Font("标楷体", Font.BOLD, 15));
        //增加点击事件
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String[] exceptMessages={"NaN", "Infinity"};
                Button button = (Button)e.getSource();//获取到点击的按钮对象
                String value = button.getLabel();//按钮的值
                String expression = display.getText();//获取显示屏的表达式
                int len = expression.length();//表达式的长度
                switch(value){
                    case "CE":
                        display.setText("0");
                        break;
                    case "<-":
                        boolean flag = false;
                        for(int i=0;i<exceptMessages.length;i++){
                            if(!expression.equals(exceptMessages[i]))
                                continue;
                            flag = true;
                            break;
                        }
                        if(len==1) expression="0";
                        else if(flag==true){
                            //存在无穷，NaN等数据
                            expression="0";
                        } else if(len>0 && expression.lastIndexOf(" ")!=len-1){
                            expression=expression.substring(0, len-1);
                        } else if(len>0 && expression.substring(len-4, len-3).matches("[0-9]+")){
                            expression = expression.substring(0, len-3);
                        } else if(len>0){
                            expression = expression.substring(0, len-2);
                        }
                        display.setText(expression);
                        break;
                    case "-/+":
                    case "1/x":
                        String num = "0";
                        if(expression.lastIndexOf(" ")!=len-1 && (hasNums(expression, "+")>=1
                                || hasNums(expression, "-")>=1 || hasNums(expression, "*")>=1
                                || hasNums(expression, "/")>=1)){
                            //如果输入符号时，前面有别的运算符号，即该运算不是第一个运算， 则只对靠近它的一组数运算
                            num = expression.substring(expression.lastIndexOf(" "), len);
                            double number1 = Double.parseDouble(num);
                            expression = expression.substring(0, expression.lastIndexOf(" "));
                            if(value.equals("-/+")) {
                                number1 = 0 - number1;
                            }else{
                                number1 = 1/number1;
                                BigDecimal b = new BigDecimal(number1);
                                number1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            }
                            expression += " " + number1;
                        }else{//当是第一个运算时
                            double number1 = Double.parseDouble(expression);
                            if(value.equals("-/+")) {
                                number1 = 0 - number1;
                            }else{
                                number1 = 1/number1;
                                BigDecimal b = new BigDecimal(number1);
                                number1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            }
                            expression = "" + number1;
                        }

                        display.setText(expression);
                        break;
                    case "√":
                    case "sin":
                    case "cos":
                        String number = "0";
                        if(expression.lastIndexOf(" ")==len-1 && expression.substring(len-2, len-1).equals(")")){
                            //如果输入符号的前面是右括号，则将其是对该对括号进行运算 如2 + sin ( 5 + 4 )
                            number = expression.substring(expression.lastIndexOf("("), len);
                            expression = expression.substring(0, expression.lastIndexOf("("));
                            expression = expression + value + " " + number;
//                            System.out.println("one");
                        }else if(expression.lastIndexOf(" ")!=len-1 && (hasNums(expression, "+")>=1
                                || hasNums(expression, "-")>=1 || hasNums(expression, "*")>=1
                                || hasNums(expression, "/")>=1)){
                            //如果输入符号时，前面有别的运算符号，即该运算不是第一个运算， 则只对靠近它的一组数运算
                            number = expression.substring(expression.lastIndexOf(" "), len);
                            expression = expression.substring(0, expression.lastIndexOf(" "));
                            expression += " " + value + " " + "(" + number + " " + ")" + " ";
//                            System.out.println("two");
                        }else{//当是第一个运算时
                            number = expression;
                            expression = value + " " + "(" + " " + number + " " + ")" + " ";
//                            System.out.println("three");
                        }
                        display.setText(expression);
                        break;
                    case "!":
                    case "^":
                    case "%":
                    case "+":
                    case "-":
                    case "*":
                    case "/":
                        if(expression.lastIndexOf(" ")==len-1 && !expression.substring(len-2, len-1).equals("(")
                                && !expression.substring(len-2, len-1).equals(")")){ //如果前面不是括号而是其他符号，则去掉这个符号，变成新的符号
                            expression = expression.substring(0, len-3);
                        } else if(len>1 && expression.substring(len-2, len-1).equals("(")){ //如果前面是左括号，则在符号前加上0
                            expression+="0";
                        }
                        expression+=" "+value+" ";
                        display.setText(expression);
                        break;
                    case "(":
                    case ")":
                        if(value.equals(")") && hasNums(expression, "(")<=hasNums(expression, ")")) break;
                        else if(value.equals(")") && expression.lastIndexOf(" ")==len-1 && expression.lastIndexOf("(")!=len-2){
                            //像2 + ( 5 + 的情况，自动加一个数
                            expression=autoAdd(expression);
                            len+=1;
                        }
                        if(expression.equals("0")) expression="";
                        else if(value=="(" && expression.lastIndexOf(" ")!=len-1) expression+=" *";//5*(2+3)默认加一个*号
                        if(expression.lastIndexOf(" ")==len-1) expression=expression.substring(0, len-1); //清除前面的空格
                        if(len>1 && value.equals("(") && expression.lastIndexOf(")")==len-2){
                            //像(a+b)*(c+d) 中间加个*
                            expression+=" *";
                            len+=2;
                        }
                        expression+=" "+value+" ";
                        display.setText(expression);
                        break;
                    case ".":
                        if(expression.lastIndexOf(")")!=-1 && expression.lastIndexOf(")")==len-2) expression+="* 0"+value;//括号自动变为*法
                        else if(expression.lastIndexOf(" ")==len-1) expression+="0"+value;
                        else if(expression.lastIndexOf(".")!=-1 && expression.lastIndexOf(".")>expression.lastIndexOf(" ")) break;//连续输入2个.
                        else expression+=value;
                        display.setText(expression);
                        break;
                    case "=":
                        if(hasNums(expression, "(")>hasNums(expression,")")){
                            //少了gapNum个括号
                            expression+=expression.lastIndexOf(" ")==len-1?"+ 0":" + 0";
                            int gapNum=hasNums(expression,"(")-hasNums(expression,")");
                            for(int i=0;i<gapNum;i++){
                                expression+=" ) ";
                            }

                        }
                        else if(expression.lastIndexOf(" ")==len-1 && expression.lastIndexOf(")")!=len-2){
                            //最后一个为运算符的时候，默认在表达式后加个0或1
                            expression=autoAdd(expression);
                        }
                        cal(expression);
                        break;
                    default:
                        //excptMessages
                        if(expression.substring(len-1).matches("[a-zA-Z]+")){
                            expression="0";
                        }
                        //数字
                        if(expression.equals("0")) expression="";
                        if(expression.lastIndexOf(" ")==len-1 && expression.lastIndexOf(")")==len-2) expression+="* ";
                        expression+=value;
                        display.setText(expression);
                }
            }
        });
        this.add(button);
    }

    // 计算
    private void cal(String str){
        //得到新表达式
        LinkedList<String> expre=getNewExpre(str.split(" "));
//        for(int i=0;i<expre.size();i++){
//            System.out.println(expre.get(i)+",");
//        }
//        System.out.println();
        for(var i=0;i<expre.size();i++){
            var val=expre.get(i);
            switch(val){
                case "-":
                case "+":
                case "*":
                case "/":
                case "%":
                case "^":
                    expre.set(i-2,String.valueOf(operation2(expre.get(i-2), expre.get(i-1), val)));
                    expre.remove(i-1);
                    expre.remove(i-1);
                    i-=2;
                    break;
                case "sin":
                case "cos":
                case "!":
                case "√":
//                    System.out.println("position4" + " " + val);
//                    System.out.println("position5" + " " + expre);
                    expre.set(i-1, String.valueOf(operation1(expre.get(i-1), val)));
//                    System.out.println("position6" + " " + expre);
                    expre.remove(i);
//                    System.out.println("position7" + " " + expre);
                    i-=1;
                    break;
                default:
                    break;
            }
        }

        String result = expre.get(0);
        display.setText(result);//归零
        //增加历史记录
        String recordStr = record.getText();
        if (recordStr.equals(nullMessage)) recordStr = "";
        recordStr += str + "=" + result + "\n\n";
        record.setText(recordStr);

    }


    //得到新表达式
    private LinkedList<String> getNewExpre(String[] expression){
//        for(int i=0;i<expression.length;i++){
//            System.out.println(expression[i]+",");
//        }
//        System.out.println("flag");
        LinkedList<String> symbols=new LinkedList<>();//存放符号的栈
        LinkedList<String> newExpre=new LinkedList<>();//存放新的表达式的栈
        for(int i=0;i<expression.length;i++){
            String val=expression[i];
            if(val.equals("")) continue;
//            System.out.println("position1" + " " + i +" " + symbols);
//            System.out.println("position2" + " " + i +" " + newExpre);
            switch(val){
                case "(":symbols.add(val); break;
                case ")":
                    boolean isOK=true;
                    while(isOK){
                        String _symbol=symbols.pollLast();
                        if(_symbol.equals("(")) isOK=false;
                        else newExpre.add(_symbol);
                    };
                    break;
                case "-/+":
                case "!":
                case "√":
                case "sin":
                case "cos":
                case "^":
                case "%":
                case "+":
                case "-":
                case "*":
                case "/":
                    if(symbols.size()==0){//如果符号栈为空，则入栈
                        symbols.add(val);
                    } else if(compareSymbols(val,symbols.get(symbols.size()-1))){//如果将入栈比栈顶符号优先级高，则入栈
                        symbols.add(val);
                    } else {
                        while(symbols.size()>0 && !compareSymbols(val,symbols.get(symbols.size()-1))){//否则，全部出栈，直到遇到优先级比自己小的
                            newExpre.add(symbols.pollLast());
                        }
                        symbols.add(val);
                    }
                    break;
                default:
                    newExpre.add(val);
            }
        }
        while (symbols.size()>0){
            newExpre.add(symbols.pollLast());
        }
        return newExpre;
    }

    //比较运算符优先级
    private boolean compareSymbols(String newSymbol, String existSymbol){
        if(getPriority(newSymbol)>getPriority(existSymbol)){
            return true;
        }
        else{
            return false;
        }
    }
    //运算符的优先级
    private int getPriority(String string){
        int order;
        switch(string){
            case "+":
            case "-":
                order = 1;
                break;
            case "%":
            case "*":
            case "/":
                order = 2;
                break;
            case "sin":
            case "cos":
            case "!":
            case "^":
            case "√":
                order = 3;
                break;
            default:
                order = -1;
                break;
        }
        return order;
    }

    //计算方法
    //双目运算
    private int open = 0;//判断输入是否正确
    private double operation2(String str1, String str2, String str3){
        double result=0;
        double num1,num2;
        num1 = Double.parseDouble(str1);
        num2 = Double.parseDouble(str2);
        switch (str3){
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                result = num1 / num2;
                break;
            case "%":
                result = num1 % num2;
                break;
            case "^":
                result = Math.pow(num1, num2);
                break;
        }
        return result;
    }

    //单目运算
    private double operation1(String str1, String str2){
        double result=0;
        double num1;
        num1= Double.parseDouble(str1);
        switch (str2){
            case "sin":
                num1 = Math.toRadians(num1);
                result = Math.sin(num1);
                BigDecimal b = new BigDecimal(result);
                result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "cos":
                num1 = Math.toRadians(num1);
                result = Math.cos(num1);
                BigDecimal d = new BigDecimal(result);
                result = d.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                break;
            case "!":
                result = Factorial(num1);
                break;
            case "√":
                result = Math.sqrt(num1);
                break;
        }
//        System.out.println("position3" + " " + result);
        return result;
    }

    //阶乘运算
    private double Factorial(double number){
        if(number==1) return number;
        else if(number>1){
            return number * Factorial(number-1);
        }
        return number;
    }


    //判断str中有多少个val
    private int hasNums(String str, String val){
        int nums = 0;
        while(str.indexOf(val)!=-1){
            nums+=1;
            str=str.substring(str.indexOf(val)+1);
        }
        return nums;
    }

    //表达式常见错误，自动在后面补0或1
    private String autoAdd(String expression){
        String symbol=expression.substring(expression.length()-2,expression.length()-1);
        switch(symbol){
            case "+":
            case "-":
                expression+="0";
                break;
            case "!":
                break;
            case "*":
            default:
                expression+="1";
        }
        return expression;
    }
}
