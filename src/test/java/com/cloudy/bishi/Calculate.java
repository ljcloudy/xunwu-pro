package com.cloudy.bishi;

import java.util.Stack;

/**
 * Created by ljy_cloudy on 2018/7/7.
 */
public class Calculate {

    public enum Operation {
        BRACKET(0, "("),
        PLUS(1, "+"),
        MINUS(1, "-"),
        MULTIPLY(2, "*"),
        DIVIDE(2, "/");

        private int code;
        private String desc;

        Operation(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        static Operation getByCode(char c) {
            Operation[] values = Operation.values();
            for (int i = 0; i < values.length; i++) {
                if (values[i].getDesc().equals(new String(new char[]{c}))) {
                    return values[i];
                }
            }
            return null;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 计算
     * @param expr
     * @return
     */
    public static int calculate(String expr) {
        Stack<Integer> stack = new Stack();
        for (int i = 0; i < expr.length(); i++) {
            char charAt = expr.charAt(i);
            if (charAt >= 48 && charAt <= 57) {
                stack.push(Integer.parseInt(new String(new char[]{charAt})));
            } else {
                int num1 = stack.pop();
                int num2 = stack.pop();
                switch (charAt) {
                    case '+':
                        stack.push(num2 + num1);
                        break;
                    case '-':
                        stack.push(num2 - num1);
                        break;
                    case '*':
                        stack.push(num2 * num1);
                        break;
                    case '/':
                        stack.push(num2 / num1);
                        break;
                }
            }

        }
        return stack.pop();
    }

    /**
     * 中缀转后缀
     *
     * @param expr
     * @return
     */
    public static String convert(String expr) {
        if (expr == null || "".equals(expr)) {
            return "";
        }
        //定义输出字符串
        StringBuilder sb = new StringBuilder(expr.length());
        //操作栈
        Stack<Character> stack = new Stack();

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c >= 48 && c <= 57) {
                sb.append(c);
            } else {
                String str = new String(new char[]{c});
                if ("(".equals(str) || "".equals(str.trim())) {
                    stack.push(c);
                    continue;
                }
                if (")".equals(str)) {
                    for (int j = 0; j <= stack.size(); j++) {
                        Character top = stack.peek();
                        if (top == '(') {
                            stack.pop();
                            break;
                        }
                        sb.append(stack.pop());
                    }
                    continue;
                }
                Operation operation = Operation.getByCode(c);

                if (stack.isEmpty()) {
                    stack.push(c);
                } else {
                    Character peek = stack.peek();
                    Operation stackOperation = Operation.getByCode(peek);
                    //判断操作符优先级
                    if (operation.getCode() > stackOperation.getCode()) {
                        stack.push(c);
                    } else {
                        for (int j = 0; j <= stack.size(); j++) {
                            Character top = stack.peek();
                            Operation op1 = Operation.getByCode(top);
                            if (op1.getCode() < operation.getCode()) {
                                break;
                            }
                            sb.append(stack.pop());
                        }
                        stack.push(c);
                    }
                }

            }
        }
        //如果栈中还有元素，出栈
        for (int i = 0; i <= stack.size(); i++) {
            sb.append(stack.pop());
        }
        return sb.toString();
    }

    public static void main(String[] args) {

        String str = "3+(3-0)*2";
        String convert = convert(str);
        System.out.println(convert);
        int calculate = calculate(convert);
        System.out.println(calculate);
        System.out.println(3+(3-0)*2);
    }
}
